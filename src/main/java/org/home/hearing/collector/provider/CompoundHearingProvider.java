package org.home.hearing.collector.provider;

import org.home.hearing.collector.Hearing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CompoundHearingProvider implements HearingProvider {
    private static final Logger log = LoggerFactory.getLogger(CompoundHearingProvider.class);
    private static final long DEFAULT_TIMEOUT_MILLIS = 5000;

    private final List<HearingProvider> providers;
    private final Executor executor;
    private final long loadTimeout;

    public CompoundHearingProvider(List<HearingProvider> providers, Executor executor, long loadTimeout) {
        this.providers = providers;
        this.executor = executor;
        this.loadTimeout = loadTimeout;
    }

    public CompoundHearingProvider(List<HearingProvider> providers, Executor executor) {
        this(providers, executor, DEFAULT_TIMEOUT_MILLIS);
    }

    @Override
    public List<Hearing> getHearings() {
        CompletionService<List<Hearing>> completionService = new ExecutorCompletionService<>(executor);

        log.debug("Submitting tasks");
        List<Future<List<Hearing>>> futures = new ArrayList<>();
        for (HearingProvider provider : providers) {
            futures.add(completionService.submit(provider::getHearings));
        }

        List<Hearing> result = new ArrayList<>();
        try {
            for (int i = 0; i < providers.size(); i++) {
                Future<List<Hearing>> completedTask = completionService.poll(loadTimeout, TimeUnit.MILLISECONDS);
                if (completedTask == null) {
                    log.warn("Provider await timeout. Cancelling tasks");
                    break;
                }

                try {
                    List<Hearing> singleTaskResult = completedTask.get();
                    log.debug("Got {} hearings", singleTaskResult.size());
                    result.addAll(singleTaskResult);
                } catch (ExecutionException exception) {
                    Throwable t = exception.getCause();
                    log.error("Hearing provider task finished with exception: {}", t);
                }
            }
        } catch (InterruptedException exception) {
            log.debug("Main thread has been interrupted. Restoring status");
            Thread.currentThread().interrupt();
        } finally {
            futures.forEach(f -> f.cancel(true));
        }

        return result;
    }
}
