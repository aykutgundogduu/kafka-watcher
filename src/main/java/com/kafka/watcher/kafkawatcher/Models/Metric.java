package com.kafka.watcher.kafkawatcher.Models;

public class Metric {
    
    public Metric(String name, Object value) {
        this.Name = name;
        this.Value = value;
    }
    
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    
    private Object Value;


    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }
}
