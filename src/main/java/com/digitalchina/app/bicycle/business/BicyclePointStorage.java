package com.digitalchina.app.bicycle.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.digitalchina.app.bicycle.vo.BaiduCallResult;
import com.digitalchina.app.bicycle.vo.BicycleVo;
import com.digitalchina.web.azalea.Session;
import com.digitalchina.web.azalea.elastricsearch.ESQuery;
import com.digitalchina.web.azalea.elastricsearch.ESQueryImpl;
import com.digitalchina.web.azalea.elastricsearch.ESSession;
import com.digitalchina.web.azalea.elastricsearch.vo.ESGeoPointVo;
import com.digitalchina.web.rose.impl.XMemcachedCache;
import com.digitalchina.web.wattle.api.ResultPageVo;
import com.digitalchina.web.wattle.util.rxjava.SchedulerHandler;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 类型描述：<br/>
 * 自行车站点存储管理
 * 
 * @createTime 2016年6月23日
 * @author maiwj
 * 
 */
public class BicyclePointStorage {

	private final static Logger LOG = LogManager.getLogger(BicyclePointStorage.class);
	
	private final static String KEY = "_bicycle_pointInfo_storage_";

	@Autowired
	private Session searchSession;
	@Autowired
	private BicycleRemote remote;
	@Autowired
	private XMemcachedCache memCache;
	@Autowired
	private BaiduRemote baiduRemote;
	/**
	 * 方法描述：<br/>
	 * 定时调度初试化数据，把自行车的数据同步到ES，每天凌晨2点调用一次
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 */
	public void initDatas() {
		Object flag = memCache.get(KEY);
		if (flag != null) { // 用于控制分布式内多机器计算的问题
			return;
		} else {
			memCache.set(KEY, Boolean.TRUE);
		}

		// 1 、 获取新的数据
		List<Map<String, Object>> remoteBicyclePointInfos = remote.findRemoteBicyclePointInfos();

		if (CollectionUtils.isNotEmpty(remoteBicyclePointInfos)) {
			// 2、 获取存储中的数据
			ESQuery query = ESQueryImpl.select().from(BicycleVo.class).limit(1, 9999).done();
			ResultPageVo<Map<String, Object>> resultPage = ((ESSession) searchSession).selectListAsMap(query);
			if (resultPage == null) {
				return;
			}
			List<Map<String, Object>> storageBicyclePointInfos = resultPage.getResultList();
			
			if (CollectionUtils.isEmpty(storageBicyclePointInfos)) { // 如果存储中没有数据，将全部自行车数据导入系统存储
				
				Observable
					.from(remoteBicyclePointInfos)
					.subscribeOn(SchedulerHandler.computation())
					.observeOn(SchedulerHandler.concurrent_io())
					.flatMap(new Func1<Map<String, Object>, Observable<BicycleVo>>() {

						@Override
						public Observable<BicycleVo> call(Map<String, Object> rbpi) {
							
							return Observable.zip(
										Observable.just(rbpi),
										Observable.just(rbpi)
											.observeOn(SchedulerHandler.concurrent_io())
											.flatMap(new Func1<Map<String, Object>, Observable<ESGeoPointVo>>() {

												@Override
												public Observable<ESGeoPointVo> call(Map<String, Object> rbpi) {
													String y = (String) rbpi.get("y");
													String x = (String) rbpi.get("x");
													
													if (StringUtils.isNotBlank(y) && StringUtils.isNotBlank(x)) {
														String address = (String) rbpi.get("address"); // 从委办局返回，默认的地理位置
														Double latitude = Double.valueOf(y); // 从委办局返回，默认的纬度
														Double longitude = Double.valueOf(x); // 从委办局返回，默认的经度
														
														BaiduCallResult bcr = baiduRemote.reverseBaiduMapGEOWithoutPOIS(latitude, longitude);
														if (bcr != null && bcr.isSuccess()) { // 查询百度成功
															address = bcr.getByPath("result.formatted_address");
														} else if (LOG.isWarnEnabled()) {
															LOG.warn(bcr != null ? bcr.getMsg() : "百度地图发生未知异常");
														}
														
														return Observable.just(new ESGeoPointVo(address, latitude, longitude));
													}
													
													return Observable.just(null);
												}
											}),
										new Func2<Map<String, Object>, ESGeoPointVo, BicycleVo>() {

											@Override
											public BicycleVo call(Map<String, Object> rbpi, ESGeoPointVo egpv) {
												if (egpv != null) {
													return new BicycleVo((String) rbpi.get("id"), (String) rbpi.get("name"),
															(String) rbpi.get("pcCount"), egpv);
												}
												return null;
											}
										}
									);
						}
					})
					.subscribe(new Subscriber<BicycleVo>() {

						@Override
						public void onCompleted() {
							if (this.isUnsubscribed()) {
								this.unsubscribe();
								if (LOG.isInfoEnabled()) {
									LOG.info("BicyclePointStorage.initDatas.unsubscribed");
								}
							}
						}

						@Override
						public void onError(Throwable e) {
							LOG.error(e);
						}

						@Override
						public void onNext(BicycleVo bv) {
							if (bv != null) {
								searchSession.insert(bv);
							}
						}
					});
			} else { 
				Map<String, Object> remoteBicyclePointInfo = null;
				Iterator<Map<String, Object>> remoteBicyclePointInfosIt = null;
				Map<String, Object> storageBicyclePointInfo = null;
				Iterator<Map<String, Object>> storageBicyclePointInfosIt = null;
				String id = null;
				ESGeoPointVo egpv = null;

				// 构建新的数据的索引
				Map<String, Map<String, Object>> remoteBicyclePointInfosIndex = new HashMap<>();
				remoteBicyclePointInfosIt = remoteBicyclePointInfos.iterator();
				while (remoteBicyclePointInfosIt.hasNext()) {
					remoteBicyclePointInfo = remoteBicyclePointInfosIt.next();
					id = (String) remoteBicyclePointInfo.get("id");
					if (StringUtils.isNotBlank(id)) {
						remoteBicyclePointInfosIndex.put(id, remoteBicyclePointInfo);
					}
				}

				// 循环进行排除
				storageBicyclePointInfosIt = storageBicyclePointInfos.iterator();
				while (storageBicyclePointInfosIt.hasNext()) {
					storageBicyclePointInfo = storageBicyclePointInfosIt.next();
					id = (String) storageBicyclePointInfo.get("id");
					if (remoteBicyclePointInfosIndex.containsKey(id)
							&& StringUtils.equals((String) storageBicyclePointInfo.get("pcCount"),
									(String) remoteBicyclePointInfosIndex.get(id).get("pcCount"))) { // 只有当pcCount一样的时候才进行排除
						remoteBicyclePointInfosIndex.remove(id);
						storageBicyclePointInfosIt.remove();
					}
				}

				// 最后剩下的存储内的信息，进行删除
				storageBicyclePointInfosIt = storageBicyclePointInfos.iterator();
				while (storageBicyclePointInfosIt.hasNext()) {
					storageBicyclePointInfo = storageBicyclePointInfosIt.next();
					id = (String) storageBicyclePointInfo.get("id");
					searchSession.delete(new BicycleVo(id));
				}

				// 最后剩下的新数据内的信息，进行增加
				remoteBicyclePointInfosIt = remoteBicyclePointInfosIndex.values().iterator();
				while (remoteBicyclePointInfosIt.hasNext()) {
					remoteBicyclePointInfo = remoteBicyclePointInfosIt.next();
					if (StringUtils.isNotBlank((String) remoteBicyclePointInfo.get("x"))) {
						egpv = new ESGeoPointVo((String) remoteBicyclePointInfo.get("address"),
								Double.valueOf((String) remoteBicyclePointInfo.get("y")),
								Double.valueOf((String) remoteBicyclePointInfo.get("x")));
						searchSession.insert(new BicycleVo((String) remoteBicyclePointInfo.get("id"),
								(String) remoteBicyclePointInfo.get("name"),
								(String) remoteBicyclePointInfo.get("pcCount"), egpv));
					}
				}
			}
			
		}

		memCache.delete(KEY);
	}

}
