package net.developers.tiny.wf.model;

public class NodeVars {
	private String name;
	private String datatype;
	private String inouttype;
	private String contextvarName;
	private String description;
	private String value;
	private String taskId;
	private int id;
	public String toSqlString()
	{
//		INSERT INTO  `wf_task_param` (`param_value`,`param_data_type`, `param_context_var_name`, `param_description`, `param_in_out_type`) VALUES ('1', '1', '1', '1');
		StringBuilder sb=new StringBuilder("INSERT INTO  `wf_task_param` (`param_value`,`param_task_id`,`param_data_type`, `param_context_var_name`, `param_description`, `param_in_out_type`) VALUES ");
		sb.append("(");
		sb.append("'"+value).append("',");
		sb.append(this.getTaskId()).append(",");
		sb.append("'"+datatype).append("',");;
		sb.append("'"+contextvarName).append("',");
		sb.append("'"+description).append("',");
		sb.append("'"+inouttype).append("'");
		sb.append(")");
		return sb.toString();
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getInouttype() {
		return inouttype;
	}
	public void setInouttype(String inouttype) {
		this.inouttype = inouttype;
	}
	public String getContextvarName() {
		return contextvarName;
	}
	public void setContextvarName(String contextvarName) {
		this.contextvarName = contextvarName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
