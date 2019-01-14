package com.digitalchina.app.bicycle.api;

import java.util.List;

import com.digitalchina.app.bicycle.vo.BicycleBindVo;
import com.digitalchina.app.bicycle.vo.BicycleListResult;

/**
 * 类型描述：<br/>
 * 委办局自行车的接口管理
 * 
 * @createTime 2015年12月24日
 * @author maiwj
 * 
 */
public interface IBicycleManager {

	/**
	 * 方法描述：<br/>
	 * 查询自行车列表
	 * 
	 * @createTime 2015年12月24日
	 * @author maiwj
	 *
	 * @param longitude
	 *            纬度
	 * @param latitude
	 *            经度
	 * @param pageSize
	 *            每页大小
	 * 
	 * @return 自行车查询列表结果
	 */
	BicycleListResult findBicycles(Double longitude, Double latitude, Integer pageSize);

	/**
	 * 方法描述：<br/>
	 * 查询当前用户绑定的卡列表
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param userId
	 * @return
	 */
	List<BicycleBindVo> findBindedCards(Long userId);

	/**
	 * 方法描述：<br/>
	 * 保存卡的绑定
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param cardNum
	 *            卡号
	 * @param idCard
	 *            身份证号
	 * @param userId
	 *            用户Id
	 * 
	 * @return
	 */
	boolean doSaveCardBingding(String cardNum, String idCard, Long userId);

	/**
	 * 方法描述：<br/>
	 * 删除卡的绑定
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param cardNum
	 * @param userId
	 * @return
	 */
	boolean doRemoveCardBinding(String cardNum, Long userId);
}
