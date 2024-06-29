package com.kafka.watcher.kafkawatcher.Models;

import java.util.List;
import java.util.Optional;

import org.apache.kafka.common.ConsumerGroupState;

public class ConsumerGroup {
    
    public ConsumerGroup(String groupId, boolean isSimpleConsumerGroup, Optional<ConsumerGroupState> state) {
        this.groupId = groupId;
        this.isSimpleConsumerGroup =isSimpleConsumerGroup;
        this.state = state;
    }
    private String groupId;
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    private boolean isSimpleConsumerGroup;
    public boolean isSimpleConsumerGroup() {
        return isSimpleConsumerGroup;
    }
    public void setSimpleConsumerGroup(boolean isSimpleConsumerGroup) {
        this.isSimpleConsumerGroup = isSimpleConsumerGroup;
    }
    private Optional<ConsumerGroupState> state;
    public Optional<ConsumerGroupState> getState() {
        return state;
    }
    public void setState(Optional<ConsumerGroupState> state) {
        this.state = state;
    }

    private List<PartitionsOffset> partitionsOffsets;
    public List<PartitionsOffset> getPartitionsOffsets() {
        return partitionsOffsets;
    }
    public void setPartitionsOffsets(List<PartitionsOffset> partitionsOffsets) {
        this.partitionsOffsets = partitionsOffsets;
    }


}