package net.developers.tiny.wf.jobs;

import net.developers.tiny.wf.service.TinyWorkFlowClient;

import org.apache.log4j.Logger;

public class QuartzJob {
	static Logger logger = Logger.getLogger(QuartzJob.class);

	public void work()
    {
		try
		{
		long begin=System.currentTimeMillis();
		TinyWorkFlowClient.processWF();
		long end=System.currentTimeMillis();
		long timeCost=end-begin;
		logger.info("任务调度完成，耗时："+timeCost);
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
			
    }
}
