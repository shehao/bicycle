package com.digitalchina.app.bicycle.business.huaruan;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

import com.digitalchina.web.wattle.util.encode.HexHandler;
import com.digitalchina.web.wattle.util.encryption.EncryptionHandler;
import com.digitalchina.web.wattle.util.http.RequestAsync;
import com.digitalchina.web.wattle.util.http.RequestAsyncFutureCallback;
import com.digitalchina.web.wattle.util.json.JsonHandler;

/**
 * 类型描述：<br/>
 * 华软请求包装
 * 
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
public class HuaRuanHttpAsyncUtil {

	private final static ContentType CONTENT_TYPE = ContentType.create("application/x-www-form-urlencoded",
			Consts.UTF_8);

	private Map<String, Object> params = new HashMap<>();
	private RequestAsync async;
	private Logger log;

	/**
	 * 方法描述：<br/>
	 * 请求包装
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param async
	 * @param log
	 * @return
	 */
	public static HuaRuanHttpAsyncUtil requestWrapper(RequestAsync async, Logger log) {
		HuaRuanHttpAsyncUtil hr = new HuaRuanHttpAsyncUtil();
		hr.async = async;
		hr.log = log;
		
		return hr;
	}

	public HuaRuanHttpAsyncUtil addHttpRequestParam(String key, Object value) {
		this.params.put(key, value);

		return this;
	}

	/**
	 * 类型描述：<br/>
	 * 华软响应回调，适用于异步
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 */
	public static interface HuaRuanResponseCallback {

		void doCallback(HuaRuanResponse response);

	}

	public void post(String url, final HuaRuanResponseCallback responseCallback) {
		String paramsKey = null;
		String paramsJson = null;
		if (MapUtils.isEmpty(params)) {
			paramsJson = "@#$*";
			paramsKey = HexHandler.encode(EncryptionHandler.messageDigest(paramsJson, null).digestMD5());
			async.addHttpRequestParam("key", paramsKey);
		} else {
			paramsJson = JsonHandler.toJson(params);
			async.addHttpRequestParam("data", paramsJson);
			paramsJson = "@#" + paramsJson + "$*";
			paramsKey = HexHandler.encode(EncryptionHandler.messageDigest(paramsJson, null).digestMD5());
			async.addHttpRequestParam("key", paramsKey);
		}
		if (log.isInfoEnabled()) {
			log.info("hua ruan async post key {}, data {}", paramsKey, paramsJson);
		}
		async.setHttpRequestBodyType(CONTENT_TYPE);

		async.post(url, new RequestAsyncFutureCallback() {

			@Override
			public void failed(Exception ex) {
				log.error("", ex);
			}

			@Override
			public void completed(HttpResponse result, HttpRequestBase request) {
				try {
					String r = EntityUtils.toString(result.getEntity(), Charset.defaultCharset());
					if (log.isInfoEnabled()) {
						log.info(r);
					}
					responseCallback.doCallback(new HuaRuanResponse(r));
				} catch (Exception e) {
					log.error("", e);
				} finally {
					HttpClientUtils.closeQuietly(result);
					if (!request.isAborted()) {
						request.reset();
					}
				}
			}

			@Override
			public void cancelled() {
				// to do nothing
			}
		});
	}

}
