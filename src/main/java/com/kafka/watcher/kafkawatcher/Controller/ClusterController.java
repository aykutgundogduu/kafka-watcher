package com.kafka.watcher.kafkawatcher.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kafka.watcher.kafkawatcher.Service.ClusterService;


@Controller
@RequestMapping("/Cluster")
public class ClusterController {
    
    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }
}
