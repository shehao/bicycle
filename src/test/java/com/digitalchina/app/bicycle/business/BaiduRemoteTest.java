package com.digitalchina.app.bicycle.business;

import org.junit.Test;

import com.digitalchina.app.bicycle.vo.BaiduCallResult;

/**
 * 类型描述：<br/>
 *
 * @createTime 2016年7月26日
 * @author maiwj
 * 
 */
public class BaiduRemoteTest {

	@Test
	public void test001() {

		BaiduRemote br = new BaiduRemote();
		BaiduCallResult bcr = br.reverseBaiduMapGEOWithoutPOIS(40.056890127931279, 116.30815063007148d);
		
		System.out.println(bcr.getMsg());
		System.out.println(bcr.isSuccess());		
		
	}

	
}
