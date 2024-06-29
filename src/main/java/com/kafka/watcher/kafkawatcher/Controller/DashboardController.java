package com.kafka.watcher.kafkawatcher.Controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;

@Controller
@RequestMapping("/Dashboard")
public class DashboardController {

    private final ClusterService clusterService;

    public DashboardController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }


    @GetMapping("/cluster")
    public String Index(Model model) {
        this.clusterService.RefreshClusters();
        List<Cluster> clusters = this.clusterService.getClusters();
        model.addAttribute("Clusters", clusters);
        return "Dashboard/cluster";
        
    }

    

    @GetMapping("/cluster/{clusterName}")
    public String getMethodName(Model model,@PathVariable String clusterName) {
        if(clusterName.equals("all"))
        {
            return "redirect:/Dashboard/cluster";
        }
        Cluster cluster = this.clusterService.getCluster(clusterName);
        this.clusterService.RefreshCluster(cluster);
        List<Cluster> clusters = Collections.singletonList(cluster);
        model.addAttribute("Clusters", clusters);
        return "Dashboard/cluster";
    }


    @GetMapping("/topics")
    public String getMethodName(Model model) {
        this.clusterService.RefreshClusters();
        List<Cluster> clusters = this.clusterService.getClusters();
        model.addAttribute("Clusters", clusters);
        return "Dashboard/topics";
    }
    
    @GetMapping("/brokers")
    public String getBrokers(Model model) {
        this.clusterService.RefreshClusters();
        List<Cluster> clusters = this.clusterService.getClusters();
        model.addAttribute("Clusters", clusters);
        return "Dashboard/brokers";
    }
}
