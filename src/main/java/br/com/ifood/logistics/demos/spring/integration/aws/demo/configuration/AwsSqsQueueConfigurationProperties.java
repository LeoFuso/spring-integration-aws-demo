package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

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

@Validated
@ConfigurationProperties("integration.aws.sqs.configuration")
public class AwsSqsQueueConfigurationProperties {

    @Valid
    private Map<String, Consumer> consumers = Map.of();

    public void setConsumers(final Map<String, Consumer> consumers) {
        this.consumers = Optional.ofNullable(consumers)
                                 .orElseGet(Map::of);

        this.consumers
                .forEach((beanName, consumer) -> consumer.setName(beanName));
    }

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

    static class Queue {

        /**
         * Configure the queue name
         */
        @NotEmpty
        private String name;

        /**
         * If present, configure a dead letter queue associated with this {@link Queue}
         */
        private String deadLetterQueue;

        /**
         * Configure the max number of times a message can be reprocessed before sent to the {@link
         * Queue#deadLetterQueue}
         */
        @Max(10)
        @Positive
        private Integer maxNumberOfSameMessageReceive = 5;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getDeadLetterQueue() {
            return deadLetterQueue;
        }

        public void setDeadLetterQueue(final String deadLetterQueue) {
            this.deadLetterQueue = deadLetterQueue;
        }

        public Integer getMaxNumberOfSameMessageReceive() {
            return maxNumberOfSameMessageReceive;
        }

        public void setMaxNumberOfSameMessageReceive(final Integer maxNumberOfSameMessageReceive) {
            this.maxNumberOfSameMessageReceive = maxNumberOfSameMessageReceive;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Queue)) {
                return false;
            }
            final Queue queue = (Queue) o;
            return getName().equals(queue.getName()) &&
                    Objects.equals(getDeadLetterQueue(), queue.getDeadLetterQueue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDeadLetterQueue());
        }
    }


    static class TaskExecutor {

        private String name;

        /**
         * The minimum number of threads to keep alive without timing out.
         */
        @Positive
        private Integer corePoolSize = 2;

        /**
         * The maximum number of threads that can ever be created.
         */
        @Positive
        private Integer maxPoolSize = Integer.MAX_VALUE;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name + "AsyncTaskExecutor";
        }

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(final Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(final Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }


    static class Consumer {

        private String name;

        @Valid
        @NotEmpty
        private Set<Queue> queues;

        @NonNull
        private SqsMessageDeletionPolicy deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS;

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

        private void setName(final String name) {
            this.name = Objects.requireNonNull(name);
            this.executor.setName(name);
        }

        public String getName() {
            return name;
        }

        public String getChannelName() {
            return this.name + "Channel";
        }

        public String getChannelAdapterName() {
            return this.name + "ChannelAdapter";
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
