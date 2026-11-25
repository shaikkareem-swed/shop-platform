package com.shop.inventory.messaging;

import com.shop.common.util.Json;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private final KafkaTemplate<String, String> kafka;

    public EventPublisher(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    public void send(String topic, String key, Object event) {
        kafka.send(topic, key, Json.write(event));
    }
}
