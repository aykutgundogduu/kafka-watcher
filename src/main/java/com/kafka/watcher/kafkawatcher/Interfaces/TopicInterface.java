package com.kafka.watcher.kafkawatcher.Interfaces;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.MethodResult;
import com.kafka.watcher.kafkawatcher.RequestModels.FetchMessageModel;

public interface TopicInterface {
    public boolean addNewTopic(Cluster clusterName, String topicName, int partition,int replicationFactor);
    public MethodResult addNewTopic(String clusterName, String topicName, int partition,int replicationFactor);
    public boolean isTopicExists(String clusterName, String topicName);
    public MethodResult deleteTopic(String clusterName, String topicName);
    public MethodResult truncateMessages(String clusterName, String topicName);
    public MethodResult fetchMessage(FetchMessageModel model) throws Exception;
}
