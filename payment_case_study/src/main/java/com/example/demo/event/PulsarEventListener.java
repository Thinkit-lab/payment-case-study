package com.example.demo.event;

import com.example.demo.payload.request.CreateAccountRequest;
import com.example.demo.utils.HelperClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.listener.AckMode;
import org.springframework.pulsar.listener.Acknowledgement;
import org.springframework.stereotype.Service;

import static com.example.demo.event.EventNames.CREATE_ACCOUNT_EVENT_NAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class PulsarEventListener {
  private final HelperClass helperFactory;

  @PulsarListener(
      topics = "#{@topicBase}" + CREATE_ACCOUNT_EVENT_NAME,
      deadLetterPolicy = "deadLetterPolicy",
      subscriptionType = SubscriptionType.Shared,
      ackMode = AckMode.MANUAL)
  public void handleCreateAccountEvent(byte[] bytes, Acknowledgement acknowledgement) {
    try {
      CreateAccountRequest eventRequest =
          helperFactory.fromByteToEvent(bytes, CreateAccountRequest.class).getData();
      log.info("Event request -> {}", eventRequest);
      processCreateAccountEvent(eventRequest, acknowledgement);
      acknowledgement.acknowledge();
      log.info(
          "Account created ::: user_id -> {}", eventRequest.getUserId());
    } catch (Exception ex) {
      log.error("Failed to handle event", ex);
      acknowledgement.nack();
    }
  }

  private void processCreateAccountEvent(CreateAccountRequest eventRequest, Acknowledgement acknowledgement) {
    // Todo: Validate the request including the bvn and nin then process the account creation
  }
}
