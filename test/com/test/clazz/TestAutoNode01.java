package com.test.clazz;

import net.developers.tiny.wf.dao.TaskInfoDAO;
import net.developers.tiny.wf.utils.SpringUtil;

import org.apache.log4j.Logger;

public class TestAutoNode01 {
	static Logger logger = Logger.getLogger(TaskInfoDAO.class);
	public void run(String param)
	{
		TestClzInSpring bean=(TestClzInSpring) SpringUtil.getApplicationContext().getBean("inSpring");
		bean.hello(param);
	}
}
