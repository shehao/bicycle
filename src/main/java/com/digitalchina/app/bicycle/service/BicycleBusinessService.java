package com.digitalchina.app.bicycle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalchina.app.bicycle.api.IBicycleManager;
import com.digitalchina.app.bicycle.business.BicycleRemote;
import com.digitalchina.app.bicycle.util.DateFormate;
import com.digitalchina.app.bicycle.vo.BicycleBindVo;
import com.digitalchina.frame.message.MessageObject;
import com.digitalchina.frame.message.Result;
import com.digitalchina.frame.message.ServiceContext;
import com.digitalchina.web.hybridization.dfh.AbstractCommonService;
import com.digitalchina.web.wattle.api.ResultPageVo;
import com.digitalchina.web.wattle.util.lang.NumberHandler;


/**
 * 类型描述：<br/>
 * 自行车业务
 * 
 * @createTime 2016年6月20日
 * @author maiwj
 * 
 */
@Service
public class BicycleBusinessService extends AbstractCommonService {

	private final static Logger LOG = Logger.getLogger(BicycleBusinessService.class);

	@Autowired
	private IBicycleManager bicycleManager;

	@Autowired
	private BicycleRemote bicycleRemote;

	/**
	 * 方法描述：<br/>
	 * 判断当前的用户是否已经绑定了实体卡
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void findBindedCardOrNot(ServiceContext serviceContext) {
		try {
			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id
			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);

			setSuccessResult(serviceContext, !results.isEmpty());
		} catch (Exception e) {
			LOG.error("", e);
			setFailResult(serviceContext);
		}
	}

	/**
	 * 方法描述：<br/>
	 * 查询当前用户的所有卡绑定信息
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void findBindedCards(ServiceContext serviceContext) {
		try {
			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id
			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);

			setSuccessResult(serviceContext, results);
		} catch (Exception e) {
			LOG.error("", e);
			setFailResult(serviceContext);
		}
	}

	/**
	 * 方法描述：<br/>
	 * 解绑卡 <br/>
	 * 如果cardNum不为空，就检查当前用户是否已经绑定了该卡，已绑定的才允许解绑； <br/>
	 * 如果cardNum为空，就拿出当前用户最近绑定的卡来进行解绑
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void unbindCard(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id
			
			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);
			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) { // 如果卡不为空
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							if (bicycleManager.doRemoveCardBinding(cardNum, userId)) {
								setSuccessResult(serviceContext);
								return;
							}
							break;
						}
					}
				} else {
					if (bicycleManager.doRemoveCardBinding(results.get(0).getCardNum(), userId)) {
						setSuccessResult(serviceContext);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}

	/**
	 * 方法描述：<br/>
	 * 绑定卡<br/>
	 * 可以绑定多张，只要身份证号和卡号能够通过匹配即可
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void bindCard(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");
			String idCard = getValue(mo, "idCard"); // 目前是直接前端带过来

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id
			
			if (StringUtils.isNotBlank(cardNum) && StringUtils.isNotBlank(idCard)) {
				// 校验cardNum和idCard是不是匹配。
				if (bicycleRemote.findCardNumMatchIdCardOrNot(cardNum, idCard)) {
					// 调用接口绑定
					if (bicycleManager.doSaveCardBingding(cardNum, idCard, userId)) {
						setSuccessResult(serviceContext);
						return;
					} else {
						serviceContext.setResponseBody(new HashMap<String, Object>(1));
						serviceContext.setResponseHead( new Result("999002", "重复绑定异常"));
						return;
					}
				} else {
					serviceContext.setResponseBody(new HashMap<String, Object>(1));
					serviceContext.setResponseHead( new Result("999001", "卡信息验证错误"));
					return;
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
			setFailResult(serviceContext);
		}
	}

	/**
	 * 方法描述：<br/>
	 * 查询绑定的卡的个人信息 <br/>
	 * 如果cardNum不为空，就检查当前用户是否已经绑定了该卡，如果已经绑定将返回对应cardNum的卡信息； <br/>
	 * 如果cardNum为空，就拿出当前用户最近绑定的卡来获取对应的卡信息。
	 *
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void findBindedCardPersonalInfo(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id

			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);
			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) {
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							Object result = bicycleRemote.findBindedCardInfo(cardNum);
							if (result != null) {
								setSuccessResult(serviceContext, result);
								return;
							}
							break;
						}
					}
				} else {
					Object result = bicycleRemote.findBindedCardInfo(results.get(0).getCardNum());
					if (result != null) {
						setSuccessResult(serviceContext, result);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}

	/**
	 * 方法描述：<br/>
	 * 新的查询绑定的卡的借还记录 <br/>
	 * 如果cardNum不为空，就检查当前用户是否已经绑定了该卡，如果已经绑定将返回对应cardNum的借还记录； <br/>
	 * 如果cardNum为空，就拿出当前用户最近绑定的卡来获取对应的借还记录。
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void findBindedCardBorrowStillRecords(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");
			Integer requestPage = NumberHandler.toInteger(getValue(mo, "requestPage"), 1);

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id

			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);

			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) {
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							ResultPageVo<Map<String, Object>> result = bicycleRemote
									.findBindedCardBorrowStillRecords(cardNum, requestPage);
							if (result != null) {
								setSuccessResult(serviceContext, result);
								return;
							}
							break; // 计算查询结果为空，也结束迭代
						}
					}
				} else {
					ResultPageVo<Map<String, Object>> result = bicycleRemote
							.findBindedCardBorrowStillRecords(results.get(0).getCardNum(), requestPage);
					if (result != null) {
						setSuccessResult(serviceContext, result);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}
	
	/**
	 * 方法描述：<br/>
	 * 查询绑定的卡的借还记录 <br/>
	 * 如果cardNum不为空，就检查当前用户是否已经绑定了该卡，如果已经绑定将返回对应cardNum的借还记录； <br/>
	 * 如果cardNum为空，就拿出当前用户最近绑定的卡来获取对应的借还记录。
	 * 
	 * @createTime 2016年8月31日
	 * @author xjj
	 *
	 * @param serviceContext
	 */
	public void findNewBindedCardBorrowStillRecords(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");
			Integer requestPage = NumberHandler.toInteger(getValue(mo, "requestPage"), 1);

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id

			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);
			List<Map<String,Object>> mlist = null;
			
			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) {
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							ResultPageVo<Map<String, Object>> result = bicycleRemote
									.findBindedCardBorrowStillRecords(cardNum, requestPage);
							
							if(result!=null) {
								
								int count = result.getResultList().size();
								mlist =new ArrayList<>();
								
								for(int i=0; i<count; i++){
									Map<String,Object> returnmap = result.getResultList().get(i);
									
									if(returnmap.get("type").equals("2")) {
										for(int j=0; j<count; j++){
											Map<String,Object> borrowmap = result.getResultList().get(j);
										    if(borrowmap.get("type").equals("1")){
										    	Long retuerntime = new DateFormate().convert2long((String)returnmap.get("operateTime"));
												Long borrowtime = new DateFormate().convert2long((String)borrowmap.get("operateTime"));
										
												if(returnmap.get("bikeNum").equals(borrowmap.get("bikeNum")) && (retuerntime>borrowtime)){
													
													Map<String,Object> map = new HashMap<>();													
													map.put("borrowmap", borrowmap);
													map.put("returnmap", returnmap);
													mlist.add(map);
												}
										    }																																											
										}									
									}
								}

								Map<String,Object> firstMap= result.getResultList().get(0);
								if(firstMap.get("type").equals("1")) {
									Map<String,Object> map = new HashMap<>();									
									map.put("borrowmap", firstMap);
									map.put("returnmap", null);
									mlist.add(map);
								}
								
							}
																											
							if (mlist != null) {
								setSuccessResult(serviceContext, mlist);
								return;
							}
							break; // 计算查询结果为空，也结束迭代
						}
					}
				} else {
					ResultPageVo<Map<String, Object>> result = bicycleRemote
							.findBindedCardBorrowStillRecords(results.get(0).getCardNum(), requestPage);
					
					if(result!=null) {						
						int count = result.getResultList().size();
						mlist =new ArrayList<>();
						
						for(int i=0; i<count; i++){
							Map<String,Object> returnmap = result.getResultList().get(i);
							
							if(returnmap.get("type").equals("2")) {
								for(int j=0; j<count; j++){
									Map<String,Object> borrowmap = result.getResultList().get(j);
								    if(borrowmap.get("type").equals("1")){
								    	Long retuerntime = new DateFormate().convert2long((String)returnmap.get("operateTime"));
										Long borrowtime = new DateFormate().convert2long((String)borrowmap.get("operateTime"));
								
										if(returnmap.get("bikeNum").equals(borrowmap.get("bikeNum")) && (retuerntime>borrowtime)){
										
											Map<String,Object> map = new HashMap<>();											
											map.put("borrowmap", borrowmap);
											map.put("returnmap", returnmap);
											mlist.add(map);
										}
								    }																																											
								}									
							}
						}

						Map<String,Object> firstMap= result.getResultList().get(0);
						if(firstMap.get("type").equals("1")) {
							Map<String,Object> map = new HashMap<>();
							map.put("borrowmap", firstMap);
							map.put("returnmap", null);
							mlist.add(map);
						}
						
					}
					if (mlist != null) {
						setSuccessResult(serviceContext, mlist);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}
	
	

	/**
	 * 方法描述：<br/>
	 * 挂失卡
	 * 
	 * @createTime 2016年6月24日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void reportBindedCardLoss(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id

			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);

			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) { // 卡号不为空
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							if (bicycleRemote.reportBindedCardLoss(cardNum, bbv.getIdCard())) {
								setSuccessResult(serviceContext);
								return;
							}
							break;
						}
					}
				} else {
					BicycleBindVo lastedResult = results.get(0);
					if (bicycleRemote.reportBindedCardLoss(lastedResult.getCardNum(), lastedResult.getIdCard())) {
						setSuccessResult(serviceContext);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}

	/**
	 * 方法描述：<br/>
	 * 解挂卡
	 * 
	 * @createTime 2016年6月24日
	 * @author maiwj
	 *
	 * @param serviceContext
	 */
	public void unlockReportedLossBindedCard(ServiceContext serviceContext) {
		try {
			MessageObject mo = serviceContext.getRequestData();
			String cardNum = getValue(mo, "cardNum");

			Long userId = getAuthUser(serviceContext).getUserid(); // 用户Id

			List<BicycleBindVo> results = bicycleManager.findBindedCards(userId);

			if (!results.isEmpty()) {
				if (StringUtils.isNotBlank(cardNum)) { // 卡号不为空
					for (BicycleBindVo bbv : results) {
						if (StringUtils.equals(bbv.getCardNum(), cardNum)) {
							if (bicycleRemote.unlockReportedLossBindedCard(cardNum, bbv.getIdCard())) {
								setSuccessResult(serviceContext);
								return;
							}
							break;
						}
					}
				} else {
					BicycleBindVo lastedResult = results.get(0);
					if (bicycleRemote.unlockReportedLossBindedCard(lastedResult.getCardNum(),
							lastedResult.getIdCard())) {
						setSuccessResult(serviceContext);
						return;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		setFailResult(serviceContext);
	}

}
