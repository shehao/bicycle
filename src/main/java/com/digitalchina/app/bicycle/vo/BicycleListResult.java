package com.digitalchina.app.bicycle.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类型描述：<br/>
 * 自行车查询列表结果
 * 
 * @createTime 2016年7月28日
 * @author maiwj
 * 
 */
public class BicycleListResult implements Serializable {

	private static final long serialVersionUID = 7020653678602503520L;

	private Boolean directionSearch = true;
	private List<Map<String, Object>> bicycleList = new ArrayList<>();
	
	public Boolean getDirectionSearch() {
		return directionSearch;
	}

	/**
	 * 方法描述：<br/>
	 * 是否径向查询
	 * 
	 * @createTime 2016年7月28日
	 * @author maiwj
	 *
	 * @param directionSearch
	 */
	public void setDirectionSearch(Boolean directionSearch) {
		this.directionSearch = directionSearch;
	}

	/**
	 * 方法描述：<br/>
	 * 设置自行车列表
	 * 
	 * @createTime 2016年7月28日
	 * @author maiwj
	 *
	 * @param bicycleList
	 */
	public void setBicycleList(List<Map<String, Object>> bicycleList) {
		this.bicycleList = bicycleList;
	}

	/**
	 * 方法描述：<br/>
	 * 获取自行车列表
	 * 
	 * @createTime 2016年7月28日
	 * @author maiwj
	 *
	 * @return
	 */
	public List<Map<String, Object>> getBicycleList() {
		return this.bicycleList;
	}

}
