package net.developers.tiny.wf.service;

import java.lang.reflect.Method;
import java.util.List;

import net.developers.tiny.wf.dao.TaskInfoDAO;
import net.developers.tiny.wf.dao.WorkFlowDAO;
import net.developers.tiny.wf.model.BaseTask;
import net.developers.tiny.wf.model.NodeVars;
import net.developers.tiny.wf.model.PublicVars;
import net.developers.tiny.wf.model.TaskNode;
import net.developers.tiny.wf.model.WorkFlowContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class TaskProcessor {
	static Logger logger = Logger.getLogger(TaskProcessor.class);
	private java.sql.Statement stat;
	public void process(TaskNode task) throws Exception
	{
		WorkFlowDAO dao=new WorkFlowDAO();
		WorkFlowContext context=dao.getWorkFlowsInfo(task.getWorkflowId(), stat);
		asynVars(context,task);
		switch(task.getTaskType())
		{
		case TaskNode.S_TASK_TYPE_AUTO:processAuto(task);break;
		case TaskNode.S_TASK_TYPE_DECISION:processDecision(task);break;
		case TaskNode.S_TASK_TYPE_FINISH:processEnd(task);break;
		case TaskNode.S_TASK_TYPE_START:processStart(task);break;
		case TaskNode.S_TASK_TYPE_USER:processUser(task);break;
		default:break;
		}
	}
	private void asynVars(WorkFlowContext wf,TaskNode task)
	{
		for(NodeVars var:task.getAutoDeal().getVars())
		{
			PublicVars pVars=wf.getVarDetailByKey(var.getContextvarName());
			if(pVars!=null&&StringUtils.isNotBlank(pVars.getValue())==false)
			{
				var.setValue(pVars.getValue());
				logger.debug("同步变量："+var.getName()+"->"+var.getValue());
			}
		}
	}
	private void processAuto(TaskNode task)  throws Exception
	{
		TaskInfoDAO dao=new TaskInfoDAO();
		logger.debug("处理自动节点："+task.getId());
		/////////////
		String clazzName=task.getAutoDeal().getRunClassName();
		String funcName=task.getAutoDeal().getRunFunctionName();
		Object[] args=new Object[task.getAutoDeal().getVars().size()-1];
		for(int i=0;i<task.getAutoDeal().getVars().size()-1;++i)
		{
			args[i]=task.getAutoDeal().getVars().get(i).getValue();
		}
		invokeMethod(clazzName,funcName,args,task.getWorkflowId(),Integer.parseInt(task.getId()));
		/////////////
		List<TaskNode> tasks=dao.getNextTask(task.getWorkflowId(), this.getStat());
		List<TaskNode> nextTasks=dao.getNextTask(tasks.get(0).getWorkflowId(), this.getStat());
		dao.processTask(Integer.parseInt(task.getId()), TaskNode.S_TASK_STATUS_SUCCESS, this.getStat());
		dao.processTask(Integer.parseInt(nextTasks.get(0).getId()), TaskNode.S_TASK_STATUS_PROCESSING, this.getStat());
		
		logger.debug("自动节点处理完成："+task.getId());
	}
	public Object invokeMethod(String className, String methodName,Object[] args,int workflowId,int taskId) throws Exception{

		Class<?> ownerClass = Class.forName(className);	
		BaseTask owner = (BaseTask)ownerClass.newInstance();
	   owner.setCurrentWorkTaskId(taskId);
	   owner.setWorkflowId(workflowId);
        Class<?>[] argsClass = new Class[args.length];    
   
        for (int i = 0, j = args.length; i < j; i++) {    
        	
           argsClass[i] = args[i].getClass();      	       
        }    
   
        Method method = ownerClass.getMethod(methodName, argsClass);   
		return method.invoke(owner, args);
	}
	private void processUser(TaskNode task)  throws Exception
	{
		TaskInfoDAO dao=new TaskInfoDAO();
		logger.debug("遇到人工节点："+task.getId()+"不做自动处理！");
	}
	private void processStart(TaskNode task)  throws Exception
	{
		TaskInfoDAO dao=new TaskInfoDAO();
		logger.debug("处理开始节点："+task.getId());
		List<TaskNode> tasks=dao.getNextTask(task.getWorkflowId(), this.getStat());
		List<TaskNode> nextTasks=dao.getNextTask(tasks.get(0).getWorkflowId(), this.getStat());
		dao.processTask(Integer.parseInt(task.getId()), TaskNode.S_TASK_STATUS_SUCCESS, this.getStat());
		dao.processTask(Integer.parseInt(nextTasks.get(0).getId()), TaskNode.S_TASK_STATUS_PROCESSING, this.getStat());
		logger.debug("开始节点处理完成："+task.getId());
	}
	private void processEnd(TaskNode task)  throws Exception
	{
		TaskInfoDAO dao=new TaskInfoDAO();
		WorkFlowDAO wfDao=new WorkFlowDAO();
		logger.debug("处理完成节点："+task.getId());
		dao.processTask(Integer.parseInt(task.getId()), TaskNode.S_TASK_STATUS_SUCCESS, this.getStat());
		WorkFlowContext wf=wfDao.getWorkFlowsInfo(task.getWorkflowId(), stat);
		logger.debug("业务流程："+wf.getLabel()+"["+wf.getWorkflowId()+"]已经处理完毕！");
		wfDao.processWorkFlow(task.getWorkflowId(), 99, stat);
	}
	private void processDecision(TaskNode task) throws Exception
	{
		TaskInfoDAO dao=new TaskInfoDAO();
		logger.debug("处理判断节点："+task.getId());
		List<TaskNode> nextTasks=dao.getNextTaskWithCondition(task.getWorkflowId(), "0001",  this.getStat());
		dao.processTask(Integer.parseInt(task.getId()), TaskNode.S_TASK_STATUS_SUCCESS, this.getStat());
		dao.processTask(Integer.parseInt(nextTasks.get(0).getId()), TaskNode.S_TASK_STATUS_PROCESSING, this.getStat());
	}
	public java.sql.Statement getStat() {
		return stat;
	}
	public void setStat(java.sql.Statement stat) {
		this.stat = stat;
	}
	
}
