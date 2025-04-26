package com.kafka.watcher.kafkawatcher.api.v1.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.RequestModels.FetchMessageModel;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;
import com.kafka.watcher.kafkawatcher.Service.TopicService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/message")
public class MessageRest {
    
    private final ClusterService clusterService;
    private final TopicService topicService;

    public MessageRest(ClusterService clusterService,TopicService topicService) {
        this.clusterService = clusterService;
        this.topicService = topicService;
    }

    @PostMapping("fetch")
    public ResponseEntity<FetchMessageModel> postMethodName(@RequestBody FetchMessageModel model) {
        if(!model.isModelValid())
        {
            return ResponseEntity.internalServerError().body(model);
        }

        try {
            this.topicService.fetchMessage(model);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(model);
        }

        return ResponseEntity.ok().body(model);
    }
}
