package com.kafka.watcher.kafkawatcher.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteRecordsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DeletedRecords;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicCollection;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import com.kafka.watcher.kafkawatcher.Interfaces.TopicInterface;
import com.kafka.watcher.kafkawatcher.Models.Cluster;
import com.kafka.watcher.kafkawatcher.Models.MethodResult;
import com.kafka.watcher.kafkawatcher.Models.Partition;
import com.kafka.watcher.kafkawatcher.Models.Records;
import com.kafka.watcher.kafkawatcher.Models.Topic;
import com.kafka.watcher.kafkawatcher.RequestModels.FetchMessageModel;
import com.kafka.watcher.kafkawatcher.Utils.KafkaFutureTaskUtil;

@Service
public class TopicService implements TopicInterface {

    private final int MAX_POLL_INTERVAL_MS_CONFIG = 15000;
    private final int MESSAGE_FECTH_TIMEOUT = 1000;
    private final String MESSAGE_FETCH_GROUP_ID = "kafkawatcher13";
    private final KafkaFutureTaskUtil kafkaFutureTaskUtil;
    private final ClusterService clusterService;

    public TopicService(ClusterService clusterService, KafkaFutureTaskUtil kafkaFutureTaskUtil) {
        this.kafkaFutureTaskUtil = kafkaFutureTaskUtil;
        this.clusterService = clusterService;
    }

    @Override
    public boolean addNewTopic(Cluster clusterName, String topicName, int partition, int replicationFactor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addNewTopic'");
    }

    @Override
    public MethodResult addNewTopic(String clusterName, String topicName, int partition, int replicationFactor) {
        MethodResult taskResult = new MethodResult(false, null);
        try {
            Cluster cluster = this.clusterService.getClusterByName(clusterName);
            NewTopic topic = new NewTopic(topicName, partition, Short.valueOf(Integer.toString(replicationFactor)));
            KafkaFuture<Void> result = cluster.getAdminClient().createTopics(Collections.singleton(topic)).all()
                    .whenComplete((t, ex) -> {
                        System.out.println(ex);
                    });
            taskResult = this.kafkaFutureTaskUtil.waitTask(result);
            System.out.println(taskResult);
            if (taskResult.getResult()) {
                return taskResult;
            }
        } catch (Exception e) {
            taskResult.setMessage(e.toString());
        }
        return taskResult;
    }

    @Override
    public boolean isTopicExists(String clusterName, String topicName) {
        try {
            List<TopicListing> topics;
            topics = this.clusterService.getClusterByName(clusterName).getAdminClient().listTopics().listings().get()
                    .stream().filter(t -> t.name().equals(topicName)).toList();
            if (topics.size() > 0)
                return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public MethodResult deleteTopic(String clusterName, String topicName) {
        String message = "";
        Boolean result = false;
        try {

            Cluster cluster = this.clusterService.getClusterByName(clusterName);
            TopicCollection tCollection = TopicCollection.ofTopicNames(Collections.singleton(topicName));
            DeleteTopicsResult deleteResult = cluster.getAdminClient().deleteTopics(tCollection);
            Map<String, KafkaFuture<Void>> results = deleteResult.topicNameValues();
            for (Entry<String, KafkaFuture<Void>> entry : results.entrySet()) {
                try {
                    entry.getValue().get(30, TimeUnit.MINUTES);
                    result = true;
                    message = "Success";
                } catch (Exception ex) {
                    message = ex.getMessage();
                    result = false;
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            message = e.getMessage();
        }

        return new MethodResult(result, message);

    }

    @Override
    public MethodResult truncateMessages(String clusterName, String topicName) {
        Boolean result = false;
        String message = "";
        try {
            Cluster cluster = this.clusterService.getClusterByName(clusterName);
            List<Topic> topics = cluster.getTopics().stream().filter(f -> f.getTopicName().equals(topicName)).toList();
            Map<TopicPartition, RecordsToDelete> recordsToDeleteMap = new HashMap<TopicPartition, RecordsToDelete>();
            if (topics.size() > 1 || topics.size() == 0) {
                throw new Exception(String.format("We found same topic name in this cluster[%s]", clusterName));
            }

            for (Partition partition : topics.get(0).getPartitions()) {
                TopicPartition tp = new TopicPartition(topicName, partition.getPartitionId());
                RecordsToDelete rd = RecordsToDelete.beforeOffset(
                        partition.getPartitionsOffset() != null ? partition.getPartitionsOffset().getCommittedOffset()
                                : 0);
                recordsToDeleteMap.put(tp, rd);
            }
            if (recordsToDeleteMap.size() == 0) {
                throw new Exception("We could not handle this request while creating remove template.");
            }

            DeleteRecordsResult deleteResult = cluster.getAdminClient().deleteRecords(recordsToDeleteMap);
            Map<TopicPartition, KafkaFuture<DeletedRecords>> deletedRecordsFuture = deleteResult.lowWatermarks();

            // Wait for the deletion to complete and get the result
            Collection<KafkaFuture<DeletedRecords>> deletedRecords = deletedRecordsFuture.values();

            // Print out the low watermarks after deletion
            for (KafkaFuture<DeletedRecords> entry : deletedRecords) {
                System.out.println("Deleted up to offset: " + entry.get().lowWatermark());
            }
            result = true;
            message = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            message = e.toString();
        }

        return new MethodResult(result, message);
    }

    @Override
    public MethodResult fetchMessage(FetchMessageModel model) throws Exception {

        MethodResult result = new MethodResult();
        if (!model.isModelValid()) {
            result.setResult(false);
            result.setMessage("This requested model is not valid.");
        }

        Cluster cluster = this.clusterService.getClusterByName(model.getClusterName());
        if (cluster == null) {
            result.setResult(false);
            result.setMessage("The requested cluster could not founded.");
        }

        Map<String, Object> config = this.clusterService.getClusterConfigs(cluster);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, MESSAGE_FETCH_GROUP_ID);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 0);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS_CONFIG);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(config);

        List<TopicPartition> partitions = consumer.partitionsFor(model.getTopicName()).stream()
                .map(info -> new TopicPartition(info.topic(), info.partition()))
                .collect(Collectors.toList());

        consumer.assign(partitions);
        consumer.seekToBeginning(partitions);

        List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(MESSAGE_FECTH_TIMEOUT));
            if (records.isEmpty())
                break; // break if there is no more data
            for (ConsumerRecord<String, String> record : records) {
                allRecords.add(record);
            }
        }

        if (model.getEndOffset() > 0 || model.getStartOffset() > 0
                || (!model.getSearchString().isEmpty() && !model.getSearchString().isBlank())) {
            // Run filter method
            allRecords.forEach(f -> {
                if (model.getEndOffset() > 0 && model.getEndOffset() < f.offset())
                    return;
                if (model.getStartOffset() > 0 && model.getStartOffset() > f.offset())
                    return;
                if (model.getSearchString().length() > 0) {
                    if (f.key() == null && f.value() == null) {
                        // There is two type is null. whatever we have to return. Contains cannot equals
                        // true
                        return;
                    }
                    if (f.value() == null || !f.value().contains(model.getSearchString()) && (f.key() == null || !f.key().contains(model.getSearchString()))) {
                        return;
                    }
                }
                model.appendMessage(new Records(f.leaderEpoch().isEmpty() ? 0 : f.leaderEpoch().get(), f.offset(),
                        f.partition(), f.topic(), f.value()));
            });
        }

        consumer.commitSync();

        if (model.getCalculatedStartIndex() >= model.getMessages().size()) {
            // Size is equals to startIndex, returns no data. Go to return
            model.clearMessages();
        } else if (model.getCalculatedEndIndex() >= model.getMessages().size()) {
            // Handle out of index errors.
            model.setMessages(
                    model.getMessages().subList(model.getCalculatedStartIndex(), model.getMessages().size() - 1));
        } else {
            model.setMessages(
                    model.getMessages().subList(model.getCalculatedStartIndex(), model.getCalculatedEndIndex()));
        }

        return result;

    }

}
