/**
 * 
 */
package com.digitalchina.app.bicycle.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类型描述：<br/>
 * 
 *
 * @createTime：2016年8月31日
 * @author xjj
 *
 */
public class DateFormate {
	public long convert2long(String data) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		Date dt = null;
		try {
			dt = sdf.parse(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return dt.getTime();    
	}
}
