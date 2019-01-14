package com.digitalchina.app.bicycle.business;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitalchina.web.rose.impl.XMemcachedCache;

/**
 * 类型描述：<br/>
 * 自行车站点信息1级缓存管理
 * 
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
@Component
public class BicycleEmptyCountL1Cache {

	private final static int LIVE_TIME_SECONDS = 300; // 1级缓存，缓存时间5分钟
	private final static String KEY_PREFIX = "_bicycle_pointInfo_ll1_";

	@Autowired
	private XMemcachedCache memCache;

	@Autowired
	private BicycleEmptyCountL2Cache l2Cache;

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
	public String getEmptyCount(String id) {
		String result = memCache.get(KEY_PREFIX + id); // 如果1级缓存有数据的话，就直接返回
		if (StringUtils.isBlank(result)) { // 如果没有，将从2级缓存读取
			result = l2Cache.getEmptyCount(id);
		}
		return result;
	}

	/**
	 * 方法描述：<br/>
	 * 设置未停放数目缓存
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 * @param id
	 * @param emptyCount
	 */
	public void setEmptyCount(String id, String emptyCount) {
		memCache.set(KEY_PREFIX + id, emptyCount, LIVE_TIME_SECONDS);
	}

}
