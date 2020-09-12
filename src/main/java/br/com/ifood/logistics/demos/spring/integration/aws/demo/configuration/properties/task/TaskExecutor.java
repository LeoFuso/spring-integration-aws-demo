package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.UUID;

public class TaskExecutor {

    private static final String DEFAULT_THREAD_NAME_PREFIX = "task-";
    private static final String DEFAULT_BEAN_NAME_PREFIX = "threadPoolTaskExecutor-";

    @Valid
    private final Pool pool = new Pool();

    @Valid
    private final Shutdown shutdown = new Shutdown();

    /**
     * Bean name that's going to be assigned to newly created instances. The default value is
     * <code>'threadPoolTaskExecutor-'</code> followed by a random {@link UUID}
     */
    @NotEmpty
    private String beanName = DEFAULT_BEAN_NAME_PREFIX + UUID.randomUUID() + "-";

    /**
     * Prefix to use for the names of newly created threads. If a {@link TaskExecutor#beanName} is provided, it will be
     * used instead.
     */
    @NotEmpty
    private String threadNamePrefix = DEFAULT_THREAD_NAME_PREFIX;

    public Pool getPool() {
        return this.pool;
    }

    public Shutdown getShutdown() {
        return this.shutdown;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(final String beanName) {
        this.beanName = beanName;
        setThreadNamePrefix(beanName);
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadNamePrefix(final String threadNamePrefix) {
        if (beanName != null) {
            this.threadNamePrefix = DEFAULT_THREAD_NAME_PREFIX + "-" + threadNamePrefix + "-";
        }
    }

}