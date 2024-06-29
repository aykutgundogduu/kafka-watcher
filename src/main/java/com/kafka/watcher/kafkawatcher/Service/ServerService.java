package com.kafka.watcher.kafkawatcher.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kafka.watcher.kafkawatcher.Exceptions.UniqueValueException;
import com.kafka.watcher.kafkawatcher.Interfaces.ServerInterface;
import com.kafka.watcher.kafkawatcher.Models.BootstrapServerConfig;
import com.kafka.watcher.kafkawatcher.Models.BootstrapServers;
import com.kafka.watcher.kafkawatcher.Models.Cluster;

@Service
public class ServerService implements ServerInterface {

    @Override
    public BootstrapServers getServerByName(Cluster cluster, String serverName) throws UniqueValueException {
        List<BootstrapServers> sList = cluster.getKafka().getBootstrapServers().stream().filter(c -> c.getIpAndPort().equals(serverName)).toList();
        if(sList.size() == 0) throw new UniqueValueException(cluster.getName() + " - " + serverName);
        if(sList.size() > 1) throw new UniqueValueException(cluster.getName() + " - " + serverName);
        return sList.getFirst();
    }

    @Override
    public BootstrapServerConfig getBootstrapServerConfig(Cluster cluster, BootstrapServers server, String configName) throws UniqueValueException {
        List<BootstrapServerConfig> cList = server.getBootstrapServerConfigs().stream().filter(c -> c.getName().equals(configName)).toList();
        if(cList.size() == 0) throw new UniqueValueException(cluster.getName() + " - " + server.getIpAndPort() + " - " + configName);
        if(cList.size() > 1) throw new UniqueValueException(cluster.getName() + " - " + server.getIpAndPort() + " - " + configName);
        return cList.getFirst();
    }
}
