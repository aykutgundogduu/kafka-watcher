Java args;

--spring.config.location=file:D:\Projects\KafkaWatcher\kafka-watcher\target\application.yml
{
    server:
    port: 8080
    kafkawatcher:
        clusters:
            -   name: "cluster-1"
                kafka:
                    name: "Cluster 1 - Kafka"
                    bootstrapServers: 
                        -   ipAddress: localhost
                            port: 9092
                            dns: "cluster1.kafka-dns.local"  
                        -   ipAddress: localhost
                            port: 9093
                            dns: "cluster2.kafka-dns.local"  
                        -   ipAddress: localhost
                            port: 9094
                            dns: "cluster3.kafka-dns.local"    
            -   name: "cluster-2"
                kafka:
                    name: "Cluster 2 - Kafka"
                    bootstrapServers: 
                        -   ipAddress: localhost
                            port: 8092
                            dns: "cluster1.kafka-dns.local"  
                        -   ipAddress: localhost
                            port: 8093
                            dns: "cluster2.kafka-dns.local"  
                        -   ipAddress: localhost
                            port: 8094
                            dns: "cluster3.kafka-dns.local" 
}