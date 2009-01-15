package com.sun.phoenix.example.vacation;

import java.io.Serializable;

public class ManagerReply implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean approved;

    private String comments;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
