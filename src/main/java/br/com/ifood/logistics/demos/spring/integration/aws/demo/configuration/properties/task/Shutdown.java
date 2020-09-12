package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;

public class Shutdown {

    /**
     * Whether the executor should wait for scheduled tasks to complete on shutdown.
     */
    private boolean awaitTermination = true;

    /**
     * Maximum time the executor should wait for remaining tasks to complete.
     */
    @DurationMin(seconds = 0)
    @DurationMax(hours = 2)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration awaitTerminationPeriod = Duration.ofSeconds(60);

    public boolean isAwaitTermination() {
        return this.awaitTermination;
    }

    public void setAwaitTermination(final boolean awaitTermination) {
        this.awaitTermination = awaitTermination;
    }

    public Duration getAwaitTerminationPeriod() {
        return this.awaitTerminationPeriod;
    }

    public void setAwaitTerminationPeriod(final Duration awaitTerminationPeriod) {
        this.awaitTerminationPeriod = awaitTerminationPeriod;
    }

}