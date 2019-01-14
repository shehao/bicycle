package com.digitalchina.app.bicycle.vo;

import java.io.Serializable;

import com.digitalchina.web.azalea.elastricsearch.vo.ESGeoPointVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 类型描述：<br/>
 * 免费自行车对象
 * 
 * @createTime 2015年12月24日
 * @author maiwj
 * 
 */
@JsonInclude(Include.NON_EMPTY)
public class BicycleVo implements Serializable {

	private static final long serialVersionUID = 5497301569223548077L;

	private String id; // id
	private String name; // 名称
	private String pcCount;// 总车位数
	private ESGeoPointVo location; // 位置
	private Long createTime; // 创建时间

	public BicycleVo() {
	}

	public BicycleVo(String id) {
		this.id = id;
	}

	public BicycleVo(String id, String name, String pcCount, ESGeoPointVo location) {
		this.id = id;
		this.name = name;
		this.pcCount = pcCount;
		this.location = location;
		this.createTime = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ESGeoPointVo getLocation() {
		return location;
	}

	public void setLocation(ESGeoPointVo location) {
		this.location = location;
	}

	public String getPcCount() {
		return pcCount;
	}

	public void setPcCount(String pcCount) {
		this.pcCount = pcCount;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

}
