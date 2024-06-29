package com.kafka.watcher.kafkawatcher.Models;

public class BootstrapServerConfig {
    
    private Boolean isSensitive;
    private Boolean isReadOnly;
    private String Documentation;
    private String Value;
    private String Name;
    public BootstrapServerConfig(Boolean isSensitive, Boolean isReadOnly, String documentation, String value,
            String name) {
        this.isSensitive = isSensitive;
        this.isReadOnly = isReadOnly;
        Documentation = documentation;
        Value = value;
        Name = name;
    }
    public Boolean getIsSensitive() {
        return isSensitive;
    }
    public void setIsSensitive(Boolean isSensitive) {
        this.isSensitive = isSensitive;
    }
    public Boolean getIsReadOnly() {
        return isReadOnly;
    }
    public void setIsReadOnly(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    public String getDocumentation() {
        return Documentation;
    }
    public void setDocumentation(String documentation) {
        Documentation = documentation;
    }
    public String getValue() {
        return Value;
    }
    public void setValue(String value) {
        Value = value;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
}
