package com.camunda.zqh.listener;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.task.IdentityLink;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClassTeacherListener implements TaskListener {

    @Inject
    IdentityService identityService;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("ClassTeacherListener receive task......");
        Set<IdentityLink> identityLinks = delegateTask.getCandidates();
        //[IdentityLinkEntity[id=7dcdab93-caa4-11eb-9138-e454e8285e12, type=candidate, userId=null, groupId=roleClassTeacher, taskId=7dcd5d70-caa4-11eb-9138-e454e8285e12, processDefId=null, task=Task[7dcd5d70-caa4-11eb-9138-e454e8285e12], processDef=null, tenantId=null]]

        //String loggedInUserName = identityService.getCurrentAuthentication().getUserId();
        //String logPrefix = "Promotion_Inititate: loggedInUser= " + loggedInUserName;
        List<String> groupIds = identityLinks.stream().map(IdentityLink::getGroupId).collect(Collectors.toList());


    }

}
