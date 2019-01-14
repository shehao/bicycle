package com.digitalchina.app.bicycle.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.digitalchina.app.bicycle.vo.BaiduCallResult;
import com.digitalchina.web.wattle.util.http.Request;
import com.digitalchina.web.wattle.util.json.JsonHandler;

/**
 * 类型描述：<br/>
 * 百度远程调度
 * 
 * @createTime 2016年7月18日
 * @author maiwj
 * 
 */
@Component
public class BaiduRemote {

	// http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
	@Value("${baidu.geocoder.api}")
	private String baiduGeocoderApiUrl = "http://api.map.baidu.com/geocoder/v2/"; // 百度地图地理位置信息反编码接口地址
	// http://lbsyun.baidu.com/index.php?title=webapi/route-matrix-api-v2
	@Value("${baidu.routematrix.api}")
	private String baiduRouteMatrixApiUrl = "http://api.map.baidu.com/routematrix/v2/"; // 百度地图路由矩阵接口地址
	@Value("${baidu.ak}")
	private String baiduApiAk = "zkTQuTVmCCVKaQnsLNZz7FQ0KVGe6HQz"; // 百度地图AK
	private String baiduCoordType = "bd09ll"; // 百度地图地理位置编码类型

	/**
	 * 方法描述：<br/>
	 * 百度地图逆向地理位置查询，不查周边的poi
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @return
	 */
	public BaiduCallResult reverseBaiduMapGEOWithoutPOIS(Double latitude, Double longitude) {
		return reverseGEO(baiduCoordType, String.format("%f,%f", latitude, longitude), 0);
	}

	/**
	 * 方法描述：<br/>
	 * 逆向地理位置查询
	 * 
	 * @createTime 2016年7月18日
	 * @author maiwj
	 *
	 * @param coordtype
	 *            坐标的类型，目前支持的坐标类型包括：bd09ll（百度经纬度坐标，默认值）、bd09mc（百度米制坐标）、gcj02ll（
	 *            国测局经纬度坐标）、wgs84ll（ GPS经纬度）
	 * @param location
	 *            根据经纬度坐标获取地址，格式是：lat<纬度>,lng<经度>
	 * @param pois
	 *            是否显示指定位置周边的poi，0为不显示，1为显示。当值为1时，显示周边100米内的poi。
	 * @return
	 */
	public BaiduCallResult reverseGEO(String coordtype, String location, Integer pois) {

		String str = Request.sync().addHttpRequestParam("output", "json").addHttpRequestParam("ak", baiduApiAk)
				.addHttpRequestParam("coordtype", coordtype).addHttpRequestParam("location", location)
				.addHttpRequestParam("pois", pois).get(baiduGeocoderApiUrl).responseString();

		BaiduCallResult result = null;

		try {
			result = JsonHandler.fromJson(str, BaiduCallResult.class);
		} catch (Exception e) {
		}

		return result;

	}

	/**
	 * 方法描述：<br/>
	 * 步行路由矩阵设计
	 * 
	 * @createTime 2016年8月4日
	 * @author maiwj
	 *
	 * @param originLatitude
	 *            起点纬度
	 * @param originLongitude
	 *            起点经度
	 * @param destLatitude
	 *            终点纬度
	 * @param destLongitude
	 *            终点经度
	 * @return
	 */
	public BaiduCallResult walkingRouteMatrixDesign(Double originLatitude, Double originLongitude, Double destLatitude,
			Double destLongitude) {
		return routeMatrixDesign(String.format("%f,%f", originLatitude, originLongitude),
				String.format("%f,%f", destLatitude, destLongitude), "walking", baiduCoordType, "11", null, null);
	}

	/**
	 * 方法描述：<br/>
	 * 行车路由矩阵设计
	 * 
	 * @createTime 2016年8月4日
	 * @author maiwj
	 *
	 * @param originLatitude
	 *            起点纬度
	 * @param originLongitude
	 *            起点经度
	 * @param destLatitude
	 *            终点纬度
	 * @param destLongitude
	 *            终点经度
	 * @return
	 */
	public BaiduCallResult drivingRouteMatrixDesign(Double originLatitude, Double originLongitude, Double destLatitude,
			Double destLongitude) {
		return routeMatrixDesign(String.format("%f,%f", originLatitude, originLongitude),
				String.format("%f,%f", destLatitude, destLongitude), "driving", baiduCoordType, "11", null, null);
	}

	/**
	 * 方法描述：<br/>
	 * 路由矩阵
	 * 
	 * @createTime 2016年8月4日
	 * @author maiwj
	 *
	 * @param origin
	 * @param destination
	 * @param mode
	 * @param coord_type
	 * @param tactics
	 * @param sn
	 * @param timestamp
	 * @return
	 */
	public BaiduCallResult routeMatrixDesign(String origin, String destination, String mode, String coord_type,
			String tactics, String sn, Long timestamp) {

		String str = Request.sync().addHttpRequestParam("output", "json").addHttpRequestParam("ak", baiduApiAk)
				.addHttpRequestParam("origins", origin).addHttpRequestParam("destinations", destination)
				.addHttpRequestParam("tactics", tactics).addHttpRequestParam("sn", sn)
				.addHttpRequestParam("timestamp", timestamp).get(baiduRouteMatrixApiUrl + mode).responseString();

		BaiduCallResult result = null;

		try {
			result = JsonHandler.fromJson(str, BaiduCallResult.class);
		} catch (Exception e) {
		}

		return result;
	}

}
