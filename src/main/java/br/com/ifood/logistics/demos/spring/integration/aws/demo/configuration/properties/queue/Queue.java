package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.queue;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.util.Objects;

public class Queue {

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
     * Configure the max number of times a message can be reprocessed before sent to the {@link Queue#deadLetterQueue}
     */
    @Max(10)
    @NotNull
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