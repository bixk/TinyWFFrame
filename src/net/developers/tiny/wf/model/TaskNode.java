package net.developers.tiny.wf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class TaskNode {
	public static final int S_TASK_STATUS_SUCCESS=99;
	public static final int S_TASK_STATUS_FAIL=-1;
	public static final int S_TASK_STATUS_PROCESSING=1;
	public static final int S_TASK_TYPE_START=0X01;
	public static final int S_TASK_TYPE_FINISH=0X02;
	public static final int S_TASK_TYPE_AUTO=0X03;
	public static final int S_TASK_TYPE_DECISION=0X04;
	public static final int S_TASK_TYPE_USER=0X05;
	private AutoDealSection autoDeal;
	private Map<String,TaskNode> nextTask;
	private List<GotoItem> gotoItems;
	private String id;
	private String taskOrderId;
	private String label;
	private int taskType;
	private String orgTaskType;
	private String taskTag;
	private boolean isStart;
	private int workflowId;
	private int staffId=-1;
	private int roleId=-1;
	private int status;
	public void refresh()
	{
		for(GotoItem item:this.getGotoItems())
		{
			item.setTaskId(Integer.parseInt(this.getId()));
		}
	}
	public List<NodeVars> getNodeVars()
	{
		return this.getAutoDeal().getVars();
	}
	public void initNodeVarsValue(Map<String,String> params)
	{
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry<String,String> pairs = (Map.Entry<String,String>) it.next();
			String varName=pairs.getKey();
			String varValue=pairs.getValue();
			this.setNodeVarValueByName(varName, varValue);
		}
	}
	public String getNodeVarByName(String varName) 
	{
		for(NodeVars var :this.getAutoDeal().getVars())
		{
			if(var.getContextvarName().toLowerCase().equals(varName.toLowerCase()))
			{
				return var.getValue();
			}
		}
		return null;
	}
	private boolean varExist(String varName)
	{
		boolean exist=false;
		for(NodeVars var :this.getAutoDeal().getVars())
		{
			if(var.getContextvarName().toLowerCase().equals(varName.toLowerCase()))
			{
				exist= true;
				break;
			}
		}
		return exist;
	}
	public boolean setNodeVarValueByName(String varName,String varValue)
	{
		if(varExist(varName)==true)
		{
			for(NodeVars var :this.getAutoDeal().getVars())
			{
				if(var.getContextvarName().toLowerCase().equals(varName.toLowerCase()))
				{
					 var.setValue(varValue);
					 break;
				}
			}
			 return true;
		}
		else
		{
			return false;
		}
		
	}
	public String toNodeSqlString()
	{
		StringBuffer sqlClause=new StringBuffer();
		sqlClause.append("INSERT INTO  `wf_task` (`task_tag`,`task_order_id`,`task_workflow`, `task_label`, `task_type`, `task_isstart`, `task_create_time`, `task_staff_id`, `task_role_id`, `task_status`, `task_service_clazz`,`task_service_func`) VALUES ");
		sqlClause.append("(");
		sqlClause.append("'"+this.getTaskTag()).append("',");
		sqlClause.append(this.getTaskOrderId()).append(",");
		sqlClause.append(this.getWorkflowId()).append(",");
		sqlClause.append("'"+this.getLabel()).append("',");
		sqlClause.append("'"+this.getTaskType()).append("',");
		sqlClause.append(this.isStart==true?"1":"0").append(",");
		sqlClause.append("'2014-6-13',");
		sqlClause.append(this.getStaffId()).append(",");
		sqlClause.append(this.getRoleId()).append(",");
		sqlClause.append(this.getStatus()).append(",");
		sqlClause.append("'"+this.getAutoDeal().getRunClassName()).append("',");
		sqlClause.append("'"+this.getAutoDeal().getRunFunctionName()).append("'");
		sqlClause.append(")");
		return sqlClause.toString();
	}
	public TaskNode()
	{
		nextTask=new HashMap<String,TaskNode>();
		autoDeal=new AutoDealSection();
		gotoItems=new ArrayList<GotoItem>();
		this.status=0;
	}
	 
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}
	public List<GotoItem> getGotoItems() {
		return gotoItems;
	}

	public void setGotoItems(List<GotoItem> gotoItems) {
		this.gotoItems = gotoItems;
	}

	public String getOrgTaskType() {
		return orgTaskType;
	}
	public void setOrgTaskType(String orgTaskType) {
		this.orgTaskType = orgTaskType;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public AutoDealSection getAutoDeal() {
		return autoDeal;
	}
	public void setAutoDeal(AutoDealSection autoDeal) {
		this.autoDeal = autoDeal;
	}
	public Map<String, TaskNode> getNextTask() {
		return nextTask;
	}
	public void setNextTask(Map<String, TaskNode> nextTask) {
		this.nextTask = nextTask;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getTaskType() {
		return taskType;
	}
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
	public String getTaskTag() {
		return taskTag;
	}
	public void setTaskTag(String taskTag) {
		this.taskTag = taskTag;
	}
	public String getTaskOrderId() {
		return taskOrderId;
	}
	public void setTaskOrderId(String taskOrderId) {
		this.taskOrderId = taskOrderId;
	}
	
}
