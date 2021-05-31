package com.oa.test;

import com.activiti.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class TestCandidate {

    /**
     * 部署流程
     */
    @Test
    public void testDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-Candidate")
                .addClasspathResource("bpmn/evection-candidate.bpmn")
                .deploy();
        System.out.println("流程部署名字->" + deploy.getName());
        System.out.println("流程部署id->" + deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("testCandidate");
    }

    /**
     *查询任务候选人
     */
    @Test
    public void findGroupTaskList() {
        String key = "testCandidate";
        String candidateUser = "wangwu";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskCandidateUser(candidateUser)
                .list();
        for (Task task : taskList) {
            System.out.println("===================================");
            System.out.println("流程实例的id->" + task.getProcessInstanceId());
            System.out.println("任务id->" + task.getId());
            System.out.println("任务负责人->" + task.getAssignee());
        }

    }

    /**
     * 候选人拾取任务
     */
    @Test
    public void claimTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String taskId = "47502";
        String candidateUser = "wangwu";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(candidateUser)
                .singleResult();
        if (task != null) {
            taskService.claim(taskId, candidateUser);
            System.out.println("taskId->" + taskId + ",用户->" + candidateUser + " 拾取任务完成");
        }
    }

    /**
     * 任务归还
     */
    @Test
    public void testAssigneeToGroupTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String taskId = "47502";
        String assignee = "wangwu";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, null);
            System.out.println("taskId->" + taskId + " 归还任务完成");
        }
    }

    /**
     * 任务的交接
     */
    @Test
    public void testAssigneeToCandidateUser() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String taskId = "47502";
        String assignee = "wangwu";
        String candidateUser = "lisi";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, candidateUser);
            System.out.println("taskId->" + taskId + " 交接任务完成");
        }
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completeTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String key = "testCandidate";
        String assignee = "zhangsan";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            //根据任务id完成任务
            taskService.complete(task.getId());
        }
        System.out.println(task.getName() + " 事件已完成");
        System.out.println(task.getId() + " 此id已完成");
        System.out.println(task.getAssignee() + " 此人已完成");
    }
}
