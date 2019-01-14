package com.digitalchina.app.bicycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * 类型描述：<br/>
 * 通用组件
 * 
 * @createTime 2015年7月23日
 * @author maiwj
 * 
 */
@ContextConfiguration(locations = { 
		"classpath:/config-springmvc.xml",
		"classpath:/config-security.xml",
		"classpath:/config.xml",
		"classpath:/config-cache.xml"}
		)
//"classpath:/config-quartz.xml",
public abstract class CommonTest {

	private final static String MARK_EXT = "===============";
	protected final Logger log = LogManager.getLogger(getClass());

	protected String testMethodName;
	@Autowired
	protected ApplicationContext ac;

	/**
	 * 方法描述：<br/>
	 * 标记结束日志
	 * 
	 * @createTime 2015年7月23日
	 * @author maiwj
	 *
	 * @param str
	 * @return
	 */
	private String markEndMessage(String str) {
		if (str != null) {
			return markMessage(str.concat(" --  END  --"));
		} else {
			return markMessage("anonymous method -- END --");
		}
	}

	/**
	 * 方法描述：<br/>
	 * 标记日志
	 * 
	 * @createTime 2015年7月23日
	 * @author maiwj
	 *
	 * @param str
	 * @return
	 */
	private String markMessage(String str) {
		return new StringBuilder(MARK_EXT).append(str).append(MARK_EXT)
				.toString();
	}

	/**
	 * 方法描述：<br/>
	 * 设置方法真实名称
	 * 
	 * @createTime 2015年7月23日
	 * @author maiwj
	 *
	 * @param testMethodName
	 */
	public void setTestMethodName(String testMethodName) {
		this.testMethodName = testMethodName;
	}

	@After
	public void after() {
		log.info(markEndMessage(testMethodName));
	}

	// |---------------|
	// BeforeClass -> Before -> Test -> After ->AfterClass

}
