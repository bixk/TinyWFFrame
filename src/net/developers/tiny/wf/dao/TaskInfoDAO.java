package net.developers.tiny.wf.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.developers.tiny.wf.model.GotoItem;
import net.developers.tiny.wf.model.NodeVars;
import net.developers.tiny.wf.model.TaskNode;

import org.apache.log4j.Logger;

public class TaskInfoDAO {
	static Logger logger = Logger.getLogger(TaskInfoDAO.class);

	public TaskInfoDAO()
	{
		
	}
	public void updateTaskNodeVar(int taskId,String varName,String varValue,java.sql.Statement stat) throws Exception
	{
		logger.info("update wf_task_param set param_value='"+varValue+"' where param_task_id="+taskId+" and param_context_var_name='"+varName.toUpperCase()+"'");
		stat.executeUpdate("update wf_task_param set param_value='"+varValue+"' where param_task_id="+taskId+" and param_context_var_name='"+varName.toUpperCase()+"'");
	}
	public void processTask(int taskId,int status,java.sql.Statement stat) throws Exception
	{
		logger.debug("update wf_task set task_status="+status+" where task_id="+taskId);
		stat.executeUpdate("update wf_task set task_status="+status+" where task_id="+taskId); 
	}
	public List<TaskNode> getTaskToProcess(int workflowId,java.sql.Statement stat) throws Exception
	{
		return getTaskNode("select * from wf_task where task_status=1 and task_workflow="+workflowId,stat);
	}
	public List<TaskNode> getNextTask(int workflowId,java.sql.Statement stat) throws Exception
	{
		return getTaskNode("select * from wf_task where task_workflow="+workflowId+" and task_order_id in ("+
		"select c.next_task_id from wf_task t inner join wf_task_condition c on c.task_id=t.task_id "+
		"where t.task_status=1 and t.task_workflow="+workflowId+")",stat);
	}
	public List<TaskNode> getNextTaskWithCondition(int workflowId,String condition,java.sql.Statement stat) throws Exception
	{
		String sql="select * from wf_task where task_workflow="+workflowId+" and task_order_id in ("+
				"select c.next_task_id from wf_task t inner join wf_task_condition c on c.task_id=t.task_id"+
				" where t.task_status=1 and t.task_workflow="+workflowId+" and c.condition_context='"+condition+"')";
		logger.debug(sql);
		return getTaskNode(sql,stat);
	}
	public List<TaskNode> getTaskNode(String sql,java.sql.Statement stat) throws Exception
	{
		List<TaskNode> tasks=new ArrayList<TaskNode>();
		ResultSet rs=stat.executeQuery(sql); 
		 while (rs.next()) { 
			 TaskNode task=new TaskNode();
			 task.setId(rs.getInt("task_id")+"");
			 task.setLabel(rs.getString("task_label"));
//			 task.setOrgTaskType(rs.getString(""));
			 task.setRoleId(rs.getInt("task_role_id"));
			 task.setStaffId(rs.getInt("task_staff_id"));
			 task.setStart(rs.getInt("task_isstart")==1?true:false);
			 task.setTaskOrderId(rs.getString("task_order_id"));
			 task.setTaskTag(rs.getString("task_tag"));
			 task.setTaskType(rs.getInt("task_type"));
			 task.setWorkflowId(rs.getInt("task_workflow"));
			 task.setStatus(rs.getInt("task_status"));
			 task.getAutoDeal().setRunClassName(rs.getString("task_service_clazz"));
			 task.getAutoDeal().setRunFunctionName(rs.getString("task_service_func"));
			 task.getAutoDeal().setServiceName(rs.getString("task_service_clazz"));
			 tasks.add(task);
		 }
		 rs.close();
		 for(TaskNode task:tasks)
		 {
			 loadGotoItem(stat,task);
			 loadCondition(stat,task);
		 }
		 return tasks;
	}
	private void loadGotoItem(java.sql.Statement stat,TaskNode task) throws Exception
	{
		 ResultSet conditionVars=stat.executeQuery("select * from wf_task_condition where task_id="+task.getId()); 
		 while (conditionVars.next()) {
			 GotoItem condition=new GotoItem();
			 condition.setCondition(conditionVars.getString("condition_context"));
			 condition.setNextId(conditionVars.getString("next_task_id"));
			 condition.setTaskId((Integer.parseInt(task.getId())));
			 condition.setWfId(task.getWorkflowId());
			 task.getGotoItems().add(condition);
		 }
		 conditionVars.close();
	}
	private void loadCondition(java.sql.Statement stat,TaskNode task) throws Exception
	{
		ResultSet rsVars=stat.executeQuery("select * from wf_task_param where param_task_id="+task.getId()); 
		 while (rsVars.next()) {
			 NodeVars var=new NodeVars();
			 var.setContextvarName(rsVars.getString("param_context_var_name"));
			 var.setDatatype(rsVars.getString("param_data_type"));
			 var.setDescription(rsVars.getString("param_description"));
			 var.setInouttype(rsVars.getString("param_in_out_type"));
			 var.setTaskId(task.getId());
			 var.setValue(rsVars.getString("param_value"));
			 task.getAutoDeal().getVars().add(var);
		 }
		 rsVars.close();
	}
	public int saveTaskInstance(TaskNode task,java.sql.Statement stat,ResultSet rs ) throws Exception
	{
		int taskId=0;
		if(task.isStart()==true)
		{
			task.setStatus(1);
		}
		stat.executeUpdate(task.toNodeSqlString(),Statement.RETURN_GENERATED_KEYS);
		rs = stat.executeQuery("SELECT LAST_INSERT_ID()"); 
		 if (rs.next()) {  
			 taskId = rs.getInt(1);  
		    } 
		 rs.close();
		for(GotoItem item:task.getGotoItems())
		{
			item.setTaskId(taskId);
			saveTaskGotoItemInstance(item,stat);
		}
		for(NodeVars var:task.getNodeVars())
		{
			var.setTaskId(taskId+"");
			saveNodeVarInstance(var,stat);
		}
		return taskId;
	}
	public int saveTaskGotoItemInstance(GotoItem item,java.sql.Statement stat) throws Exception
	{
		int itemId=stat.executeUpdate(item.toSqlString(),Statement.RETURN_GENERATED_KEYS);
		return itemId;
	 
	}
	public int saveNodeVarInstance(NodeVars var,java.sql.Statement stat) throws Exception
	{
		int itemId=stat.executeUpdate(var.toSqlString(),Statement.RETURN_GENERATED_KEYS);
		return itemId;
	 
	}
	public TaskNode getTaskNode(long wfId,long taskId,long condition) throws Exception
	{
		return null;
	}
	public TaskNode getTaskNodes(Map<String,Object> params,int index,int offset) throws Exception
	{
		return null;
	}
	public int getTaskNodesCount(Map<String,Object> params) throws Exception
	{
		return 0;
	}
}
