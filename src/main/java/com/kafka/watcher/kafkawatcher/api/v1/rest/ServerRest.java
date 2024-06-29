package com.kafka.watcher.kafkawatcher.api.v1.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.watcher.kafkawatcher.Service.ClusterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/server")
public class ServerRest {
    
    private final ClusterService clusterService;

    public ServerRest(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

}
