package com.kafka.watcher.kafkawatcher.Models;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.ViewModels.ClusterViewModel;

public class Cluster extends ClusterViewModel {
    private Instant lastPoolDate;
    public Instant getLastPoolDate() {
        return lastPoolDate;
    }

    public void setLastPoolDate(Instant lastPoolDate) {
        this.lastPoolDate = lastPoolDate;
    }

    private boolean clusterStatusAvailable = false;
    public boolean isClusterStatusAvailable() {
        return clusterStatusAvailable;
    }

    public void setClusterStatusAvailable(boolean clusterStatusAvailable) {
        this.clusterStatusAvailable = clusterStatusAvailable;
    }

    private List<Topic> topics = Collections.emptyList();
    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    private List<ConsumerGroup> consumerGroups;

    public List<ConsumerGroup> getConsumerGroups() {
        if(this.consumerGroups == null) return Collections.<ConsumerGroup>emptyList();
        return consumerGroups;
    }

    public void setConsumerGroups(List<ConsumerGroup> consumerGroups) {
        this.consumerGroups = consumerGroups;
    }

    private List<Metric> clusterMetrics;
    public List<Metric> getClusterMetrics() {
        return clusterMetrics;
    }

    public void setClusterMetrics(List<Metric> clusterMetrics) {
        this.clusterMetrics = clusterMetrics;
    }

    private AdminClient adminClient;
    
    public AdminClient getAdminClient() {
        return adminClient;
    }

    public void setAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    private Kafka kafka;

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
