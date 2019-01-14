package com.digitalchina.app.bicycle.vo;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.mvel2.MVEL;

/**
 * 类型描述：<br/>
 * 百度地图回调结果
 * 
 * @createTime 2016年7月18日
 * @author maiwj
 * 
 */
public class BaiduCallResult extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * 方法描述：<br/>
	 * 是否成功
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @return
	 */
	public boolean isSuccess() {
		return getStatus() == 0;
	}

	/**
	 * 方法描述：<br/>
	 * 获取状态码<br/>
	 * 0 正常 <br/>
	 * 1 服务器内部错误 <br/>
	 * 2 请求参数非法 <br/>
	 * 3 权限校验失败 <br/>
	 * 4 配额校验失败 <br/>
	 * 5 ak不存在或者非法 </br/>
	 * 101 服务禁用 <br/>
	 * 102 不通过白名单或者安全码不对<br/>
	 * 2xx 无权限 <br/>
	 * 3xx 配额错误 <br/>
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @return
	 */
	public Integer getStatus() {
		return (Integer) get("status");
	}
	
	/**
	 * 方法描述：<br/>
	 * 获取消息
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @return
	 */
	public String getMsg() {
		Integer status = getStatus();
		switch (status) {
		case 0:
			return "正常";
		case 1:
			return "服务器内部错误";
		case 2:
			return "请求参数非法";
		case 3:
			return "权限校验失败";
		case 4: 
			return "配额校验失败";
		case 5:
			return "ak不存在或者非法";
		case 101:
			return "服务禁用";
		case 102:
			return "不通过白名单或者安全码不对";
		default:
			Integer cstatus = status / 100;
			if (cstatus == 2) {
				return "无权限，代码为：" + status;
			} else if (cstatus == 3) {
				return "配额错误，代码为：" + status;
			} else {
				return "未知权限，代码为：" + status;
			}
		}
	}
	
	/**
	 * 方法描述：<br/>
	 * 获取结果集
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		return (T) get("result");
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
	public <T> T getByPath(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		try {
			return (T) MVEL.eval(path, this);
		} catch (Exception e) {
			return null;
		}
	}

}
