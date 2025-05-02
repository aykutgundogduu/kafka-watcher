package com.kafka.watcher.kafkawatcher.RequestModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kafka.watcher.kafkawatcher.Models.Records;

public class FetchMessageModel {
    
    private Long startOffset = 0L;
    private Long endOffset = 1000L;
    private String topicName = "";
    private String clusterName = "";
    private String searchString = "";
    private List<Records> messages = new ArrayList<>();
    private int startIndex = 0;
    private int countRow = 100;
    private int resultCount = 0;

    public int getResultCount() {
        return resultCount;
    }

    private void setResultCount() {
        this.resultCount = this.getMessages().size();
    }

    public int getCalculatedEndIndex() {
        int nextCount = getCalculatedStartIndex() + countRow;
        return nextCount;
    }

    public int getCountRow() {
        return countRow;
    }

    public void setCountRow(int countRow) {
        if(countRow == 0) return;
        this.countRow = countRow;
    }

    public int getCalculatedStartIndex() {
        int beforeCount = countRow * startIndex;
        return beforeCount;
    }
    
    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public List<Records> getMessages() {
        return messages;
    }

    public void setMessages(List<Records> messages) {
        this.messages = messages;
        this.setResultCount();
    }

    public FetchMessageModel appendMessage(Records record){
        this.getMessages().add(record);
        this.setResultCount();
        return this;
    }

    public Long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Long startOffset) {
        if(startOffset == null) return;
        if(startOffset == 0 )return;
        this.startOffset = startOffset;
    }

    public Long getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Long endOffset) {
        if(endOffset == null) return;
        this.endOffset = endOffset;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isModelValid()
    {
        if(this.getClusterName().isEmpty() || this.getClusterName().isBlank() || this.getTopicName().isEmpty() || this.getTopicName().isBlank())
        {
            return false;
        }
        return true;
        
    }

    public void clearMessages()
    {
        this.getMessages().clear();
    }
}
