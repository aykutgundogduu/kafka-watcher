package com.kafka.watcher.kafkawatcher.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.kafka.common.Uuid;


public class Topic {
    public Topic(){
        
    }
    public Topic(String topicName,Uuid topicId,boolean isInternal){
        this.setTopicName(topicName);
        this.setIsInternal(isInternal);
        this.setTopicId(topicId);
    }
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }    
    private List<Partition> partitions = Collections.emptyList();

    public List<Partition> getPartitions() {
        return partitions;
    }
    public void setPartitions(List<Partition> partitions) {
        this.partitions = partitions;
    }
    private Uuid topicId;
    public void setTopicId(Uuid topicId) {
        this.topicId = topicId;
    }
    public Uuid topicId() {
        return this.topicId;
     }
     private boolean internal;
     public void setIsInternal(boolean internal) {
        this.internal = internal;
    }
    public boolean isInternal() {
        return this.internal;
    }
    public void setOffset(PartitionsOffset offset) {
        this.getPartitions().stream().filter(f -> f.getPartitionId() == offset.getPartition()).forEach(p -> p.setPartitionsOffset(offset));
    }

    public Long getTotalMessageCount()
    {
        return this.getPartitions().stream().filter(f -> Objects.nonNull(f.getPartitionsOffset())).mapToLong(p -> p.getPartitionsOffset().totalMessageCount()).sum();
    }

    public Long getTotalReplicaMessageSize()
    {
        return this.getPartitions().stream().mapToLong(f -> f.getReplicaSize()).sum();
    }
    public int getReplicationFactorCount()
    {
        return this.getPartitions().stream().mapToInt(m -> m.getReplicaNodes().size()).max().getAsInt();
    }
}
