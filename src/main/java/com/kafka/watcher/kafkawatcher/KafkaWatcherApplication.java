package com.kafka.watcher.kafkawatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaWatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaWatcherApplication.class, args);
	}

}
