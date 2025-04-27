package com.kafka.watcher.kafkawatcher.Interfaces;


import java.util.List;
import java.util.concurrent.TimeUnit;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.ConsumerGroup;
import com.kafka.watcher.kafkawatcher.Models.Kafka;
import com.kafka.watcher.kafkawatcher.Models.Topic;

public interface ClusterInterface {
    public Kafka getKafka(Cluster cluster) throws Exception;
    public Cluster getDiskUsage(Cluster cluster);
    public Cluster getProducers(Cluster cluster);
    public Cluster getMetrics(Cluster cluster);
    public Cluster getPartitions(Cluster cluster);
    public Cluster getTopics(Cluster cluster);
    public Topic getTopic(Cluster cluster,String topicName);
    public List<ConsumerGroup> getConsumerGroups(Cluster cluster);
    public Cluster getBootstrapServers(Cluster cluster);
    public void RefreshCluster(Cluster cluster);
    public void RefreshClusters();
    public Cluster getCluster(String clusterName);
    public Cluster getConsumerGroupsOffset(Cluster cluster);
    public boolean editServerConfig(String clusterName,String serverName,String configName,String configValue) throws Exception;
    public Cluster getClusterByName(String clusterName) throws Exception;
    public boolean checkClusterStatus(Cluster cluster) throws Exception;
    public boolean checkClusterStatus(Cluster cluster,int unit,TimeUnit unitType) throws Exception;
    public void applyPartitionsToNodes(Cluster cluster);
}
