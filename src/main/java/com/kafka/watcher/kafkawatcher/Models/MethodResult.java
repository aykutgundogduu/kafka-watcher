package com.kafka.watcher.kafkawatcher.Models;

public class MethodResult {

    private Boolean result = false;
    public Boolean getResult() {
        return result;
    }
    public void setResult(Boolean result) {
        this.result = result;
    }
    private String message = "";
    public MethodResult(Boolean result, String message) {
        this.result = result;
        this.message = message;
    }
    public MethodResult() {
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    
}
