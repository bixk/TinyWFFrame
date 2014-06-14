package com.test.clazz;

import net.developers.tiny.wf.dao.TaskInfoDAO;
import net.developers.tiny.wf.model.BaseTask;
import net.developers.tiny.wf.service.TinyWorkFlowClient;
import net.developers.tiny.wf.utils.SpringUtil;

import org.apache.log4j.Logger;

public class TestAutoNode01 extends BaseTask{
	static Logger logger = Logger.getLogger(TaskInfoDAO.class);
	public void run(String param)
	{
		try {
			TinyWorkFlowClient.updateTaskVars(this.getCurrentWorkTaskId(), "PARA_STR", "valueChanged");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestClzInSpring bean=(TestClzInSpring) SpringUtil.getApplicationContext().getBean("inSpring");
		bean.hello(param);
	}
}
