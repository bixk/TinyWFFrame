package com.test.clazz;

import org.apache.log4j.Logger;

public class TestClzInSpring {
	static Logger logger = Logger.getLogger(TestClzInSpring.class);

public void hello(String val)
{
	logger.info("spring 托管类：TestClzInSpring返回结果："+val);
}
}
