package com.kafka.watcher.kafkawatcher.Interfaces;

import com.kafka.watcher.kafkawatcher.Models.BootstrapServerConfig;
import com.kafka.watcher.kafkawatcher.Models.BootstrapServers;
import com.kafka.watcher.kafkawatcher.Models.Cluster;

public interface ServerInterface {
    public BootstrapServers getServerByName(Cluster cluster, String serverName) throws Exception;
    public BootstrapServerConfig getBootstrapServerConfig(Cluster cluster, BootstrapServers server,String configName) throws Exception;
}
