package net.developers.tiny.wf.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	  private static ApplicationContext applicationContext = null;  
      
	    public static ApplicationContext getApplicationContext() {  
	        if(applicationContext == null){  
	            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
	        }  
	        return applicationContext;  
	    }  
}
