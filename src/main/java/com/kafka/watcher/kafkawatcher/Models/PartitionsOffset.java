package com.kafka.watcher.kafkawatcher.Models;


public class PartitionsOffset {
    
    public PartitionsOffset(Long earliestOffSet, Long lastOffSet,Long committedOffset, int partition, String topicName) {
        this.lastOffSet = lastOffSet;
        this.committedOffset = committedOffset;
        this.partition = partition;
        this.topicName = topicName;
        this.earliestOffSet = earliestOffSet;
    }

    private Long committedOffset = Long.valueOf(0);
    private Long lastOffSet = Long.valueOf(0);
    private Long earliestOffSet = Long.valueOf(0);

    private int partition;
    private String topicName;
    public Long getLastOffSet() {
        return lastOffSet;
    }
    public void setLastOffSet(Long lastOffSet) {
        this.lastOffSet = lastOffSet;
    }
    public int getPartition() {
        return partition;
    }
    public void setPartition(int partition) {
        this.partition = partition;
    }
    public String getTopicName() {
        return topicName;
    }
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getTotalLag() {
        return this.lastOffSet - this.committedOffset;
    }

    public Long getCommittedOffset() {
        return committedOffset;
    }
    public void setCommittedOffset(Long committedOffset) {
        this.committedOffset = committedOffset;
    }
    public Long getEarliestOffSet() {
        return earliestOffSet;
    }
    public void setEarliestOffSet(Long earliestOffSet) {
        this.earliestOffSet = earliestOffSet;
    }

    public Long totalMessageCount(){
        return this.getCommittedOffset() - this.getEarliestOffSet();
    }
}
