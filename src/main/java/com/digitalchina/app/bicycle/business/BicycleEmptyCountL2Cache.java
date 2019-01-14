package com.digitalchina.app.bicycle.business;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.digitalchina.app.bicycle.api.IBicycleManager;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanHttpAsyncUtil.HuaRuanResponseCallback;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanResponse;
import com.digitalchina.app.bicycle.vo.BicycleListResult;
import com.digitalchina.web.rose.impl.XMemcachedCache;
import com.digitalchina.web.wattle.util.rxjava.SchedulerHandler;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 类型描述：<br/>
 * 自行车站点信息2级缓存管理
 * 
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
// @Component("l2") 因为使用了config-quartz.xml的配置，所以不需要使用@Component
public class BicycleEmptyCountL2Cache {

	private final static Logger LOG = LogManager.getLogger(BicycleEmptyCountL2Cache.class);

	private final static int LIVE_TIME_SECONDS = 60 * 15; // 2级缓存，缓存时间10分钟
	private final static String KEY_PREFIX = "_bicycle_pointInfo_ll2_";

	@Autowired
	private XMemcachedCache memCache;

	@Autowired
	private BicycleEmptyCountL1Cache l1Cache;

	@Autowired
	private IBicycleManager manager;

	@Autowired
	private BicycleRemote remote;

	/**
	 * 方法描述：<br/>
	 * 异步获取远程自行车的数据
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 * @param id
	 */
	private void asyncRemoteGetBicyclePointInfo(final String id) {
		remote.findRemoteBicycle(id, new HuaRuanResponseCallback() {

			@Override
			public void doCallback(HuaRuanResponse response) {
				if (response != null && response.isResponseSuccess()) {
					String emptyCount = response.getDataByPath("data.emptyCount");
					if (LOG.isInfoEnabled()) {
						LOG.info("find bicyclePoint emptyCount is {}", emptyCount);
					}
					if (StringUtils.isNotBlank(emptyCount)) {
						l1Cache.setEmptyCount(id, emptyCount); // 回写一级缓存，更新新的数据
						memCache.set(KEY_PREFIX + id, emptyCount, LIVE_TIME_SECONDS);
					}
				} else {
					LOG.error("can not find bicycelPoint {} emptyCount, response is {} ", id, response.getRawData());
				}
			}
		});
	}

	/**
	 * 方法描述：<br/>
	 * 获取空桩数
	 * 
	 * @createTime 2016年6月23日
	 * @author maiwj
	 *
	 * @param id
	 * @return
	 */
	public String getEmptyCount(final String id) {
		String result = memCache.get(KEY_PREFIX + id);
		this.asyncRemoteGetBicyclePointInfo(id); // 异步调用一下更新接口
		if (StringUtils.isBlank(result)) {
			result = "0";
		}
		return result;
	}

	/**
	 * 方法描述：<br/>
	 * 定时调度初试化数据，每隔1分钟调用一次
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 */
	public void initDatas() {
		BicycleListResult bicycleListResult = manager.findBicycles(null, null, 9999); // 全查
		List<Map<String, Object>> bicycleList = bicycleListResult.getBicycleList();

		if (CollectionUtils.isNotEmpty(bicycleList)) {
			Observable
				.from(bicycleList)
				.subscribeOn(SchedulerHandler.computation())
				.observeOn(SchedulerHandler.computation())
				.map(new Func1<Map<String, Object>, String>() {

						@Override
						public String call(Map<String, Object> bv) {
							return (String) bv.get("id");
						}
				})
				.subscribe(new Subscriber<String>() {

					@Override
					public void onCompleted() {
						// to do nothing
					}

					@Override
					public void onError(Throwable e) {
						if (this.isUnsubscribed()) {
							this.unsubscribe();
						}
						LOG.error(e);
					}

					@Override
					public void onNext(String id) {
						asyncRemoteGetBicyclePointInfo(id);
					}
				});
		}
	}

}
