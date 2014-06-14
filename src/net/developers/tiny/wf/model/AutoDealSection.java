package net.developers.tiny.wf.model;

import java.util.ArrayList;
import java.util.List;

public class AutoDealSection {
	private List<NodeVars> vars;
	private String runType;
	private String serviceName;
	private String runClassName;
	private String runFunctionName;
	public AutoDealSection()
	{
		vars=new ArrayList<NodeVars>();
	}
	public List<NodeVars> getVars() {
		return vars;
	}
	public void setVars(List<NodeVars> vars) {
		this.vars = vars;
	}
	public String getRunType() {
		return runType;
	}
	public void setRunType(String runType) {
		this.runType = runType;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getRunClassName() {
		return runClassName;
	}
	public void setRunClassName(String runClassName) {
		this.runClassName = runClassName;
	}
	public String getRunFunctionName() {
		return runFunctionName;
	}
	public void setRunFunctionName(String runFunctionName) {
		this.runFunctionName = runFunctionName;
	}
	
}
