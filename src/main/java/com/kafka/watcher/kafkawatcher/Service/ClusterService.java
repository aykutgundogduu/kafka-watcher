package com.kafka.watcher.kafkawatcher.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeLogDirsResult;
import org.apache.kafka.clients.admin.DescribeProducersResult;
import org.apache.kafka.clients.admin.DescribeProducersResult.PartitionProducerState;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.ReplicaInfo;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import com.kafka.watcher.kafkawatcher.Exceptions.ReadOnlyException;
import com.kafka.watcher.kafkawatcher.Exceptions.UniqueValueException;
import com.kafka.watcher.kafkawatcher.Interfaces.ClusterInterface;
import com.kafka.watcher.kafkawatcher.Models.BootstrapServerConfig;
import com.kafka.watcher.kafkawatcher.Models.BootstrapServers;
import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.ConsumerGroup;
import com.kafka.watcher.kafkawatcher.Models.Kafka;
import com.kafka.watcher.kafkawatcher.Models.Kafkawatcher;
import com.kafka.watcher.kafkawatcher.Models.Partition;
import com.kafka.watcher.kafkawatcher.Models.PartitionsOffset;
import com.kafka.watcher.kafkawatcher.Models.Producer;
import com.kafka.watcher.kafkawatcher.Models.Topic;

import groovyjarjarantlr4.v4.parse.GrammarTreeVisitor.astOperand_return;

@Service
public class ClusterService implements ClusterInterface {
    public static final int RETRY_TIMEOUT = 1;
    public static final long RETRY_INTERVAL = 1;
    private final List<Cluster> clusters;
    private final Kafkawatcher kafkaWatcher;
    private final ServerService serverService;
    //Service is initialized when application is running.
    //So we can connect to every single kafka cluster defined in application.yml
    public ClusterService(Kafkawatcher KafkaWatcher,ServerService serverService) throws Exception{
        this.kafkaWatcher = KafkaWatcher;
        this.clusters = this.kafkaWatcher.getClusters();
        this.serverService = serverService;



        if(this.clusters.size()== 0){
            throw new Exception("Cluster size is 0, please check application.yml");
        }

        for(Cluster cluster : this.clusters) {
            try{
                boolean connectionResult = ConnectToCluster(cluster);
                if(!connectionResult){
                    //TODO Retry count will defined in application.yml and we will try again to reach that retry-count.
                    cluster.setClusterStatusAvailable(false);
                }
                else {
                    System.out.println("Successfully connected to " + cluster.getName());
                    System.out.println("Data collector is started");
                    cluster.setClusterStatusAvailable(true);
                    this.RefreshCluster(cluster);
                    System.out.println("Data collector is done");
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private boolean ConnectToCluster(Cluster cluster) throws Exception
    {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,cluster.getKafka().getBootstrapServers().get(0).getIpAndPort());
        cluster.setAdminClient(AdminClient.create(configs));
        return this.checkClusterStatus(cluster, RETRY_TIMEOUT, TimeUnit.SECONDS);
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public Kafka getKafka(Cluster cluster) throws Exception {
        if(this.clusters.indexOf(cluster) == -1) {
            throw new Exception("Cluster is not found in cluster definitions");
        }
        return this.clusters.get(this.clusters.indexOf(cluster)).getKafka();
    }

    @Override
    public Cluster getDiskUsage(Cluster cluster) {
        AdminClient adminClient = cluster.getAdminClient();
        for (BootstrapServers server : cluster.getKafka().getAvailableNodes()) {

            DescribeLogDirsResult describeLogDirsResult = adminClient.describeLogDirs(Collections.singleton(server.getNodeId()));
            try {
                Collection<Map<String, LogDirDescription>> logDesc = describeLogDirsResult.allDescriptions().get().values();
                if(logDesc.isEmpty()) 
                {
                    return cluster;
                }
                LogDirDescription node = logDesc.stream().findFirst().get().values().stream().findFirst().get();
                if(node.error() != null) server.setErrorString(node.error().getMessage());

                DataSize dataSizeTotalByte = DataSize.ofBytes(node.totalBytes().orElse(0));
                DataSize dataSizeUsableByte = DataSize.ofBytes(node.usableBytes().orElse(0));
                
                server.setTotalBtyes(dataSizeTotalByte.toKilobytes());
                server.setUsableBytes(dataSizeUsableByte.toKilobytes());
                server.setTotalGB(dataSizeTotalByte.toGigabytes());
                server.setUsableGB(dataSizeUsableByte.toGigabytes());
                server.setLogDir(logDesc.stream().toList().get(0).keySet().toString());
                
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        return cluster;
    }

    @Override
    public Cluster getMetrics(Cluster cluster) {
        Map<MetricName, ? extends Metric> metrics = cluster.getAdminClient().metrics();
        List<com.kafka.watcher.kafkawatcher.Models.Metric>clusterMetrics = metrics.values().stream().map(m -> new com.kafka.watcher.kafkawatcher.Models.Metric(m.metricName().name(),m.metricValue())).toList();
        cluster.setClusterMetrics(clusterMetrics);
        return cluster;
    }
  
    @Override
    public Cluster getProducers(Cluster cluster) {
        for (Topic topic : cluster.getTopics()) {
            for (Partition partition : topic.getPartitions()) {

                DescribeProducersResult producerResult = cluster.getAdminClient().describeProducers(Collections.singleton(new TopicPartition(topic.getTopicName(),partition.getPartitionId())));

                KafkaFuture<Map<TopicPartition, PartitionProducerState>> allProducers = producerResult.all();
                
                try {
                    Map<TopicPartition, PartitionProducerState> procuders = allProducers.get();
                    List<PartitionProducerState> producerValues = procuders.values().stream().toList();
                    for (PartitionProducerState procs : producerValues) {
                        List<Producer> pList = procs.activeProducers().stream().map(z-> new Producer(z.producerId(),z.producerEpoch() , z.currentTransactionStartOffset().orElse(0), z.lastSequence())).toList();
                        partition.setProducers(pList);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }
        return cluster;
    }

    @Override
    public Cluster getPartitions(Cluster cluster) {
        for (Topic topic : cluster.getTopics()) {


            DescribeTopicsResult describeTopicsResult = cluster.getAdminClient().describeTopics(Collections.singletonList(topic.getTopicName()));
            Map<String, KafkaFuture<TopicDescription>> values = describeTopicsResult.topicNameValues();
            KafkaFuture<TopicDescription> topicDescription = values.get(topic.getTopicName());
            
            try {
                List<TopicPartitionInfo> partitions = topicDescription.get().partitions().stream().toList();
                List<Partition> parsedPartitions = partitions.stream()
                .map(t -> new Partition(t.partition(), t.replicas(),t.isr(),t.leader())).toList();

                //Get replicas 
                for(TopicPartitionInfo partitionInfo : partitions)
                {
                    Collection<Integer> replicaNodes = partitionInfo.replicas().stream().map(p -> p.id()).toList();
                    Map<Integer, Map<String, LogDirDescription>> logDirsResult = cluster.getAdminClient().describeLogDirs(replicaNodes).allDescriptions().get();
                    for (Map<String, LogDirDescription> logDirs : logDirsResult.values()) {
                        for (LogDirDescription logDir : logDirs.values()) {

                            List<Entry<TopicPartition, ReplicaInfo>> infos = logDir.replicaInfos().entrySet().stream().filter(f -> f.getKey().partition() == partitionInfo.partition())
                            .filter(f-> f.getKey().topic().equals(topic.getTopicName())).toList();
                            
                            for (Entry<TopicPartition, ReplicaInfo> info : infos) {
                                parsedPartitions.stream().filter(f -> f.getPartitionId() == info.getKey().partition())
                                .forEach(e -> e.addReplicas(info));
                            }

                        }   
                    }

                }

                //Check total lag//
                //SetPartitionOffsets
                for (Partition partition : parsedPartitions) {
                    //For latest offsets
                    Map<TopicPartition, OffsetSpec> topicLatestsOffsets = new HashMap<>();
                    topicLatestsOffsets.put(new TopicPartition(topic.getTopicName(), partition.getPartitionId()), OffsetSpec.latest());

                    //For earliest offsets
                    Map<TopicPartition, OffsetSpec> topicEarliestOffsets = new HashMap<>();
                    topicEarliestOffsets.put(new TopicPartition(topic.getTopicName(), partition.getPartitionId()), OffsetSpec.earliest());

                    ListOffsetsResult listLatestOffsetsResult = cluster.getAdminClient().listOffsets(topicLatestsOffsets);
                    ListOffsetsResult listEarliestOffsetsResult = cluster.getAdminClient().listOffsets(topicEarliestOffsets);
                    Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsetMap = listLatestOffsetsResult.all().get();
                    Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestOffsetMap = listEarliestOffsetsResult.all().get();

                    List<PartitionsOffset> earliestPartitionOffsets = new ArrayList<>();

                    //Work for get earliest offsets
                    for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> e : earliestOffsetMap.entrySet()) {
                        String topicName = e.getKey().topic();
                        int p =  e.getKey().partition();
                        long earliestOffSet = e.getValue().offset();
                        //First get the earlistes offset for calculate the number of message after delete records.
                        PartitionsOffset offsetModel = new PartitionsOffset(earliestOffSet,Long.valueOf(0), Long.valueOf(0), p, topicName);
                        earliestPartitionOffsets.add(offsetModel);
                    }
                    
                    //Work for get latest offsets and merge with earliests.
                    for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> e : latestOffsetMap.entrySet()) {
                        String topicName = e.getKey().topic();
                        int p =  e.getKey().partition();
                        long committedOffset = e.getValue().offset();
                        //This is not consumer group details so lastoffset does not reachable at this point
                        Optional<PartitionsOffset> earliestOffSet = earliestPartitionOffsets.stream().filter(f -> f.getPartition() == p && f.getTopicName().equals(topic.getTopicName())).findFirst();

                        PartitionsOffset offsetModel = new PartitionsOffset(earliestOffSet.get().getEarliestOffSet(),Long.valueOf(0), committedOffset, p, topicName);
                        
                        partition.setPartitionsOffset(offsetModel);
                    }
                }
                topic.setPartitions(parsedPartitions);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return cluster;
    }

    @Override
    public Cluster getTopics(Cluster cluster) {
        try {
            //ListTopicsResult topics=cluster.getAdminClient().listTopics(new ListTopicsOptions().listInternal(true));
            ListTopicsResult topics=cluster.getAdminClient().listTopics();
            cluster.setTopics(topics.listings().get().stream().map(t-> new Topic(t.name(),t.topicId(),t.isInternal())).toList());
            return cluster;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            cluster.setTopics(null);
        }
        return cluster;
    }

    @Override
    public List<ConsumerGroup> getConsumerGroups(Cluster cluster) {
        List<ConsumerGroup> consumers;
        try {
            consumers = cluster.getAdminClient().listConsumerGroups().all().get().stream().map(c -> new ConsumerGroup(c.groupId(),c.isSimpleConsumerGroup(),c.state())).toList();
            cluster.setConsumerGroups(consumers);
            return consumers;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Cluster getBootstrapServers(Cluster cluster) {
        KafkaFuture<Collection<Node>> servers = cluster.getAdminClient().describeCluster().nodes();

        try {
            for (Node node : servers.get()) {
                
                
                
                //Get server config
                Collection<ConfigResource> resources = new ArrayList<>();
                resources.add(new ConfigResource(Type.BROKER, node.idString()));
                
                Collection<KafkaFuture<Config>> describeConfigs = cluster.getAdminClient().describeConfigs(resources).values().values();

                describeConfigs.forEach(p -> {
                    try {
                        List<BootstrapServerConfig> configs = p.get().entries().stream().map(e -> new BootstrapServerConfig(e.isSensitive(), e.isReadOnly(), e.documentation() , e.value() , e.name())).toList();
                        cluster.getKafka().getBootstrapServers().forEach(server -> server.setNodeId(node.id(), node.idString(), node.port(),configs));
                    } catch (InterruptedException | ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });

            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cluster;
    }

    @Override
    public void RefreshCluster(Cluster cluster) {
        if(cluster.isClusterStatusAvailable())
        {
            this.getBootstrapServers(cluster);
            this.getTopics(cluster);
            this.getMetrics(cluster);
            this.getConsumerGroups(cluster);
            this.getPartitions(cluster);
            this.getProducers(cluster);
            this.getDiskUsage(cluster);
            this.getConsumerGroupsOffset(cluster);
            this.applyPartitionsToNodes(cluster);
        }
    }

    @Override
    public void RefreshClusters() {
        for (Cluster cluster : this.clusters) {
            this.RefreshCluster(cluster);
        }
    }

    @Override
    public Cluster getCluster(String clusterName) {
        for (Cluster cluster : this.clusters) {
            if(cluster.getName().equals(clusterName)) return cluster;
        }
        return null;
    }

    @Override
    public Cluster getConsumerGroupsOffset(Cluster cluster) {
        for (ConsumerGroup consumerGroup : cluster.getConsumerGroups()) {
            Map<TopicPartition, OffsetAndMetadata> offsets;
            try {
                offsets = cluster.getAdminClient().listConsumerGroupOffsets(consumerGroup.getGroupId()).partitionsToOffsetAndMetadata().get();

                
                Map<TopicPartition, OffsetSpec> requestLatestOffsets = new HashMap<>();
                for(TopicPartition tp: offsets.keySet()) {
                    requestLatestOffsets.put(tp, OffsetSpec.latest());
                }

                Map<TopicPartition, OffsetSpec> requestEarLiestOffsets = new HashMap<>();
                for(TopicPartition tp: offsets.keySet()) {
                    requestEarLiestOffsets.put(tp, OffsetSpec.earliest());
                }

                Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earlistOffsets = cluster.getAdminClient().listOffsets(requestEarLiestOffsets).all().get();
                
                Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets = cluster.getAdminClient().listOffsets(requestLatestOffsets).all().get();


                //First get earliest offset to find number of messages count
                //After delete records committedoffsets does not give correct number of messages
                List<PartitionsOffset> earliestPartitionsOffsets = new ArrayList<>();

                for (Map.Entry<TopicPartition, OffsetAndMetadata> e: offsets.entrySet()) {
                    String topic = e.getKey().topic();
                    int partition =  e.getKey().partition();
                    long committedOffset = e.getValue().offset();
                    long earlistOffset = earlistOffsets.get(e.getKey()).offset();
                    //(latestOffset - committedOffset) + " records behind"
                    PartitionsOffset offsetModel = new PartitionsOffset(earlistOffset,Long.valueOf(0), committedOffset, partition, topic);
                    earliestPartitionsOffsets.add(offsetModel);
                }

                List<PartitionsOffset> partitionsOffsets = new ArrayList<>();
                for (Map.Entry<TopicPartition, OffsetAndMetadata> e: offsets.entrySet()) {
                    String topic = e.getKey().topic();
                    int partition =  e.getKey().partition();
                    long committedOffset = e.getValue().offset();
                    long latestOffset = latestOffsets.get(e.getKey()).offset();
                    //(latestOffset - committedOffset) + " records behind"
                    Optional<PartitionsOffset> earlistOffset = earliestPartitionsOffsets.stream().filter(f -> f.getTopicName().equals(topic.toString()) && f.getPartition() == partition).findFirst();
                    PartitionsOffset offsetModel = new PartitionsOffset(earlistOffset.get().getEarliestOffSet(),latestOffset, committedOffset, partition, topic);
                    partitionsOffsets.add(offsetModel);
                    consumerGroup.setPartitionsOffsets(partitionsOffsets);
                }

                

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
        return cluster;
    }

    @Override
    public boolean editServerConfig(String clusterName, String serverName, String configName, String configValue) throws Exception {
        
        Cluster cluster = this.getClusterByName(clusterName);
        BootstrapServers server = this.serverService.getServerByName(cluster, serverName);
        BootstrapServerConfig config = this.serverService.getBootstrapServerConfig(cluster, server, configName);
        if(config.getIsReadOnly()) {
            throw new ReadOnlyException("Config is read-only, We can not edit this value.");
        }
        System.out.println("Now we can edit this config for :" + cluster.getName() + " - " + server.getIpAndPort() + " - " + config.getName());

        return true;
       
    }

    @Override
    public Cluster getClusterByName(String clusterName) throws Exception {
        List<Cluster> cList = this.clusters.stream().filter(c -> c.getName().equals(clusterName)).toList();
        if(cList.size() == 0) throw new UniqueValueException("Cluster is not found in list with name " + clusterName);
        if(cList.size() > 1) throw new UniqueValueException("Multiple cluster is founded in list. Please pass unique name. Name of this cluster :" + clusterName);
        return cList.getFirst();
    }

    @Override
    public Topic getTopic(Cluster cluster,String topicName) {
        Optional<Topic> topic = cluster.getTopics().stream().filter(f -> f.getTopicName().equals(topicName)).findFirst();
        if(topic.isPresent() & !topic.isEmpty())
        {
            return topic.get();
        }
        return null;
    }

    @Override
    public boolean checkClusterStatus(Cluster cluster) {
        try {
            return this.checkClusterStatus(cluster, RETRY_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    @Scheduled(fixedDelay = RETRY_INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void checkClustersStatus() throws Exception {
        for (Cluster cluster : this.clusters) {
            this.checkClusterStatus(cluster);
        }
    }

    @Override
    public boolean checkClusterStatus(Cluster cluster, int unit, TimeUnit unitType) throws Exception {
        DescribeClusterResult clusterResult = cluster.getAdminClient().describeCluster();
        cluster.setLastPoolDate(Instant.now());
        try {
            Collection<Node> nodes = clusterResult.nodes().get(unit,unitType);
            //if there is any node details, cluster is available
            //System.out.println("Cluster is available with nodes - " + nodes);
            //First mark as disconnect every node
            cluster.getKafka().getBootstrapServers().forEach(f -> {
                f.setConnected(false);
            });
            for (Node node : nodes) {
                Optional<BootstrapServers> inClusterNode = cluster.getKafka().getBootstrapServers().stream().filter(f -> f.getNodeId() == node.id()).findFirst();
                if(inClusterNode.isPresent() & !inClusterNode.isEmpty())
                {
                    inClusterNode.get().setConnected(true);
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            cluster.setClusterStatusAvailable(false);
            return false;
        }  
        
        cluster.setClusterStatusAvailable(true);
        return true;
    }

    @Override
    public void applyPartitionsToNodes(Cluster cluster) {

        for (BootstrapServers server : cluster.getKafka().getBootstrapServers()) {
            server.clearIsrPatitions();
            server.clearLeaderPartitions();
            server.clearReplicaPartitions();
            cluster.getTopics().stream().map(m -> m.getPartitions()).forEach(f -> {
                f.forEach(fe -> {

                    if(server.getNodeId() == fe.getLeader().id())
                    {
                        server.addLeaderPartition(fe);
                        
                    }

                    fe.getIsr().forEach(isr -> {
                        if(server.getNodeId() == isr.id())
                        {
                            server.addIsrPartitition(fe);
                        }
                    });

                    fe.getReplicaNodes().forEach(replica -> {
                        if(server.getNodeId() == replica.id())
                        {
                            server.addReplicaPartition(fe);
                        }
                    });
                    
                });
            });
        }
    }
}
