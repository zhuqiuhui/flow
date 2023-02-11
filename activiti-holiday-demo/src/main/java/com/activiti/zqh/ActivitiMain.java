package com.activiti.zqh;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiMain {

    private static final ActivitiConfiguration activitiConfiguration = new ActivitiConfiguration();

    private static final ProcessEngine processEngine = activitiConfiguration.newProcessEngineConfiguration();

    private static final TaskService taskService = processEngine.getTaskService();

    private static final IdentityService identityService = processEngine.getIdentityService();

//    static {
//        // 建立组和用户关系（可使用 candidateGroups 标签，这里不做示例了）
//        identityService.createMembership("方辰", "employee");
//        identityService.createMembership("蒋凡", "project manager");
//        identityService.createMembership("蒋芳", "HR");
//        identityService.createMembership("马云", "CEO");
//    }

    public static void main(String[] args) {

        // 1. 初始化 ProcessEngine
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 2. 流程部署
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("holiday.bpmn")
                .name("流程测试")
                .category("")
                .deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        System.out.println("流程名称 ： [" + processDefinition.getName() + "]， 流程ID ： ["
                + processDefinition.getId() + "], 流程KEY : " + processDefinition.getKey());


        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 3. 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("fcHolidaySystem");//返回一个流程实例对象
        System.out.println("流程实例ID = " + processInstance.getId());
        System.out.println("正在活动的流程节点ID = " + processInstance.getActivityId());
        System.out.println("流程定义ID = " + processInstance.getProcessDefinitionId());

        // Step 1：发请请假任务
        Task holidayRequestTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskAssignee("方辰").singleResult();
        System.out.println("发请请假任务ID：" + holidayRequestTask.getId());
        taskService.complete(holidayRequestTask.getId());

        // Step 2：项目经理同意
        findAndFinishTask(processInstance.getId(), "蒋凡", true);

        // Step 3：人事经理不同意
        findAndFinishTask(processInstance.getId(), "蒋芳", false);

        // Step 4：再次修改申请
        Task editHolidayRequestTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskAssignee("方辰").singleResult();
        Map<String, Object> variables = new HashMap<>();
        variables.put("msg", "通过");
        taskService.complete(editHolidayRequestTask.getId(), variables);
        System.out.println("方辰重新提交了请假任务 editHolidayRequestTask id = " + editHolidayRequestTask.getId());

        // Step 5：项目经理同意
        findAndFinishTask(processInstance.getId(), "蒋凡", true);

        // Step 6：人事经理同意
        findAndFinishTask(processInstance.getId(), "蒋芳", true);

        // Step 7：总经理同意
        findAndFinishTask(processInstance.getId(), "CEO", true);


        // 历史日志打印
        logHistoryActivities(true);

    }

    private static void findAndFinishTask(String processInstanceId, String assignee, boolean approve) {
        System.out.println("-----------------------------");
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assignee).singleResult();
        System.out.println("查询到当前待完成的任务 task = " + task);
        if(task == null) {
            System.out.println(assignee + "当前无任务可完成");
            return;
        }
        Map<String, Object> variables = new HashMap<>();
        if(approve) {
            variables.put("msg", "通过");
        } else {
            variables.put("msg", "未通过");
        }
        taskService.complete(task.getId(), variables);
        System.out.println(assignee + "完成任务：" + task.getName() + " " + variables.get("msg"));
        System.out.println("-----------------------------");
    }


    private static void logHistoryActivities(boolean isQueryFinish) {
        List<HistoricActivityInstance> historicActivityInstances = new ArrayList<>();
        if (isQueryFinish) {
            historicActivityInstances = processEngine.getHistoryService()
                    // 创建历史活动实例查询
                    .createHistoricActivityInstanceQuery()
                    .finished() // 查询已经完成的任务
                    .orderByHistoricActivityInstanceEndTime()
                    .asc()
                    .list();
        } else {
            historicActivityInstances = processEngine.getHistoryService()
                    // 创建历史活动实例查询
                    .createHistoricActivityInstanceQuery()
                    .orderByHistoricActivityInstanceEndTime()
                    .asc()
                    .list();
        }
        System.out.println("===========================");
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            System.out.println("任务ID:" + historicActivityInstance.getId());
            System.out.println("流程实例ID:" + historicActivityInstance.getProcessInstanceId());
            System.out.println("活动名称：" + historicActivityInstance.getActivityName());
            System.out.println("办理人：" + historicActivityInstance.getAssignee());
            System.out.println("开始时间：" + historicActivityInstance.getStartTime());
            System.out.println("结束时间：" + historicActivityInstance.getEndTime());
            System.out.println("===========================");
        }
    }

}
