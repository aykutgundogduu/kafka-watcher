package com.kafka.watcher.kafkawatcher.api.v1.rest;

import org.springframework.web.bind.annotation.RestController;

import com.kafka.watcher.kafkawatcher.Exceptions.ReadOnlyException;
import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/cluster")
public class ClusterRest {
    
    
    private final ClusterService clusterService;

    public ClusterRest(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping("/disk/{clusterName}")
    public Cluster getDiskUsage(@PathVariable String clusterName) {
        Cluster cluster = this.clusterService.getCluster(clusterName);
        if(cluster != null)
        {
            this.clusterService.getDiskUsage(cluster);
        }
        return cluster;
    }

    @PutMapping("/server/config/edit")
    public ResponseEntity<String> editConfig(@RequestParam String cluster,@RequestParam String server,@RequestParam String configName,@RequestParam String configValue) throws Exception {
        if(configValue.length() == 0 || cluster.length() == 0 || server.length() == 0 || configName.length() == 0) {
            return ResponseEntity.status(400).body("Cluster,server,configName and configValue is required");
        }
        this.clusterService.editServerConfig(cluster, server, configName, configValue);
        
        return ResponseEntity.ok().body("Success");
    }
}
    