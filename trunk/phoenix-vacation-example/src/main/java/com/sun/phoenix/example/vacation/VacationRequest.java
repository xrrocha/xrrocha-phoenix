package com.sun.phoenix.example.vacation;

import java.io.Serializable;
import java.util.Date;

import com.sun.phoenix.util.beanform.ScalarProperty;

public class VacationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private Date startDate;
    private Date endDate;
    private String comments;
    
    public String getEmployeeId() {
        return employeeId;
    }

    @ScalarProperty(
		prompt = "Employee id",
		fyi = "Enter the employee identification number",
		length = 32)
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    @ScalarProperty(
    	prompt = "Vacation start date",
    	fyi = "Enter the start date of the vacation period in format 'MM/dd/yyyy'",
    	length = 10)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @ScalarProperty(
        	prompt = "Vacation end date",
        	fyi = "Enter the end date of the vacation period in format 'MM/dd/yyyy'",
        	length = 10)
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getComments() {
        return comments;
    }

    @ScalarProperty(
        	prompt = "Comments",
        	fyi = "Enter optional comments to describe this vacation request",
        	length = 24,
        	numLines = 4)
    public void setComments(String comments) {
        this.comments = comments;
    }
}
