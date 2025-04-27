package com.kafka.watcher.kafkawatcher.Models;

public class Records {

    private Integer leaderEpoch;
    private Long offset;
    private int partition;
    private String topicName;
    private String value;

    public Records(Integer leaderEpoch, Long offset, int partition, String topicName, String value)
    {
        this.leaderEpoch = leaderEpoch;
        this.offset = offset;
        this.partition = partition;
        this.topicName = topicName;
        this.value = value;

    }

    public Integer getLeaderEpoch() {
        return leaderEpoch;
    }
    public void setLeaderEpoch(Integer leaderEpoch) {
        this.leaderEpoch = leaderEpoch;
    }
    public Long getOffset() {
        return offset;
    }
    public void setOffset(Long offset) {
        this.offset = offset;
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
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
