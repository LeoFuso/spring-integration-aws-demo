package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.lang.NonNull;

@ConstructorBinding
@ConfigurationProperties("integration.message.aws.sqs.configuration")
public class SQSQueueConfigurationProperties {

    private final Map<String, SQSMessageProducerBeanConfiguration> messageProducers;

    public SQSQueueConfigurationProperties(final Map<String, SQSMessageProducerBeanConfiguration> messageProducers) {
        this.messageProducers = Optional.ofNullable(messageProducers)
                                        .orElseGet(Map::of);
    }

    public Map<String, SQSMessageProducerBeanConfiguration> getMessageProducersConfigurationMap() {
        return messageProducers;
    }

    private static class SQSMessageProducerBeanConfiguration {

        private final Map<String, String> queueNames;
        private final SqsMessageDeletionPolicy deletionPolicy;
        private final Integer maxNumberOfSameMessageReceive;

        private final Integer maxNumberOfMessages;
        private final Integer maxNumberOfThreads;
        private final Integer visibilityTimeout;
        private final Integer waitTimeout;
        private final Boolean autoStartup;

        public SQSMessageProducerBeanConfiguration(@NotEmpty final Map<String, String> queueNames,
                                                   @DefaultValue("ON_SUCCESS") @NonNull
                                                   final SqsMessageDeletionPolicy deletionPolicy,
                                                   @DefaultValue("5") @Min(1) @Max(10)
                                                   final Integer maxNumberOfSameMessageReceive,
                                                   @DefaultValue("10") @Min(1) @Max(10)
                                                   final Integer maxNumberOfMessages,
                                                   @DefaultValue("10") @Min(1) final Integer maxNumberOfThreads,
                                                   @Positive @Max(43200) final Integer visibilityTimeout,
                                                   @DefaultValue("20") @Min(1) @Max(20) final Integer waitTimeout,
                                                   @DefaultValue("true") final Boolean autoStartup) {
            this.queueNames = Objects.requireNonNull(queueNames);
            this.deletionPolicy = Objects.requireNonNull(deletionPolicy);
            this.maxNumberOfSameMessageReceive = Objects.requireNonNull(maxNumberOfSameMessageReceive);
            this.maxNumberOfMessages = Objects.requireNonNull(maxNumberOfMessages);
            this.maxNumberOfThreads = Objects.requireNonNull(maxNumberOfThreads);
            this.visibilityTimeout = Objects.requireNonNull(visibilityTimeout);
            this.waitTimeout = Objects.requireNonNull(waitTimeout);
            this.autoStartup = Objects.requireNonNull(autoStartup);
        }

        public Map<String, String> getQueueNames() {
            return queueNames;
        }

        public SqsMessageDeletionPolicy getDeletionPolicy() {
            return deletionPolicy;
        }

        public Integer getMaxNumberOfSameMessageReceive() {
            return maxNumberOfSameMessageReceive;
        }

        public Integer getMaxNumberOfMessages() {
            return maxNumberOfMessages;
        }

        public Integer getMaxNumberOfThreads() {
            return maxNumberOfThreads;
        }

        public Integer getVisibilityTimeout() {
            return visibilityTimeout;
        }

        public Integer getWaitTimeout() {
            return waitTimeout;
        }

        public Boolean getAutoStartup() {
            return autoStartup;
        }
    }

}
