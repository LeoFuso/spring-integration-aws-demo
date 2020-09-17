package br.com.ifood.logistics.demos.spring.integration.aws.demo.test;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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
    public void handleInboundQueueErrorChannel(@NonNull final ErrorMessage message) throws Exception {

        final Message<?> originalMessage = message.getOriginalMessage();
        final String errorMessage =
                Optional.ofNullable(originalMessage)
                        .map(Message::getPayload)
                        .map(payload -> String.format("Consume of Message [ %s ] resulted in errors:", payload))
                        .orElse("Unhandled error:");

        final Throwable payload = message.getPayload();
        LOGGER.error(errorMessage, payload);

        /* This exception needs to be rethrown, otherwise if the message has a re drive policy nothing will happen */
        ReflectionUtils.rethrowException(payload);
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
