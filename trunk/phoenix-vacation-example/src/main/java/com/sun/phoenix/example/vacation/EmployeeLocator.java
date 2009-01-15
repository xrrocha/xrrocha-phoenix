package com.sun.phoenix.example.vacation;

import java.util.Map;

public class EmployeeLocator {
    private Map<String, Employee> employees;
    
    public Employee locateEmployee(String employeeId) {
        return employees.get(employeeId);
    }

    public Map<String, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, Employee> employees) {
        this.employees = employees;
    }
}
