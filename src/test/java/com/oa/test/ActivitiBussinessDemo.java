package com.oa.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ActivitiBussinessDemo {

    /**
     * 添加业务的Key到Activiti的表
     */
    @Test
    public void addBusinessKey(){
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取runtimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3、启动流程的过程中，添加businessKey
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myEvection", "1001");//第一个参数：流程定义的Key，第二个参数：BusinessKey（出差申请单的id）
        //4、输出
        System.out.println(instance.getBusinessKey());
    }


    /**
     * 全部流程实例的挂起和激活
     */
    @Test
    public void suspendAllProcessInstance(){
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、查询流程定义，获取流程定义的查询对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //4、获取当前流程定义的实例是否都是挂起状态
        boolean suspended = processDefinition.isSuspended();
        //5、获取流程定义的id
        String definitionId = processDefinition.getId();
        //6、如果是激活状态，改为挂起状态
        if (suspended == true) {
            repositoryService.activateProcessDefinitionById(definitionId, true, null);//参数1：流程定义id，参数2：是否激活，参数3：激活时间
            System.out.println("流程定义id：" + definitionId + "，已激活");
        }else {
            //7、如果是挂起状态，改为激活状态
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);//参数1：流程定义id，参数2：是否咱暂定，参数3：暂停时间
            System.out.println("流程定义id：" + definitionId + "，已挂起");
        }
    }

    /**
     * 挂起，激活单个实例
     */
    @Test
    public void suspendSingleProcessInstance() {
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3、通过RuntimeService获取流程实例对象
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("5001")
                .singleResult();
        //4、得到当前流程实例的暂停状态，true-->暂停，false-->激活
        boolean suspended = instance.isSuspended();
        //5、获取流程实例id
        String instanceId = instance.getId();
        //6、判断是否已经暂停，如果已暂停，则执行激活操作
        if (suspended) {
            runtimeService.activateProcessInstanceById(instanceId);
            System.out.println("流程实例id：" + instanceId + "，已激活");
        } else {
            //7、如果是激活状态，指定暂停操作
            runtimeService.suspendProcessInstanceById(instanceId);
            System.out.println("流程实例id：" + instanceId + "，已挂起");
        }
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completeTask() {
        //1、获取流程引擎
        ProcessEngine processEngines = ProcessEngines.getDefaultProcessEngine();
        //2、获取TaskService
        TaskService taskService = processEngines.getTaskService();
        //3、使用TaskService获取任务
        Task task = taskService.createTaskQuery()
                .processInstanceId("5001")
                .taskAssignee("jerry")
                .singleResult();
        System.out.println("流程实例的id：" + task.getProcessInstanceId());
        System.out.println("流程任务：" + task.getId());
        System.out.println("负责人：" + task.getAssignee());
        System.out.println("任务名称：" + task.getName());
        //4、根据任务的id完成任务
        taskService.complete(task.getId());
    }
}
