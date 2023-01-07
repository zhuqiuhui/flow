package com.flowable.zqh;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ExternalDelegateApproved implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("Calling the external system for employee " + execution.getVariable("employee"));
	}

}
