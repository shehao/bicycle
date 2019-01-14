package com.digitalchina.app.bicycle.business;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitalchina.app.bicycle.api.IBicycleManager;
import com.digitalchina.app.bicycle.dao.BicycleBindingDao;
import com.digitalchina.app.bicycle.vo.BaiduCallResult;
import com.digitalchina.app.bicycle.vo.BicycleBindVo;
import com.digitalchina.app.bicycle.vo.BicycleListResult;
import com.digitalchina.app.bicycle.vo.BicycleVo;
import com.digitalchina.web.azalea.Session;
import com.digitalchina.web.azalea.elastricsearch.ESQuery;
import com.digitalchina.web.azalea.elastricsearch.ESQueryImpl;
import com.digitalchina.web.azalea.elastricsearch.ESSession;
import com.digitalchina.web.wattle.api.ResultPageVo;
import com.digitalchina.web.wattle.dao.DaoHandler;
import com.digitalchina.web.wattle.util.rxjava.SchedulerHandler;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func3;

/**
 * 描述：</br>
 * 自行车管理类
 * 
 * @author wukj
 * @createTime 2015年12月27日
 */
@Component
public class BicycleManager implements IBicycleManager {

	private final static Logger LOG = LogManager.getLogger(BicycleManager.class);
	
	@Autowired
	private Session searchSession;
	@Autowired
	private BicycleEmptyCountL1Cache l1Cache;
	@Autowired
	private BicycleBindingDao bicycleBindingDao;
	@Autowired
	private BaiduRemote baiduRemote;
	
	/**
	 * 方法描述：<br/>
	 * 通过查询语句从ES查询自行车列表
	 * 
	 * @createTime 2016年7月28日
	 * @author maiwj
	 *
	 * @param query ES query
	 * @return
	 */
	private List<Map<String, Object>> findBicyclesFromES (ESQuery query) {
		ResultPageVo<Map<String, Object>> esResultPage = ((ESSession) searchSession).selectListAsMap(query);
		return esResultPage.getResultList();
	}
	
	/**
	 * 方法描述：<br/>
	 * 通过百度查询距离
	 * 
	 * @createTime 2016年7月28日
	 * @author maiwj
	 *
	 * @param isWalkiing
	 * @param originLatitude
	 * @param originLongitude
	 * @param destLatitude
	 * @param destLongitude
	 * @return
	 */
	private BaiduCallResult findDirectionFromBaidu(boolean isWalkiing, Double originLatitude, Double originLongitude,
			Double destLatitude, Double destLongitude) {
		if (isWalkiing) {
			return baiduRemote.walkingRouteMatrixDesign(originLatitude, originLongitude, destLatitude, destLongitude);
		} else {
			return baiduRemote.drivingRouteMatrixDesign(originLatitude, originLongitude, destLatitude, destLongitude);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BicycleListResult findBicycles(final Double longitude, final Double latitude, Integer pageSize) {
		
		final BicycleListResult result = new BicycleListResult();
		
		// 从ES查询出自行车列表
		List<Map<String, Object>> esBicycleList = null;
		if (longitude != null && latitude != null) {
			esBicycleList = this.findBicyclesFromES(
					
					ESQueryImpl
					.select()
						.from(BicycleVo.class)
					.where(QueryBuilders.geoDistanceRangeQuery("location.point").point(latitude, longitude).from(DistanceUnit.METERS.toString(0d)).to(DistanceUnit.METERS.toString(1000)))
					.limit(1, pageSize)
					.sort(SortBuilders.geoDistanceSort("location.point").point(latitude, longitude).unit(DistanceUnit.METERS).order(SortOrder.ASC))
					.done()
					
			);
		}
		
		// 结果丰富化
		if (CollectionUtils.isEmpty(esBicycleList)) {
			
			if (longitude != null && latitude != null) {
				esBicycleList = this.findBicyclesFromES(
						
						ESQueryImpl
						.select()
							.from(BicycleVo.class)
						.limit(1, pageSize)
						.sort(SortBuilders.geoDistanceSort("location.point").point(latitude, longitude).unit(DistanceUnit.METERS).order(SortOrder.ASC))
						.asc("createTime")
						.done()
						
				);
			} else {
				esBicycleList = this.findBicyclesFromES(
						
						ESQueryImpl
						.select()
							.from(BicycleVo.class)
						.limit(1, pageSize)
						.asc("createTime")
						.done()
						
				);
			}

			result.setDirectionSearch(false);
		}
		
		
		// 数据丰富化
		if (CollectionUtils.isNotEmpty(esBicycleList)) { 
			final CountDownLatch cdl = new CountDownLatch(1);
			
			Observable.from(esBicycleList)
				.subscribeOn(SchedulerHandler.computation())
				.observeOn(SchedulerHandler.concurrent_io())
				.flatMap(new Func1<Map<String, Object>, Observable<?>>() {

					@Override
					public Observable<?> call(Map<String, Object> esBicycle) {
						
						return Observable.zip(
									Observable.just(esBicycle),
									Observable.just((String) esBicycle.get("id"))
										.observeOn(SchedulerHandler.concurrent_io())
										.flatMap(new Func1<String, Observable<String>>() {

											@Override
											public Observable<String> call(String id) {
												String emptyCount = l1Cache.getEmptyCount(id);
												
												if (StringUtils.isNotBlank(emptyCount)) {
													return Observable.just(emptyCount);
												}
												
												return Observable.just(null);
											}
										}), 
									Observable.just((Map<String, Object>) (((Map<String, Object>) (esBicycle.get("location"))).get("point")))
										.observeOn(SchedulerHandler.concurrent_io())
										.flatMap(new Func1<Map<String, Object>, Observable<BaiduCallResult>>(){

											@Override
											public Observable<BaiduCallResult> call(Map<String, Object> point) {
												BaiduCallResult callResult = findDirectionFromBaidu(result.getDirectionSearch(), latitude, longitude, (Double) point.get("lat"), (Double) point.get("lon"));
												
												if (callResult != null) {
													return Observable.just(callResult);
												}
												
												return Observable.just(null);
											}
											
										}), 
									new Func3<Map<String, Object>, String, BaiduCallResult, Object>() {

										@Override
										public Object call(Map<String, Object> esBicycle, String emptyCount,
												BaiduCallResult bcr) {
											Integer distanceInMeter = null;
											String distanceText = null;
											Integer durationInSecond = null;
											String durationText = null;
											
											if (bcr != null && bcr.isSuccess()) {
												distanceInMeter = bcr.getByPath("result[0].distance.value");
												distanceText = bcr.getByPath("result[0].distance.text");
												durationInSecond = bcr.getByPath("result[0].duration.value");
												durationText = bcr.getByPath("result[0].duration.text");
											} else {
												distanceInMeter = 0;
												distanceText = " - 米";
												durationInSecond = 0;
												durationText = " - 秒";
												if (LOG.isWarnEnabled()) {
													LOG.warn(bcr != null ? bcr.getMsg() : "百度地图发生未知异常");
												}
											}
											
											esBicycle.put("distance", distanceInMeter); // 距离，单位：米
											esBicycle.put("distanceText", distanceText);
											esBicycle.put("duration", durationInSecond); // 耗时，单位：秒
											esBicycle.put("durationText", durationText);
											esBicycle.put("emptyCount", emptyCount != null ? emptyCount : "0");
											
											return Observable.empty();
										}
									});
					}
				})
				.subscribe(new Subscriber<Object>() {

					@Override
					public void onCompleted() {
						cdl.countDown();
					}

					@Override
					public void onError(Throwable e) {
						LOG.error("", e);
						cdl.countDown();
					}

					@Override
					public void onNext(Object t) {
						// to do nothing
					}
				});
			
			try {
				cdl.await(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				LOG.error("", e);
			}
		}

		result.setBicycleList(esBicycleList);

		return result;
	}

	@Override
	public boolean doSaveCardBingding(String cardNum, String idCard, Long userId) {
		BicycleBindVo bindingVo = new BicycleBindVo(userId, cardNum, idCard);
		bindingVo.setBindingTime(System.currentTimeMillis());

		try {
			bicycleBindingDao.insertVo(bindingVo);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean doRemoveCardBinding(String cardNum, Long userId) {
		BicycleBindVo bindingVo = new BicycleBindVo(userId, cardNum);

		BicycleBindVo tempBindingVo = bicycleBindingDao.selectVo(bindingVo);
		if (tempBindingVo != null) {
			bicycleBindingDao.deleteVo(bindingVo);
			return true;
		}
		return false;
	}

	@Override
	public List<BicycleBindVo> findBindedCards(Long userId) {
		return bicycleBindingDao.selectVos(DaoHandler.param("userId", userId));
	}

}
