package com.digitalchina.app.bicycle.business;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.digitalchina.app.bicycle.business.huaruan.HuaRuanHttpAsyncUtil;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanHttpAsyncUtil.HuaRuanResponseCallback;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanHttpSyncUtil;
import com.digitalchina.app.bicycle.business.huaruan.HuaRuanResponse;
import com.digitalchina.web.wattle.api.ResultPageVo;
import com.digitalchina.web.wattle.util.http.Request;

/**
 * 
 * 描述：</br>
 * 公共调度管理接口实现
 * 
 * @author wukj
 * @createTime 2015年12月27日
 *
 */
@Component
public class BicycleRemote {

	public final static Logger LOG = LogManager.getLogger(BicycleRemote.class);

	private static final String REMOTE_URL = "http://218.17.174.242:9098"; // 委办局自行车调用接口地址

	/**
	 * 方法描述：<br/>
	 * 获取自行车位置信息
	 * 
	 * @createTime 2016年6月21日
	 * @author maiwj
	 *
	 * @param id
	 * @param callback
	 */
	public void findRemoteBicycle(String id, HuaRuanResponseCallback callback) {
		if (StringUtils.isBlank(id) || callback == null) {
			return;
		}
		HuaRuanHttpAsyncUtil.requestWrapper(Request.async(), LOG).addHttpRequestParam("parkId", id)
				.post(REMOTE_URL + "/parkData_getParkDataDetail.do", callback);
	}

	/**
	 * 方法描述：<br/>
	 * 远程调用，获取所有自行车点
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 * @return
	 */
	public List<Map<String, Object>> findRemoteBicyclePointInfos() {
		HuaRuanResponse response = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.post(REMOTE_URL + "/parkData_getAllParkData.do").response();

		if (response.isResponseSuccess()) {
			return response.getDataByPath("data");
		}

		return null;
	}

	/**
	 * 方法描述：<br/>
	 * 检查自行车卡号是否匹配身份证号
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param cardNum
	 *            自行车卡号
	 * @param idCard
	 *            身份证号
	 * @return
	 */
	public boolean findCardNumMatchIdCardOrNot(String cardNum, String idCard) {
		if (StringUtils.isBlank(cardNum) || StringUtils.isBlank(idCard)) {
			return false;
		}

		HuaRuanResponse result = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.addHttpRequestParam("cardNum", cardNum).addHttpRequestParam("idCard", idCard)
				.post(REMOTE_URL + "/checkBind_checkAcount.do").response();

		return result.isResponseSuccess();
	}

	/**
	 * 方法描述：<br/>
	 * 查询自行车卡信息
	 * 
	 * @createTime 2016年6月20日
	 * @author maiwj
	 *
	 * @param cardNum
	 * @return
	 */
	public Object findBindedCardInfo(String cardNum) {
		if (StringUtils.isBlank(cardNum)) {
			return null;
		}

		int retry = 1;
		while (true) {
			try {
				HuaRuanResponse response = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG, 1000 * retry)
						.addHttpRequestParam("cardNum", cardNum).post(REMOTE_URL + "/account_getAccountInfo.do")
						.response();

				if (response.isResponseSuccess()) {
					return response.getDataByPath("data");
				}

				return null;

			} catch (Exception e) {
				if (e.getCause() instanceof SocketTimeoutException && retry++ <= 5) { // 由于对方网络存在一定的延迟，需要进行5次的有效重试
					LOG.error("findBindedCardInfo retry {}", retry - 1);
					continue;
				} else {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * 方法描述：<br/>
	 * 查询自行车借还记录
	 * 
	 * @createTime 2016年6月22日
	 * @author maiwj
	 *
	 * @param cardNum
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultPageVo<Map<String, Object>> findBindedCardBorrowStillRecords(String cardNum, Integer currentPage) {
		Integer pageSize = 10;

		if (StringUtils.isBlank(cardNum) || currentPage == null) {
			return null;
		}

		Map<String, Object> cardRecord = new HashMap<>(1);
		cardRecord.put("cardNum", cardNum);

		Map<String, Object> pager = new HashMap<>(2);
		pager.put("currentPage", currentPage);
		pager.put("pageSize", pageSize);

		int retry = 1;
		while (true) {
			try {
				HuaRuanResponse response = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG, 1000 * retry)
						.addHttpRequestParam("field", "operateTime").addHttpRequestParam("order", "0")
						.addHttpRequestParam("cardRecord", cardRecord).addHttpRequestParam("pager", pager)
						.post(REMOTE_URL + "/cardRecord_getCardRecordList.do").response();

				if (response.isResponseSuccess()) {
					Map<String, Object> data = response.getDataByPath("data");
					Long totalCount = Long.valueOf((String) data.get("totalSize"));
					List<Map<String, Object>> resultLists = (List<Map<String, Object>>) data.get("cardRecordList");
					return ResultPageVo.page(currentPage, totalCount, pageSize, resultLists);
				}

				return null;

			} catch (Exception e) {
				if (e.getCause() instanceof SocketTimeoutException && retry++ <= 5) { // 由于对方网络存在一定的延迟，需要进行5次的有效重试
					LOG.error("findBindedCardBorrowStillRecords retry {}", retry - 1);
					continue;
				} else {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * 方法描述：<br/>
	 * 挂失自行车卡
	 * 
	 * @createTime 2016年6月24日
	 * @author maiwj
	 *
	 * @param cardNum
	 * @param idCard
	 * @return
	 */
	public boolean reportBindedCardLoss(String cardNum, String idCard) {
		if (StringUtils.isBlank(cardNum) || StringUtils.isBlank(idCard)) {
			return false;
		}

		HuaRuanResponse response = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.addHttpRequestParam("idCard", idCard).addHttpRequestParam("cardNum", cardNum)
				.post(REMOTE_URL + "/clientLoss_loss.do").response();

		return response.isResponseSuccess();
	}

	/**
	 * 方法描述：<br/>
	 * 解挂自行车卡
	 * 
	 * @createTime 2016年6月24日
	 * @author maiwj
	 *
	 * @param cardNum
	 * @param idCard
	 * @return
	 */
	public boolean unlockReportedLossBindedCard(String cardNum, String idCard) {
		if (StringUtils.isBlank(cardNum) || StringUtils.isBlank(idCard)) {
			return false;
		}

		HuaRuanResponse response = HuaRuanHttpSyncUtil.requestWrapper(Request.sync(), LOG)
				.addHttpRequestParam("idCard", idCard).addHttpRequestParam("cardNum", cardNum)
				.post(REMOTE_URL + "/clientLoss_unloss.do").response();

		return response.isResponseSuccess();
	}

}
