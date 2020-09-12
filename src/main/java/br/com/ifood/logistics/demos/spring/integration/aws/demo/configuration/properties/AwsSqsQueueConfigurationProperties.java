package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.queue.Queue;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task.TaskExecutor;

@Validated
@ConfigurationProperties("integration.aws.sqs.configuration")
public class AwsSqsQueueConfigurationProperties {

    @Valid
    private Map<String, Consumer> consumers = Map.of();

    public void setConsumers(final Map<String, Consumer> consumers) {
        this.consumers = Optional.ofNullable(consumers)
                                 .orElseGet(Map::of);

        this.consumers.forEach((beanName, consumer) -> consumer.setBeanNamePrefix(beanName));
    }

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

    @Validated
    public static class Consumer {

        /**
         * <p>Serves as an reference for all the named beans dependencies.</p>
         * <p>Its default value its the key referencing this {@link Consumer} in the {@link
         * AwsSqsQueueConfigurationProperties#consumers} map</p>
         */
        private String beanNamePrefix;

        /**
         * <p>The channel in which the messages of this consumer will be redirected to.</p>
         * <p>If empty, the channel name will be "{@link Consumer#beanNamePrefix} + Channel"</p>
         */
        private String channelName;

        /**
         * <p>The channel in which the messages that, for some reason, failed to be successfully processed will be
         * redirected to.</p>
         * <p>If empty, no channel will be created.</p>
         */
        private String errorChannelName;

        @Valid
        @NotEmpty
        private Set<Queue> queues;

        @NonNull
        private SqsMessageDeletionPolicy deletionPolicy = SqsMessageDeletionPolicy.NO_REDRIVE;

        @Valid
        private TaskExecutor executor = new TaskExecutor();

        /**
         * Configure the maximum number of messages that should be retrieved during one poll to the Amazon SQS system.
         * This number must be a positive, non-zero number that has a maximum number of 10. Values higher then 10 are
         * currently not supported by the queueing system.
         */
        @Min(1)
        @Max(10)
        private Integer maxNumberOfMessages = 10;


        /**
         * Configures the duration (in seconds) that the received messages are hidden from subsequent poll requests
         * after being retrieved from the system.
         */
        @DurationMin(seconds = 0)
        @DurationMax(hours = 12)
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration visibilityTimeout = Duration.ofSeconds(30);

        /**
         * Configures the wait timeout that the poll request will wait for new message to arrive if the are currently no
         * messages on the queue. Higher values will reduce poll request to the system significantly. The value should
         * be in seconds and between 1 and 20. For more information read the <a href=
         * "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html">documentation</a>.
         */
        @DurationMin(seconds = 0)
        @DurationMax(seconds = 20)
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration waitTimeout = Duration.ofSeconds(20);

        /**
         * Configures if this container should be automatically started. The default value is true.
         */
        private Boolean autoStartup = Boolean.TRUE;

        public String getBeanNamePrefix() {
            return beanNamePrefix;
        }

        public void setBeanNamePrefix(final String beanNamePrefix) {
            this.beanNamePrefix = Objects.requireNonNull(beanNamePrefix);
            this.executor.setBeanName(beanNamePrefix);
            this.setChannelName(this.beanNamePrefix + "Channel");
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(final String channelName) {
            this.channelName = channelName;
        }

        public String getErrorChannelName() {
            return errorChannelName;
        }

        public void setErrorChannelName(final String errorChannelName) {
            this.errorChannelName = errorChannelName;
        }

        public String getChannelAdapterName() {
            return this.beanNamePrefix + "ChannelAdapter";
        }

        public List<Queue> getQueues() {
            return new ArrayList<>(queues);
        }

        public void setQueues(final List<Queue> queues) {
            this.queues = new HashSet<>(queues);
        }

        public SqsMessageDeletionPolicy getDeletionPolicy() {
            return deletionPolicy;
        }

        public void setDeletionPolicy(final SqsMessageDeletionPolicy deletionPolicy) {
            this.deletionPolicy = deletionPolicy;
        }

        public TaskExecutor getExecutor() {
            return executor;
        }

        public void setExecutor(final TaskExecutor executor) {
            this.executor = executor;
        }

        public Integer getMaxNumberOfMessages() {
            return maxNumberOfMessages;
        }

        public void setMaxNumberOfMessages(final Integer maxNumberOfMessages) {
            this.maxNumberOfMessages = maxNumberOfMessages;
        }

        public Duration getVisibilityTimeout() {
            return visibilityTimeout;
        }

        public void setVisibilityTimeout(final Duration visibilityTimeout) {
            this.visibilityTimeout = visibilityTimeout;
        }

        public Duration getWaitTimeout() {
            return waitTimeout;
        }

        public void setWaitTimeout(final Duration waitTimeout) {
            this.waitTimeout = waitTimeout;
        }

        public Boolean getAutoStartup() {
            return autoStartup;
        }

        public void setAutoStartup(final Boolean autoStartup) {
            this.autoStartup = autoStartup;
        }
    }
}
