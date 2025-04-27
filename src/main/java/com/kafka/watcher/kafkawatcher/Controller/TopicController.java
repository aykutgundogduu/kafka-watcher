package com.kafka.watcher.kafkawatcher.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.Topic;
import com.kafka.watcher.kafkawatcher.Service.ClusterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/topic")
public class TopicController {
    
    private final ClusterService clusterService;

    public TopicController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping("/{clusterName}/{topicName}")
    public String getTopic(Model model,@PathVariable String clusterName,@PathVariable String topicName) {
        
        try {
            Cluster cluster = this.clusterService.getClusterByName(clusterName);
            this.clusterService.RefreshCluster(cluster);
            Optional<Topic> topic = cluster.getTopics().stream().filter(f -> f.getTopicName().equals(topicName)).findFirst();
            if(!topic.isEmpty())
            {
                model.addAttribute("topic", topic.get());
                model.addAttribute("cluster", cluster);
            }
            else {
                return "redirect:/errors/404";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Topic/topic";

    }
    

}
