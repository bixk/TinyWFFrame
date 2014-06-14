package net.developers.tiny.wf.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.developers.tiny.wf.model.PublicVars;
import net.developers.tiny.wf.model.WorkFlowContext;

public class WorkFlowDAO {
	public void processWorkFlow(int workflowId,int status,java.sql.Statement stat) throws Exception
	{
		stat.executeUpdate("update wf_workflow set workflow_status="+status+" where workflow_id="+workflowId);
	}
	public List<WorkFlowContext> getWorkFlowsToProcess(java.sql.Statement stat) throws Exception
	{
		StringBuffer sb=new StringBuffer("select * from wf_workflow where workflow_status=1");
		return getWorkFlows(sb.toString(),stat);
	}
	public WorkFlowContext getWorkFlowsInfo(int workflowId,java.sql.Statement stat) throws Exception
	{
		StringBuffer sb=new StringBuffer("select * from wf_workflow where workflow_status=1 and workflow_id="+workflowId);
		List<WorkFlowContext> contexts=getWorkFlows(sb.toString(),stat);
		if(null!=contexts&&contexts.size()>0)
		{
			return contexts.get(0);
		}
		else
		{
			return null;
		}
	}
	private List<WorkFlowContext> getWorkFlows(String sql,java.sql.Statement stat) throws Exception
	{
		List<WorkFlowContext> wfs=new ArrayList<WorkFlowContext>();
		ResultSet rs=stat.executeQuery(sql); 
		 while (rs.next()) { 
			 WorkFlowContext context=new WorkFlowContext();
			 context.setBusinessId(rs.getInt("workflow_business_id"));
			 context.setBusinessType(rs.getInt("workflow_business_type"));
			 context.setLabel(rs.getString("workflow_name"));
			 context.setTaskTag(rs.getString("workflow_task_tag"));
			 context.setTaskType(rs.getString("workflow_type"));
			 context.setWorkflowId(rs.getInt("workflow_id"));
			
			 wfs.add(context);
		    } 
		 rs.close();
		 for(WorkFlowContext wf:wfs)
		 {
		 loadWorkflowVars(wf,stat);
		 }
		 return wfs;
	}
    public void loadWorkflowVars(WorkFlowContext wf,java.sql.Statement stat) throws Exception
    {
    	 ResultSet rs=stat.executeQuery("select * from wf_param where param_workflow_id="+wf.getWorkflowId()); 
		 while (rs.next()) {
			 PublicVars var=new PublicVars();
			 var.setDatatype(rs.getString("param_data_type"));
			 var.setInouttype(rs.getString("param_in_out_type"));
			 var.setName(rs.getString("param_name"));
			 var.setVarId(rs.getInt("param_id"));
			 var.setValue(rs.getString("param_value"));
			 var.setWorkflowId(rs.getInt("param_workflow_id"));
			 wf.getVars().add(var);
		 }
		 rs.close();
    }
	public int createWorkFlowInstance(WorkFlowContext wf,java.sql.Statement stat,ResultSet rs) throws Exception
	{
		int wfId= stat.executeUpdate(wf.toSqlString(),Statement.RETURN_GENERATED_KEYS);
		rs = stat.executeQuery("SELECT LAST_INSERT_ID()"); 
		 if (rs.next()) {  
			 wfId = rs.getInt(1);  
		    } 
		 rs.close();
		 for(PublicVars var:wf.getVars())
		 {
			 var.setWorkflowId(wfId);
			 saveWorkFlowVarsInstance(var,stat);
		 }
		 return wfId;
	}
	public void saveWorkFlowVarsInstance(PublicVars var,java.sql.Statement stat) throws Exception
	{
		 stat.executeUpdate(var.toSqlString(),Statement.RETURN_GENERATED_KEYS);
	}
}
