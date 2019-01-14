package com.digitalchina.app.bicycle.service;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalchina.app.bicycle.api.IBicycleManager;
import com.digitalchina.app.bicycle.vo.BicycleListResult;
import com.digitalchina.frame.message.MessageObject;
import com.digitalchina.frame.message.Result;
import com.digitalchina.frame.message.ServiceContext;
import com.digitalchina.web.hybridization.dfh.AbstractCommonService;
import com.digitalchina.web.wattle.util.lang.NumberHandler;

/**
 * 类型描述：<br/>
 * 自行车服务
 * 
 * @createTime 2015年12月24日
 * @author maiwj
 * 
 */
@Service
public class BicycleService extends AbstractCommonService {

	private final static Logger LOG = Logger.getLogger(BicycleService.class);

	@Autowired
	private IBicycleManager bicycleManager;

	/**
	 * 方法描述：<br/>
	 * 查找自行车列表<br/>
	 * 查询条件：<br/>
	 * longitude和latitude表示的是地理信息位置（基于百度地图）<br/>
	 * keyword表示查询关键字，比对自行车点名称和自行车的所在地址<br/>
	 * id表示对应的自行车站点的id<br/>
	 * <br/>
	 * 条件组合：<br/>
	 * 1、longitude和latitude必须成对组成地理信息位置，当只有地理信息的时候，查询该地理信息周边半径1km圆形区域的自行车点；<br/>
	 * 2、当只有关键字信息的时候，查询符合该关键字的自行车点；<br/>
	 * 3、以上三个条件可以自由组合，形成与条件查询；<br/>
	 * 4、当什么都没有的时候，列出所有的自行车查询点
	 *
	 * 
	 * @createTime 2015年12月24日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void locationFindBicycles(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();

			Double longitude = NumberHandler.toDouble(getValue(mo, "longitude"), null);
			Double latitude = NumberHandler.toDouble(getValue(mo, "latitude"), null);
			Integer pageSize = NumberHandler.toInteger(getValue(mo, "pageSize"), 10);
			
			if (longitude == null || latitude == null) {
				setFailResult(serviceContext);
				return;
			}
			
			if (pageSize > 9999) {
				pageSize = 9999; // 全查
			}

			BicycleListResult result = bicycleManager.findBicycles(longitude, latitude, pageSize);

			HashMap<String, Object> responseBody = new HashMap<String, Object>(1);
			responseBody.put("result", result.getBicycleList());

			serviceContext.setResponseBody(responseBody);
			serviceContext.setResponseHead(
					result.getDirectionSearch() ? Result.SUCCESS_RESULT : new Result("000001", "请求成功"));
		} catch (Exception e) {
			LOG.error("", e);
			setFailResult(serviceContext);
		}

	}
}
