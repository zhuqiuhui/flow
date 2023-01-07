package com.flowable.zqh;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jorge Morando
 */
public class SendRejectionEmail implements JavaDelegate {

    Logger logger = LoggerFactory.getLogger(SendRejectionEmail.class);

    public void execute(DelegateExecution execution) {
        logger.info("Rejected holidays for employee \"{}\". Mail Sent.", execution.getVariable("employee"));
    }

}