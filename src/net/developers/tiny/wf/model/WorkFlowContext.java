package net.developers.tiny.wf.model;

import java.util.ArrayList;
import java.util.List;

public class WorkFlowContext {
 private String label;
 private String taskType;
 private String taskTag;
 private List<TaskNode> tasks;
 private List<PublicVars> vars;
 private int businessType;
 private int businessId;
 private int workflowId;
 public PublicVars getVarDetailByKey(String key)
 {
	 for(PublicVars var:vars)
	 {
		 if(var.getName().toUpperCase().equals(key.toUpperCase().trim()))
		 {
			 return var;
		 }
	 }
	 return null;
 }
 public WorkFlowContext()
 {
	 tasks=new ArrayList<TaskNode>();
	 vars=new ArrayList<PublicVars>();
 }
public List<TaskNode> getTasks() {
	return tasks;
}
public void setTasks(List<TaskNode> tasks) {
	this.tasks = tasks;
}
public String toSqlString()
{
//	INSERT INTO  `wf_workflow` (`workflow_name`, `workflow_type`, `workflow_business_type`, `workflow_business_id`, `workflow_create_time`, `workflow_status`) VALUES ('cc1', '1', '111', '222', '2014-06-15 00:00:00', '2');
	StringBuilder sb=new StringBuilder("INSERT INTO  `wf_workflow` (`workflow_task_tag`,`workflow_name`, `workflow_type`, `workflow_business_type`, `workflow_business_id`, `workflow_create_time`, `workflow_status`) VALUES ");
	sb.append("(");
	sb.append("'"+taskTag).append("',");
	sb.append("'"+label).append("',");
	sb.append("'"+taskType).append("',");
	sb.append(""+businessType).append(",");
	sb.append(""+businessId).append(",");
	sb.append("'2014-06-15',");
	sb.append(""+1).append("");
	sb.append(")");
	return sb.toString();
}
public List<PublicVars> getVars() {
	return vars;
}
public void setVars(List<PublicVars> vars) {
	this.vars = vars;
}
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
public String getTaskType() {
	return taskType;
}
public void setTaskType(String taskType) {
	this.taskType = taskType;
}
public String getTaskTag() {
	return taskTag;
}
public void setTaskTag(String taskTag) {
	this.taskTag = taskTag;
}
public int getBusinessType() {
	return businessType;
}
public void setBusinessType(int businessType) {
	this.businessType = businessType;
}
public int getBusinessId() {
	return businessId;
}
public void setBusinessId(int businessId) {
	this.businessId = businessId;
}
public int getWorkflowId() {
	return workflowId;
}
public void setWorkflowId(int workflowId) {
	this.workflowId = workflowId;
}
 
}
