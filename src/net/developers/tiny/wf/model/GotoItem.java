package net.developers.tiny.wf.model;

public class GotoItem {
	private String condition;
	private String nextId;
	private int wfId;
	private int taskId;
	
	
	public String toSqlString()
	{
//		INSERT INTO `cached_frame`.`wf_task_condition` (`condition_context`, `next_task_id`, `task_id`) VALUES ('1', '1', '1');
		StringBuilder sb=new StringBuilder("INSERT INTO  `wf_task_condition` (`task_id`,`condition_context`, `next_task_id`, `wf_id`) VALUES");
		sb.append("(");
		sb.append(this.getTaskId()).append(",");
		sb.append("'"+this.getCondition()).append("',");
		sb.append(this.getNextId()).append(",");
		sb.append(this.getWfId()).append("");
		sb.append(")");
		return sb.toString();
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getNextId() {
		return nextId;
	}
	public void setNextId(String nextId) {
		this.nextId = nextId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getWfId() {
		return wfId;
	}
	public void setWfId(int wfId) {
		this.wfId = wfId;
	}
	
}
