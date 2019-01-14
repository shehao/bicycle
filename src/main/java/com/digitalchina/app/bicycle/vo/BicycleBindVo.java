package com.digitalchina.app.bicycle.vo;

import java.io.Serializable;

/**
 * 类型描述：<br/>
 * 自行车绑定
 * 
 * @createTime 2016年6月21日
 * @author maiwj
 * 
 */
public class BicycleBindVo implements Serializable {

	private static final long serialVersionUID = -7699013863782342272L;

	private Long userId; // 用户Id
	private String cardNum; // 自行车卡号
	private String idCard; // 身份证号
	private Long bindingTime; // 绑定时间

	public BicycleBindVo() {

	}

	public BicycleBindVo(Long userId, String cardNum) {
		this.userId = userId;
		this.cardNum = cardNum;
	}

	public BicycleBindVo(Long userId, String cardNum, String idCard) {
		this.userId = userId;
		this.cardNum = cardNum;
		this.idCard = idCard;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public long getBindingTime() {
		return bindingTime;
	}

	public void setBindingTime(Long bindingTime) {
		this.bindingTime = bindingTime;
	}

}
