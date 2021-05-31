package com.oa.test;

import com.activiti.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;

public class TestVariablesComplete {

    /**
     * 流程部署
     */
    @Test
    public void testDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-variables-complete")
                .addClasspathResource("bpmn/evection-global.bpmn")
                .deploy();
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名字=" + deploy.getName());
    }


    /**
     * 启动流程的时候设置流程变量
     * 设置流程变量num
     * 设置任务负责人
     */
    @Test
    public void testStartProcess(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //流程定义的key
        String key = "myEvection2";
        //流程变量的map
        HashMap<String, Object> variables = new HashMap<>();
        //设定任务的负责人
        variables.put("assignee0", "李四3");
        variables.put("assignee1", "王经理3");
        variables.put("assignee2", "杨总经理3");
        variables.put("assignee3", "张财务3");
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);

        System.out.println("id->" + processInstance.getId());
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completeTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //流程定义的key
        String key = "myEvection2";
        //设置流程变量
        Evection evection = new Evection();
        evection.setNum(2d);
        HashMap<String, Object> map = new HashMap<>();
        map.put("evection", evection);

        //任务负责人
        String assignee = "王经理3";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            //根据任务id完成任务
            taskService.complete(task.getId(), map);
        }

        System.out.println(task.getName() + " 已完成");
    }
}
