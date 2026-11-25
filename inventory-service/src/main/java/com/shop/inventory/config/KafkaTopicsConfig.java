package com.shop.inventory.config;

import com.shop.common.event.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean NewTopic orderCreatedTopic() { return TopicBuilder.name(Topics.ORDER_CREATED).partitions(3).replicas(1).build(); }
    @Bean NewTopic inventoryReservedTopic() { return TopicBuilder.name(Topics.INVENTORY_RESERVED).partitions(3).replicas(1).build(); }
    @Bean NewTopic inventoryRejectedTopic() { return TopicBuilder.name(Topics.INVENTORY_REJECTED).partitions(3).replicas(1).build(); }
}
