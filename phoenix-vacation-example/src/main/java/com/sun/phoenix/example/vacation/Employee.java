package com.sun.phoenix.example.vacation;

import java.io.Serializable;

public class Employee implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private Name name;

    private String email;

    private String managerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
}
