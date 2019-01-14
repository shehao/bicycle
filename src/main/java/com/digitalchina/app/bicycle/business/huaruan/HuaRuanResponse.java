package com.digitalchina.app.bicycle.business.huaruan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mvel2.MVEL;

import com.digitalchina.web.wattle.util.encryption.EncryptionHandler;
import com.digitalchina.web.wattle.util.json.JsonHandler;

/**
 * 类型描述：<br/>
 * 华软响应接口
 * 
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
public class HuaRuanResponse implements Serializable {

	private static final long serialVersionUID = -8859366610550869213L;

	private int crcCode;
	private String rawData;
	private Map<String, Object> rawDataMap;

	public HuaRuanResponse(String result) {
		if (StringUtils.isBlank(result)) {
			throw new RuntimeException("hua ruan access http interfaces has error");
		}
		result = StringUtils.trim(result);
		int pox = result.indexOf(",");
		this.crcCode = NumberUtils.toInt(result.substring(0, pox));
		this.rawData = StringUtils.trimToNull(result.substring(pox + 1, result.length()));
		try {
			this.rawDataMap = JsonHandler.fromJson(this.rawData, Map.class);
		} catch (Exception e) {
			this.rawDataMap = new HashMap<>();
		}
	}

	/**
	 * 方法描述：<br/>
	 * 是否成功响应
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @return
	 */
	public boolean isResponseSuccess() {
		String status = getDataByPath("status");

		return StringUtils.equals("0", status);
	}

	/**
	 * 方法描述：<br/>
	 * 是否成功响应（安全）
	 *
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @return
	 */
	@Deprecated
	public boolean isSafeResponseSuccess() {
		if (this.crcCode == EncryptionHandler.crc(("@#" + this.rawData + "$*").getBytes()).toCRC16()) {
			return isResponseSuccess();
		}
		return false;
	}

	/**
	 * 方法描述：<br/>
	 * 获取CRC校验码
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @return
	 */
	@Deprecated
	public int getCrcCode() {
		return crcCode;
	}

	/**
	 * 方法描述：<br/>
	 * 获取元数据
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @return
	 */
	public String getRawData() {
		return rawData;
	}

	/**
	 * 方法描述：<br/>
	 * 通过路径获取结果集内部<br/>
	 * 比如获取result
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getDataByPath(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		try {
			return (T) MVEL.eval(path, rawDataMap);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 方法描述：<br/>
	 * 返回
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @return
	 */
	public HuaRuanResponse response() {
		return this;
	}

}
