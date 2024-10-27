package com.example.demo.config;

import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {

  @Value("${spring.pulsar.client.service.url}")
  private String pulsarUrl;

  @Value("${spring.pulsar.client.authentication.param.token}")
  private String token;

  @Value("${pulsar.producer.topic-base}")
  private String topicBase;

  @Bean
  public PulsarClient pulsarClient() throws PulsarClientException {
    return PulsarClient.builder()
        .serviceUrl(pulsarUrl)
        .proxyServiceUrl(pulsarUrl, ProxyProtocol.SNI)
        .authentication(AuthenticationFactory.token(token))
        .build();
  }

  @Bean
  DeadLetterPolicy deadLetterPolicy() {
    return DeadLetterPolicy.builder().maxRedeliverCount(5).build();
  }

  @Bean
  public String topicBase() {
    return topicBase;
  }
}
