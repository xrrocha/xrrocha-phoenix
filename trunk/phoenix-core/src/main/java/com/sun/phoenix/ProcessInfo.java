/**
 * 
 */
package com.sun.phoenix;

public class ProcessInfo {
    private String uri;

    private ProcessStatus status;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}