package com.kafka.watcher.kafkawatcher.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.kafka.clients.admin.ReplicaInfo;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;

public class Partition {

    private int partitionId;
    public int getPartitionId() {
        return partitionId;
    }
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }
    private List<Node> replicaNodes;
    public List<Node> getReplicaNodes() {
        return replicaNodes;
    }
    public void setRepliceNodes(List<Node> replicaNodes) {
        this.replicaNodes = replicaNodes;
    }
    private List<Node> isr;
    public List<Node> getIsr() {
        return isr;
    }
    public void setIsr(List<Node> isr) {
        this.isr = isr;
    }
    private Node leader;
    public Node getLeader() {
        return leader;
    }
    public void setLeader(Node leader) {
        this.leader = leader;
    }
    private List<Producer> producers = Collections.emptyList();
    
    public List<Producer> getProducers() {
        return producers;
    }
    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }
    public Partition(int partitionId, List<Node> replicaNodes, List<Node> isr, Node leader) {
        this.partitionId = partitionId;
        this.replicaNodes = replicaNodes;
        this.isr = isr;
        this.leader = leader;
    }

    private PartitionsOffset partitionsOffset;
    public PartitionsOffset getPartitionsOffset() {
        return partitionsOffset;
    }
    public void setPartitionsOffset(PartitionsOffset partitionsOffset) {
        this.partitionsOffset = partitionsOffset;
    }

    
    private List<Replica> replicas = new ArrayList<>();


    public List<Replica> getReplicas() {
        return replicas;
    }
    public void clearReplicas(){
        this.replicas = new ArrayList<>();
    }
    public void addReplicas(List<Replica> replicas) {
        this.replicas.addAll(replicas);
    }
    public void setReplicas(List<Replica> replicas) {
        this.replicas= replicas;
    }
    public void addReplicas(Replica replica) {
        this.replicas.add(replica);
    }
    public void addReplicas(Entry<TopicPartition, ReplicaInfo> replicaInfo) {
        if(replicaInfo.getKey().partition() == this.getPartitionId())
        {
            this.addReplicas(new Replica(replicaInfo.getValue().isFuture(), replicaInfo.getValue().offsetLag(),replicaInfo.getValue().size()));
        }
    }

    public Long getReplicaSize(){
        return this.getReplicas().stream().mapToLong(m -> m.getSize()).sum();
    }


}
