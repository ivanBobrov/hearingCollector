package org.home.hearing.collector;

import org.home.hearing.collector.excel.ExcelGenerator;
import org.home.hearing.collector.provider.HearingProvider;
import org.home.hearing.collector.provider.HearingProviderFactory;
import org.home.hearing.collector.source.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final int SHUTDOWN_TIMEOUT = 5;

    public static void main(String[] args) throws InterruptedException {
        log.info("Collector application started");
        Configuration configuration = Configuration.load();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Hearing> hearings = loadHearings(configuration, executor);
        executor.shutdown();

        if (!hearings.isEmpty()) {
            createExcelFile(hearings, configuration);
        } else {
            log.warn("No hearings have been loaded. Won't create file");
        }

        if (!executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
            log.error("Cannot gracefully finish executor in {} seconds", SHUTDOWN_TIMEOUT);
        }
    }

    private static List<Hearing> loadHearings(Configuration configuration, Executor executor) {
        HearingProviderFactory hearingProviderFactory = new HearingProviderFactory();
        HearingProvider hearingProvider = hearingProviderFactory.providerDaysAhead(configuration.getSourceUrls(),
                                                                                   configuration.getLoadDays(),
                                                                                   executor,
                                                                                   configuration.getLoadTimeout());

        return hearingProvider.getHearings();
    }

    private static void createExcelFile(List<Hearing> hearings, Configuration configuration) {
        ExcelGenerator excelGenerator = new ExcelGenerator();
        excelGenerator.generateExcelForHearings(hearings, Paths.get(configuration.getOutputFilename()));
    }

}
