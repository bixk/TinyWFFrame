package net.developers.tiny.wf.model;

public class PublicVars {
   
	private String name;
	private String datatype;
	private String inouttype;
	private int workflowId;
	private int varId;
	private String value;
	public String toSqlString()
	{
//		INSERT INTO  `wf_param` (`param_data_type`, `param_in_out_type`, `param_workflow_id`) VALUES ('1', '1', '1');

		StringBuilder sb=new StringBuilder("INSERT INTO  `wf_param` (`param_value`,`param_name`,`param_data_type`, `param_in_out_type`, `param_workflow_id`) VALUES ");
		sb.append("(");
		sb.append("'"+value).append("',");
		sb.append("'"+name).append("',");
		sb.append("'"+datatype).append("',");
		sb.append("'"+inouttype).append("',");
		sb.append(workflowId).append("");
		sb.append(")");
		return sb.toString();
	}
	
	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public int getVarId() {
		return varId;
	}

	public void setVarId(int varId) {
		this.varId = varId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
