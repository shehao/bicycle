package com.digitalchina.app.bicycle.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.digitalchina.app.bicycle.business.huaruan.HuaRuanHttpSyncUtil;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanResponse;
import com.digitalchina.web.wattle.util.http.Request;

/**
 * 类型描述：<br/>
 *
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
public class BicycleRemoteManagerTest {

	protected final static Logger LOG = LogManager.getLogger(BicycleRemoteManagerTest.class);

	private final static String REMOTE_URL = "http://sz.gzyzinfo.com:9098";

	// 测试华软的接口
	@Test
	public void test001() {
		HuaRuanResponse result = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.addHttpRequestParam("cardNum", "YT6666666").addHttpRequestParam("idCard", "440902199008241250")
				.post(REMOTE_URL + "/checkBind_checkAcount.do").response();
		LOG.info("rawData is {}, crcCode is {}, isSafeResponseSuccess is {}", result.getRawData(), result.getCrcCode(),
				result.isSafeResponseSuccess());
	}

	@Test
	public void test002() {
		HuaRuanResponse result = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.post(REMOTE_URL + "/parkData_getAllParkData.do").response();
		LOG.info(result.getRawData());
	}
	
	@Test
	public void test003() {
		System.out.println(List.class.isAssignableFrom(ArrayList.class));
	}

}
