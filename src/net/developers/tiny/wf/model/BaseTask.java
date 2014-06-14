package net.developers.tiny.wf.model;

public class BaseTask {
	private int workflowId;
	private int currentWorkTaskId;
	public int getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}
	public int getCurrentWorkTaskId() {
		return currentWorkTaskId;
	}
	public void setCurrentWorkTaskId(int currentWorkTaskId) {
		this.currentWorkTaskId = currentWorkTaskId;
	}
	
}
