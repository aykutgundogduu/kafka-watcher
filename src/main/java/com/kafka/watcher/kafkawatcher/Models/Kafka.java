package com.kafka.watcher.kafkawatcher.Models;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class Kafka {
    
    public String name;
    @NestedConfigurationProperty
    public List<BootstrapServers> bootstrapServers;
    public List<BootstrapServers> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<BootstrapServers> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BootstrapServers> getAvailableNodes()
    {
        if(this.getBootstrapServers() == null) return Collections.<BootstrapServers>emptyList();

        return this.getBootstrapServers().stream().filter(f -> f.isConnected()).toList();
    }
    public List<BootstrapServers> getUnavailableNodes()
    {
        if(this.getBootstrapServers() == null) return Collections.<BootstrapServers>emptyList();

        return this.getBootstrapServers().stream().filter(f -> !f.isConnected()).toList();
    }

    
}
