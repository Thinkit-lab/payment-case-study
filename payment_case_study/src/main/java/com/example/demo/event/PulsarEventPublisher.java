package com.example.demo.event;

import com.example.demo.event.dto.BaseEvent;
import com.example.demo.exceptions.EventException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.demo.constants.ResponseStatus.EVENT_PUBLISH_ERROR;

@Component
@Slf4j
@RequiredArgsConstructor
public class PulsarEventPublisher {

  private final PulsarClient pulsarClient;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Map<String, Producer<byte[]>> producers = new ConcurrentHashMap<>();

  public void publishEvent(String eventName, Object event) {
    try {
      BaseEvent<Object> baseEvent = new BaseEvent<>();
      baseEvent.setSource("ms-customer");
      baseEvent.setData(event);

      Producer<byte[]> producer = producers.computeIfAbsent(eventName, this::createProducer);

      byte[] bytes = mapper.writeValueAsBytes(baseEvent);
      MessageId messageIdCompletableFuture = producer.send(bytes);
      log.info(
          "Published event to topic {} with message id {}", eventName, messageIdCompletableFuture);

      log.info("Event published ::: eventName -> {}, eventData -> {}", eventName, event);
    } catch (Exception ex) {
      log.error("Error publishing event ::: eventName -> {}, event -> {}", eventName, event, ex);
      throw new EventException(EVENT_PUBLISH_ERROR.getCode(), EVENT_PUBLISH_ERROR.getStatus());
    }
  }

  private Producer<byte[]> createProducer(String topic) {
    try {
      return pulsarClient.newProducer().topic(topic).create();
    } catch (PulsarClientException ex) {
      log.error("Failed to create Pulsar producer for topic: {}", topic, ex);
      throw new EventException(EVENT_PUBLISH_ERROR.getCode(), String.format("%s: %s", "Error creating Pulsar producer for topic", topic));
    }
  }

  @PreDestroy
  public void closeProducers() {
    producers
        .values()
        .forEach(
            producer -> {
              try {
                producer.close();
              } catch (PulsarClientException ex) {
                log.error("Failed to close Pulsar producer", ex);
              }
            });
  }
}
