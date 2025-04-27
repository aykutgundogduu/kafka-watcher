package com.kafka.watcher.kafkawatcher.Models;

public class Producer {

    private Topic topic;
    public Topic getTopic() {
        return topic;
    }
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    private Long producerId;
    public Long getProducerId() {
        return producerId;
    }
    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }
    private int producerEpoch;
    public int getProducerEpoch() {
        return producerEpoch;
    }
    public void setProducerEpoch(int producerEpoch) {
        this.producerEpoch = producerEpoch;
    }
    private Long currentTransactionStartOffset;
    public Long getCurrentTransactionStartOffset() {
        return currentTransactionStartOffset;
    }
    public void setCurrentTransactionStartOffset(Long currentTransactionStartOffset) {
        this.currentTransactionStartOffset = currentTransactionStartOffset;
    }
    private int sequence;
    public Producer(Long producerId, int producerEpoch, Long currentTransactionStartOffset, int sequence,Topic topic) {
        this.producerId = producerId;
        this.producerEpoch = producerEpoch;
        this.currentTransactionStartOffset = currentTransactionStartOffset;
        this.sequence = sequence;
        this.topic = topic;
    }
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
