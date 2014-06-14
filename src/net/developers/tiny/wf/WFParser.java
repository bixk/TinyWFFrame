package net.developers.tiny.wf;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.developers.tiny.wf.model.GotoItem;
import net.developers.tiny.wf.model.NodeVars;
import net.developers.tiny.wf.model.PublicVars;
import net.developers.tiny.wf.model.TaskNode;
import net.developers.tiny.wf.model.WorkFlowContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WFParser {
public WorkFlowContext parser(String wf) throws  Exception
{
	WorkFlowContext context=new WorkFlowContext();
	 DocumentBuilderFactory builderFactory = DocumentBuilderFactory     
	            .newInstance();     
	    DocumentBuilder builder = builderFactory.newDocumentBuilder();     
	    /*    
	     * builder.parse()方法将给定文件的内容解析为一个 XML 文档， 并且返回一个新的 DOM Document对象。    
	     */    
	    Document document = builder.parse(new File(wf));     
	    //打印document节点     
//	    printNode(document,0);  
	    
	    Element rootElement = document.getDocumentElement();
//	    System.out.println(rootElement.getNodeName());
	    context.setLabel(rootElement.getAttribute("label"));
	    context.setTaskType(rootElement.getAttribute("tasktype"));
	    context.setTaskTag(rootElement.getAttribute("tasktag"));
	    NodeList childNodes = rootElement.getChildNodes();     
	    for(int i = 0;i < childNodes.getLength();i++){ 
	    	Node childNode = childNodes.item(i); 
	    	
	    	if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("vars")){ 
	    		PublicVars var=new PublicVars();
	    		var.setName(childNode.getAttributes().getNamedItem("name").getNodeValue());
	    		var.setDatatype(childNode.getAttributes().getNamedItem("datatype").getNodeValue());
	    		var.setInouttype(childNode.getAttributes().getNamedItem("inouttype").getNodeValue());
	    		context.getVars().add(var);
	    	}
	    	else if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("task"))
	    	{
	    		 
	    		TaskNode task=new TaskNode();
	    		NamedNodeMap attrs=childNode.getAttributes(); 
	    		Node taskType=attrs.getNamedItem("tasktype");
	    		Node id=attrs.getNamedItem("id");
	    		Node label=attrs.getNamedItem("label");
	    		
	    		task.setTaskOrderId(id.getNodeValue());
   			task.setLabel(label.getNodeValue());
   			task.setOrgTaskType(taskType.getNodeValue());
   			NodeList taskChildren=childNode.getChildNodes();
	    		if(taskType.getNodeValue().equals("start"))
	    		{
	    			task.setStart(true);
	    			task.setTaskType(TaskNode.S_TASK_TYPE_START);
	   				GotoItem item=new GotoItem();
	   				Node tmp=taskChildren.item(3);
	   				NamedNodeMap nlist=tmp.getAttributes();
					item.setNextId(nlist.getNamedItem("goto").getNodeValue());
					task.getGotoItems().add(item);
	    		}
	    		else if(taskType.getNodeValue().equals("finish"))
	    		{
	    			task.setStart(false);
	    			task.setTaskType(TaskNode.S_TASK_TYPE_FINISH);
	    		}
	    		else
	    		{
	    			Node taskTag=attrs.getNamedItem("tasktag");
	    			Node taskTypeForTask=attrs.getNamedItem("tasktype");
	    			task.setTaskTag(taskTag.getNodeValue());
	    			String _nodeType=taskTypeForTask.getNodeValue().toLowerCase();
	    				
	    				int taskChildrenCnt=taskChildren.getLength();
	    				for(int c=0;c<taskChildrenCnt;++c)
	    				{
	    					if(taskChildren.item(c).getNodeName().toLowerCase().equals("autodeal"))
	    					{
	    						NodeList detailCfg=taskChildren.item(c).getChildNodes();
	    						for(int d=0;d<detailCfg.getLength();++d)
	    						{
	    							String attrType=detailCfg.item(d).getNodeName();
	    							Node _nodeVar=detailCfg.item(d);
	    							if(attrType.equals("vars"))
	    							{
	    								
	    								NodeVars nodeVar=new NodeVars();
	    								nodeVar.setName(_nodeVar.getAttributes().getNamedItem("name").getNodeValue());
	    								nodeVar.setDatatype(_nodeVar.getAttributes().getNamedItem("datatype").getNodeValue());
	    								nodeVar.setContextvarName(_nodeVar.getAttributes().getNamedItem("contextvarName").getNodeValue());
	    								nodeVar.setInouttype(_nodeVar.getAttributes().getNamedItem("inouttype").getNodeValue());
	    								nodeVar.setDescription(_nodeVar.getAttributes().getNamedItem("description").getNodeValue());
	    								task.getAutoDeal().getVars().add(nodeVar);
	    							}
	    							else if(attrType.equals("servicename"))
	    							{
	    								task.getAutoDeal().setServiceName(_nodeVar.getTextContent());
	    							}
	    							else if(attrType.equals("runclassname"))
	    							{
	    								task.getAutoDeal().setRunClassName(_nodeVar.getTextContent());
	    							}
	    							else if(attrType.equals("runfunctionname"))
	    							{
	    								task.getAutoDeal().setRunFunctionName(_nodeVar.getTextContent());
	    							}
	    						}
	    					}
	    					else if(taskChildren.item(c).getNodeName().toLowerCase().equals("gotoitem"))
	    					{
	    						GotoItem item=new GotoItem();
	    						Node condition=taskChildren.item(c).getAttributes().getNamedItem("condition");
	    						if(null!=condition)
	    						{
	    							item.setCondition(condition.getNodeValue());
	    						}
	    						item.setNextId(taskChildren.item(c).getAttributes().getNamedItem("goto").getNodeValue());
	    						task.getGotoItems().add(item);
	    					}
	    				}
	    				if(_nodeType.equals("auto"))
			    			{
			    				task.setTaskType(TaskNode.S_TASK_TYPE_AUTO);
			    				
			    			}
			    			else if(_nodeType.equals("autodecision"))
			    			{
			    				task.setTaskType(TaskNode.S_TASK_TYPE_DECISION);
			    			}
			    			else if(_nodeType.equals("user"))
			    			{
			    				task.setTaskType(TaskNode.S_TASK_TYPE_USER);
			    			}
			    			else
			    			{
			    				
			    			}
	    				
	    			
	    		}
	    		context.getTasks().add(task);
	    	}
	    }
	    return context;
}
public static void main(String[] args) throws Exception
{
	
}
public static void printNode(Node node,int count) {   
    if (node != null) {   
        String tmp = "";   
        for(int i = 0 ; i < count ; i++) tmp += "  ";   
        //获取node节点的节点类型，赋值给nodeType变量   
        int nodeType = node.getNodeType();   
        switch (nodeType) {   
            case Node.ATTRIBUTE_NODE: tmp += "ATTRIBUTE";break;   
            case Node.CDATA_SECTION_NODE: tmp += "CDATA_SECTION";break;   
            case Node.COMMENT_NODE:tmp += "COMMENT";break;   
            case Node.DOCUMENT_FRAGMENT_NODE:tmp += "DOCUMENT_FRAGMENT";break;   
            case Node.DOCUMENT_NODE:tmp += "DOCUMENT";break;   
            case Node.DOCUMENT_TYPE_NODE:tmp += "DOCUMENT_TYPE";break;   
            case Node.ELEMENT_NODE:tmp += "ELEMENT";break;   
            case Node.ENTITY_NODE:tmp += "ENTITY";break;   
            case Node.ENTITY_REFERENCE_NODE:tmp += "ENTITY_REFERENCE";break;   
            case Node.NOTATION_NODE:tmp += "NOTATION";break;   
            case Node.PROCESSING_INSTRUCTION_NODE:tmp += "PROCESSING_INSTRUCTION";break;   
            case Node.TEXT_NODE:tmp += "TEXT";break;   
            default:return;//invalid node type.   
        }   
           
        System.out.println(tmp+" ("+node.getNodeName()+","+node.getNodeValue()+")");   
        /*  
         * node.getAttributes()方法返回  
         * 包含node节点的属性的 NamedNodeMap（如果它是 Element）  
         */  
        NamedNodeMap attrs = node.getAttributes();   
        if(attrs != null)   
            for(int i = 0 ; i < attrs.getLength() ; i++){   
                printNode(attrs.item(i),count+1);   
            }   
        /*  
         * node.getChildNodes()方法返回  
         * 包含node节点的所有子节点的 NodeList。  
         */  
        NodeList childNodes = node.getChildNodes();   
        for(int i = 0 ; i < childNodes.getLength() ; i++){   
            printNode(childNodes.item(i),count+1);   
        }   
    }   
}   
}
