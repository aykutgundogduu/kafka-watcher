package com.kafka.watcher.kafkawatcher.ViewModels;

import java.util.List;
import java.util.Objects;

import com.kafka.watcher.kafkawatcher.Models.ConsumerGroup;
import com.kafka.watcher.kafkawatcher.Models.Kafka;
import com.kafka.watcher.kafkawatcher.Models.Partition;
import com.kafka.watcher.kafkawatcher.Models.Topic;

public class ClusterViewModel {

    private String collectorDelimiter = "\", \"";
    private String collectorPrefix = "\"";


    public int getTotalProducers(List<Topic> topics) {
        int count = 0;
        for (Topic topic : topics) {
            for (Partition partition : topic.getPartitions()) {
                count += partition.getProducers().size();
            }
        }
        return count;
    }

    public int getTotalBootstrapServers(Kafka kafka){
        return kafka.getBootstrapServers().size();
    }

    public int getTotalPartitions(List<Topic> topics) {
        int count = 0;
        for (Topic topic : topics) {
            count += topic.getPartitions().size();
        }
        return count;
    }

    public Long getTotalLag(List<ConsumerGroup> consumerGroups)
    {
        return consumerGroups.stream().filter(c-> Objects.nonNull(c.getPartitionsOffsets()))
                .mapToLong(c -> c.getPartitionsOffsets().stream().filter(p -> Objects.nonNull(p))
                    .mapToLong(p -> p.getTotalLag()).sum()).sum();
    }
}
