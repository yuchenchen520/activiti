package com.oa.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.junit.Test;

import javax.print.attribute.standard.PresentationDirection;

public class TestCreate {

    /**
     * 使用activiti提供的默认的方式来创建mysql表
     */
    @Test
    public void testCreateDbTable(){
        //需要使用activiti提供的工具类
        //getDefaultProcessEngine会默认从resources下读取名字为activiti.cfg.xml的文件
        //创建processEngine时就会创建mysql的表

        //创建工作流程引擎的两种方式

        //1、默认的方式
        //ProcessEngine processEngines = ProcessEngines.getDefaultProcessEngine();
        //RepositoryService repositoryService = processEngines.getRepositoryService();
        //repositoryService.createDeployment();

        //1、自定义的方式，配置文件的名字可以自定义,bean的名字也可以自定义
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");

        //获取流程引擎对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println(processEngine);
    }
}
