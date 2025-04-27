package com.kafka.watcher.kafkawatcher.Models;

public class Replica {
    private boolean isFuture;
    public boolean isFuture() {
        return isFuture;
    }
    public void setFuture(boolean isFuture) {
        this.isFuture = isFuture;
    }
    private Long offsetLag;
    public Long getOffsetLag() {
        return offsetLag;
    }
    public void setOffsetLag(Long offsetLag) {
        this.offsetLag = offsetLag;
    }
    private Long size;
    public Replica(boolean isFuture, Long offsetLag, Long size) {
        this.isFuture = isFuture;
        this.offsetLag = offsetLag;
        this.size = size;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
}
