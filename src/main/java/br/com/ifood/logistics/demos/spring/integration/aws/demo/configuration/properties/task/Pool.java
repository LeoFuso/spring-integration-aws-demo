package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task;

import javax.validation.constraints.Min;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;

public class Pool {

    /**
     * Queue capacity. An unbounded capacity does not increase the pool and therefore ignores the "max-size" property.
     */
    private int queueCapacity = Integer.MAX_VALUE;

    /**
     * Minimum number of threads in the pool.
     */
    @Min(2)
    private int coreSize = 2;

    /**
     * Maximum allowed number of threads. If tasks are filling up the queue, the pool can expand up to that size to
     * accommodate the load. Ignored if the queue is unbounded.
     */
    private int maxSize = Integer.MAX_VALUE;

    /**
     * Whether core threads are allowed to time out. This enables dynamic growing and shrinking of the pool.
     */
    private boolean allowCoreThreadTimeout = true;

    /**
     * Time limit for which threads may remain idle before being terminated.
     */
    @DurationMin(seconds = 0)
    @DurationMax(hours = 2)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAlive = Duration.ofSeconds(60);

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public void setQueueCapacity(final int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getCoreSize() {
        return this.coreSize;
    }

    public void setCoreSize(final int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isAllowCoreThreadTimeout() {
        return this.allowCoreThreadTimeout;
    }

    public void setAllowCoreThreadTimeout(final boolean allowCoreThreadTimeout) {
        this.allowCoreThreadTimeout = allowCoreThreadTimeout;
    }

    public Duration getKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(final Duration keepAlive) {
        this.keepAlive = keepAlive;
    }

}

