package com.digitalchina.app.bicycle.business.huaruan;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.Logger;

import com.digitalchina.web.wattle.util.encode.HexHandler;
import com.digitalchina.web.wattle.util.encryption.EncryptionHandler;
import com.digitalchina.web.wattle.util.http.RequestSync;
import com.digitalchina.web.wattle.util.json.JsonHandler;

/**
 * 类型描述：<br/>
 * 华软请求包装
 * 
 * @createTime 2016年6月20日
 * @author maiwj
 * 
 */
public class HuaRuanHttpSyncUtil {

	private final static ContentType CONTENT_TYPE = ContentType.create("application/x-www-form-urlencoded",
			Consts.UTF_8);

	private Map<String, Object> params = new HashMap<>();
	private RequestSync sync;
	private Logger log;

	/**
	 * 方法描述：<br/>
	 * 请求包装
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param sync
	 * @param log
	 * @param socketTimeout
	 * @return
	 */
	public static HuaRuanHttpSyncUtil requestWrapper(RequestSync sync, Logger log, Integer... socketTimeout) {
		HuaRuanHttpSyncUtil hr = new HuaRuanHttpSyncUtil();
		hr.sync = sync;
		hr.log = log;

		if (socketTimeout.length == 0) {
			socketTimeout = new Integer[] { 10000 };
		}

		return hr;
	}

	public HuaRuanHttpSyncUtil addHttpRequestParam(String key, Object value) {
		this.params.put(key, value);

		return this;
	}

	public HuaRuanResponse post(String url) {
		String paramsKey = null;
		String paramsJson = null;
		if (MapUtils.isEmpty(params)) {
			paramsJson = "@#$*";
			paramsKey = HexHandler.encode(EncryptionHandler.messageDigest(paramsJson, null).digestMD5());
			sync.addHttpRequestParam("key", paramsKey);
		} else {
			paramsJson = JsonHandler.toJson(params);
			sync.addHttpRequestParam("data", paramsJson);
			paramsJson = "@#" + paramsJson + "$*";
			paramsKey = HexHandler.encode(EncryptionHandler.messageDigest(paramsJson, null).digestMD5());
			sync.addHttpRequestParam("key", paramsKey);
		}
		if (log.isInfoEnabled()) {
			log.info("hua ruan async post key {}, data {}", paramsKey, paramsJson);
		}
		sync.setHttpRequestBodyType(CONTENT_TYPE);

		String r = sync.post(url).responseString(); // responseString是会正常关闭流的
		if (log.isInfoEnabled()) {
			log.info(r);
		}
		return new HuaRuanResponse(r);
	}
}
