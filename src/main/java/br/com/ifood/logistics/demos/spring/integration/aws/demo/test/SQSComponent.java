package br.com.ifood.logistics.demos.spring.integration.aws.demo.test;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public class SQSComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSComponent.class);

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "inboundQueueChannel")
    public void handleInboundQueueChannel(final Message<String> message) {
        LOGGER.info("inbound -> {}", message);

        final MessageHeaders headers = message.getHeaders();
        headers.put(MessageHeaders.CONTENT_TYPE, "throwExceptionHere");
    }

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "inboundQueueErrorChannel")
    public void handleInboundQueueErrorChannel(final ErrorMessage message) {
        final Message<?> originalMessage = message.getOriginalMessage();
        final String errorMessage = Optional.ofNullable(originalMessage)
                                            .map(om -> String.format(
                                                    "Consume of Message [ %s ] resulted in errors:",
                                                    om.getPayload()))
                                            .orElse("Unhandled error:");
        LOGGER.error(errorMessage, message.getPayload());
    }

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "genericQueueChannel")
    public void handleGenericQueueChannel(final Message<String> message) {

        LOGGER.info("generic-> {}", message);

        final MessageHeaders headers = message.getHeaders();
        headers.put(MessageHeaders.CONTENT_TYPE, "throwExceptionHere");

    }

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "genericQueueDLQChannel")
    public void handleGenericQueueDLQChannel(final Message<String> message) {
        LOGGER.info("genericDLQ -> {}", message);
    }

}
