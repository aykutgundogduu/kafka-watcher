package com.kafka.watcher.kafkawatcher.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/brokers")
public class BrokerController {
    
    private final ClusterService clusterService;

    public BrokerController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping("/{clusterName}/{brokerId}")
    public String getBroker(Model model,@PathVariable String clusterName,@PathVariable int brokerId) {
        this.clusterService.RefreshClusters();
        List<Cluster> clusters = this.clusterService.getClusters();
        model.addAttribute("Clusters", clusters);
        return "Dashboard/brokers";
    }

}
