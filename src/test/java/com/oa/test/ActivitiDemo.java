package com.oa.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
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

    /**
     * 下载资源文件
     * 方案1：使用Activiti提供的api下载资源文件
     * 方案2：自己写代码从数据库中下载文件，使用jdbc对blob/clob类型数据读取出来，保存到文件目录，解决io操作：commons-io.jar
     * 这里使用方案1，Activiti提供的api，RepositoryService
     */
    @Test
    public void getDeployment() throws IOException {
        //1.得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取api，RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.获取查询对象ProcessDefinitionQuery，查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //4.通过流程定义信息，获取部署ID
        String depolymentId = processDefinition.getDeploymentId();
        //5.通过RepositoryService，传递部署id参数，读取资源信息（png和bpmn）
        //5.1获取png图片的流
        String pngName = processDefinition.getDiagramResourceName();//从流程定义中，获取png图片的目录和名字
        InputStream pngInput = repositoryService.getResourceAsStream(depolymentId, pngName);//通过部署id和文件名字来获取图片的资源
        //5.2获取bpmn的流
        String bpmnName = processDefinition.getResourceName();
        InputStream bpmnInput = repositoryService.getResourceAsStream(depolymentId, bpmnName);
        //6.构造OutputStream流
        File pngFile = new File("D:\\git仓库\\activiti\\src\\main\\resources\\bpmn\\evectionflow01.png");
        File bpmnFile = new File("D:\\git仓库\\activiti\\src\\main\\resources\\bpmn\\evectionflow01.bpmn");
        FileOutputStream pngOutStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutStream = new FileOutputStream(bpmnFile);
        //7.输入流，输出流的转换
        IOUtils.copy(pngInput, pngOutStream);
        IOUtils.copy(bpmnInput, bpmnOutStream);
        //8.关闭流
        pngInput.close();;
        pngOutStream.close();
        bpmnInput.close();
        bpmnOutStream.close();
    }
}
