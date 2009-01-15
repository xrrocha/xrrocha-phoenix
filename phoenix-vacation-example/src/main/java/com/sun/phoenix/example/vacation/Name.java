package com.sun.phoenix.example.vacation;

import java.io.Serializable;

public class Name implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String firstName;

    private String middleName;

    private String lastName;

    public Name() {
    }

    public Name(String firstName, String lastName) {
        this(firstName, null, lastName);
    }

    public Name(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(firstName);
        if (middleName != null) {
            builder.append(' ');
            builder.append(middleName);
        }
        builder.append(' ');
        builder.append(lastName);
        return builder.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
