package net.developers.tiny.wf.service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.PooledConnection;

import net.developers.tiny.wf.WFParser;
import net.developers.tiny.wf.dao.TaskInfoDAO;
import net.developers.tiny.wf.dao.WorkFlowDAO;
import net.developers.tiny.wf.model.PublicVars;
import net.developers.tiny.wf.model.TaskNode;
import net.developers.tiny.wf.model.WorkFlowContext;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class TinyWorkFlowClient {
	static Logger logger = Logger.getLogger(TinyWorkFlowClient.class);
	protected Connection conn = null;
	protected PreparedStatement pstmt = null;
	protected static String dbUrl = "jdbc:mysql://localhost:3306/cached_frame?user=root&password=root";
	public static void processUserTask(int workflowId,int status) throws Exception
	{
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
	 
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();
		TaskInfoDAO taskDao=new TaskInfoDAO();
		List<TaskNode> nextTask=taskDao.getNextTask(workflowId, stat);
		List<TaskNode> task=taskDao.getTaskToProcess(workflowId, stat);
		if(task.get(0).getTaskType()==TaskNode.S_TASK_TYPE_USER)
		{
		taskDao.processTask(Integer.parseInt(task.get(0).getId()), TaskNode.S_TASK_STATUS_SUCCESS, stat);
		taskDao.processTask(Integer.parseInt(nextTask.get(0).getId()), TaskNode.S_TASK_STATUS_PROCESSING, stat);
		}
	}
	public static List<PublicVars> getWorkFlowVars(int workflowId) throws Exception
	{
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
	 
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();
		WorkFlowDAO dao=new WorkFlowDAO();
		WorkFlowContext workFlowInstance=dao.getWorkFlowsInfo(workflowId, stat);
		stat.close();
		connToMySQL.close();
		pooledConnection.close();
		return workFlowInstance.getVars();
	}
	public static void updateWorkFlowVars(int workFlowId,String varName,String value) throws Exception
	{
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
	 
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();
		WorkFlowDAO dao=new WorkFlowDAO();
		dao.updateWorkFlowVar(workFlowId, varName, value, stat);
		stat.close();
		connToMySQL.close();
		pooledConnection.close();
	}
	public static void updateTaskVars(int taskId,String varName,String value) throws Exception
	{
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
	 
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();
		TaskInfoDAO dao=new TaskInfoDAO();
		dao.updateTaskNodeVar(taskId, varName, value, stat);
		stat.close();
		connToMySQL.close();
		pooledConnection.close();
	}
	public static long createWorkFlow(String wfTemplate,int businessType,int businessId,Map<String,String> params) throws Exception
	{
		ResultSet rs=null;
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
	 
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();

		wfTemplate="/Users/bixk/DownLoads/wf_template.xml";
		WFParser parser=new WFParser();
		WorkFlowContext wf=parser.parser(wfTemplate);
		for(PublicVars var:wf.getVars())
		{
			String key=var.getName().toUpperCase();
			String value=params.get(key);
			var.setValue(value);
		}
		wf.setBusinessId(businessId);
		wf.setBusinessType(businessType);
		WorkFlowDAO wfDao=new WorkFlowDAO();
		TaskInfoDAO taskDao=new TaskInfoDAO();
		long wfId=wfDao.createWorkFlowInstance(wf,stat,rs);
		List<TaskNode> tasks=wf.getTasks();
	
		
		for(TaskNode task:tasks)
		{
			task.initNodeVarsValue(params);
			task.setWorkflowId((int)wfId);
			int taskId=taskDao.saveTaskInstance(task,stat,rs);
			task.setId(taskId+"");
			task.refresh();
		}
		return wfId;
	}
	public static void processWF() throws Exception
	{
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL(TinyWorkFlowClient.dbUrl);
		ds.setCharacterEncoding("utf-8");
		PooledConnection pooledConnection = ds.getPooledConnection();
		java.sql.Connection connToMySQL = pooledConnection.getConnection();
		java.sql.Statement stat=connToMySQL.createStatement();
		WorkFlowDAO wfDao=new WorkFlowDAO();
		TaskInfoDAO taskDao=new TaskInfoDAO();
		List<WorkFlowContext> workflowToProcess=wfDao.getWorkFlowsToProcess(stat);
		logger.debug("待处理的流程数量："+workflowToProcess.size());
		for(WorkFlowContext context:workflowToProcess)
		{
			logger.debug("处理流程："+context.getLabel()+":"+context.getWorkflowId());
			List<TaskNode> lst=taskDao.getTaskToProcess(context.getWorkflowId(), stat);
			if(null!=lst&&lst.isEmpty()==false)
			{
				TaskNode task=lst.get(0);
				logger.debug("当前待处理节点:"+task.getLabel()+"-"+task.getId());
				TaskProcessor processor=new TaskProcessor();
				processor.setStat(stat);
				processor.process(task);
				
			}
		}
	}
	public static void main(String[] args) throws Exception
	{
//		PropertyConfigurator.configure("log4j.properties");
		Map<String,String> params=new HashMap<String,String>();
		params.put("PARA_STR", "cc");
		for(int i=1347;i<=1815;i++)
		{
 		TinyWorkFlowClient.createWorkFlow("", 1000, 1, params);
// 		TinyWorkFlowClient.processUserTask(i, TaskNode.S_TASK_STATUS_SUCCESS);
// 		Thread.sleep(20);
		}
//		TinyWorkFlowClient.processUserTask(7, TaskNode.S_TASK_STATUS_SUCCESS);
//		TinyWorkFlowClient.processUserTask(8, TaskNode.S_TASK_STATUS_SUCCESS);
//		TinyWorkFlowClient.processUserTask(9, TaskNode.S_TASK_STATUS_SUCCESS);
	}
}
