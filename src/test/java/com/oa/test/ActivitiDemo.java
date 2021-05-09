package com.oa.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ActivitiDemo {

    /**
     * 测试流程部署
     */
    @Test
    public void testDeployment(){
        //1、创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、使用service进行流程的部署，定义一个名字，把bpmn和png部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请")
                .addClasspathResource("bpmn/evection.bpmn")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
        //测试输出
        System.out.println("流程部署id--->" + deploy.getId());
        System.out.println("流程部署名字--->" + deploy.getName());
    }


    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess(){
        //1、创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3、根据流程定义的id启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myEvection");

        //测试输出
        System.out.println("\n流程定义id--->" + instance.getProcessDefinitionId());
        System.out.println("流程实例id--->" + instance.getId());
        System.out.println("当前活动的id--->" + instance.getActivityId());
    }


    /**
     * 查询个人待执行的任务
     */
    @Test
    public void testFindPersonalTaskList(){
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取taskService
        TaskService taskService = processEngine.getTaskService();
        //3、根据流程key和任务的负责人查询任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")//流程key
                .taskAssignee("zhangsan")//要查询的负责人
                .list();

        //测试输出
        for (Task task : taskList) {
            System.out.println("\n流程实例id--->" + task.getProcessInstanceId());
            System.out.println("任务id--->" + task.getId());
            System.out.println("任务负责人--->" + task.getAssignee());
            System.out.println("任务名称--->" + task.getName());
        }
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completeTask(){
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取Service
        TaskService taskService = processEngine.getTaskService();
        //完成任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")
                .taskAssignee("zhangsan")
                .singleResult();
        //Task task = taskService.createTaskQuery()
        //        .processDefinitionKey("myEvection")
        //        .taskAssignee("jerry")
        //        .singleResult();
        //Task task = taskService.createTaskQuery()
        //        .processDefinitionKey("myEvection")
        //        .taskAssignee("jack")
        //        .singleResult();
        //Task task = taskService.createTaskQuery()
        //        .processDefinitionKey("myEvection")
        //        .taskAssignee("rose")
        //        .singleResult();
        //根据任务id完成任务
        taskService.complete(task.getId());
    }


    /**
     * 使用zip包进行批量的部署
     */
    @Test
    public void deployProcessByZip(){
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //流程部署
        //读取资源包文件，构造成inputStream
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        //用inputStream构造ZipInputStream
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //使用压缩包的流进行流程的部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("\n流程部署id--->" + deploy.getId());
        System.out.println("流程部署的名字--->" + deploy.getName());
    }


    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService接口
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取ProcessDefinitionQuery对象
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        //查询当前所有的定义
        //processDefinitionKey流程定义key
        //orderByProcessDefinitionVersion进行排序，desc倒序
        List<ProcessDefinition> definitionList = definitionQuery.processDefinitionKey("myEvection")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        //输出信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("\n流程定义id--->" + processDefinition.getId());
            System.out.println("流程定义名字--->" + processDefinition.getName());
            System.out.println("流程定义Key--->" + processDefinition.getKey());
            System.out.println("流程定义版本--->" + processDefinition.getVersion());
            System.out.println("流程部署id--->" + processDefinition.getDeploymentId());
        }
    }


    /**
     * 删除流程部署信息
     * 如果当前流程未完成，想要删除需要特殊方式，原理：级联删除
     */
    @Test
    public void deleteDeployment(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //通过部署id来删除流程部署信息
        //普通删除方式，通过部署id
        String deploymentId = "1";
        //repositoryService.deleteDeployment(deploymentId);
        //级联删除
        repositoryService.deleteDeployment(deploymentId, true);
    }
}
