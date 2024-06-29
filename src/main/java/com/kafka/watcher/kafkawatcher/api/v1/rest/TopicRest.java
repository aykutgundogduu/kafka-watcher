package com.kafka.watcher.kafkawatcher.api.v1.rest;

import org.springframework.web.bind.annotation.RestController;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.MethodResult;
import com.kafka.watcher.kafkawatcher.Models.Topic;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;
import com.kafka.watcher.kafkawatcher.Service.TopicService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/v1/topic")
public class TopicRest {
    
    private final TopicService topicService;
    private final ClusterService clusterService;

    public TopicRest(TopicService topicService, ClusterService clusterService) {
        this.topicService = topicService;
        this.clusterService = clusterService;
    }

    @PostMapping("add")
    public ResponseEntity<String> addNewTopic(@RequestParam List<String> clusterList, @RequestParam String topicName, @RequestParam int partitionSize, @RequestParam int replicationFactor) {
        boolean result = false;
        for (String cluster : clusterList) {
            if(this.topicService.isTopicExists(cluster, topicName))
            {
                return ResponseEntity.badRequest().body(String.format("Topic is %s already exists in %s", topicName,cluster));
            }
            MethodResult methodResult = this.topicService.addNewTopic(cluster, topicName, partitionSize,replicationFactor);
            result = methodResult.getResult();
            if(!methodResult.getResult()){
                return ResponseEntity.internalServerError().body(String.format("We got error while adding topics of %s to %s. Please check logs. %s",topicName, cluster,methodResult.getMessage()));
            }
        }

        if(result)
        {
            return ResponseEntity.ok().body(String.format("Created new topic to cluster %s", clusterList));
        }
        return ResponseEntity.badRequest().body("Invalid request");
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteTopic(@RequestParam String clusterName, @RequestParam String topicName)
    {
        MethodResult result = this.topicService.deleteTopic(clusterName, topicName);
        if(result.getResult())
        {
            return ResponseEntity.ok().body(String.format("%s is deleted from %s",topicName,clusterName));
        }
        return ResponseEntity.badRequest().body(result.getMessage());
    }
    
    @PostMapping("truncateMessages")
    public ResponseEntity<String> truncateMessages(@RequestParam String clusterName,@RequestParam String topicName) {

        
        if(topicService.isTopicExists(clusterName, topicName))
        {
            MethodResult result = this.topicService.truncateMessages(clusterName, topicName);
            if(result.getResult())
            {
                return ResponseEntity.ok().body(String.format("Message is removed from %s at %s",topicName,clusterName));
            }
            return ResponseEntity.badRequest().body(result.getMessage());
       }
       return ResponseEntity.badRequest().body(String.format("%s is not exists in %s",topicName,clusterName));

    }
    

    @PostMapping("recreateTopic")
    public ResponseEntity<String> recreateTopic(@RequestParam String clusterName, @RequestParam String topicName) throws Exception
    {
        boolean result = false;
        if(!this.topicService.isTopicExists(clusterName, topicName))
        {
            return ResponseEntity.badRequest().body(String.format("Topic is %s not exists in %s", topicName,clusterName));
        }
        Cluster cluster = this.clusterService.getClusterByName(clusterName);
        Topic topic = this.clusterService.getTopic(cluster, topicName);
        System.out.println(topic.getReplicationFactorCount() + " - " + topic.getPartitions().size() + " - " + topic.getTotalMessageCount());
        MethodResult methodResult = new MethodResult(false, "test message");
        if(!methodResult.getResult()){
            return ResponseEntity.internalServerError().body(String.format("We got error while adding topics of %s to %s. Please check logs. %s",topicName, cluster,methodResult.getMessage()));
        }

        if(result)
        {
            return ResponseEntity.ok().body(String.format("%s of topic is recreated for %s.",topicName,clusterName));
        }
        return ResponseEntity.badRequest().body("Invalid request");
    }
}
