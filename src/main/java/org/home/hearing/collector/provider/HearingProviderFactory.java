package org.home.hearing.collector.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class HearingProviderFactory {

    public HearingProvider providerForUrl(LocalDate date, String url) {
        return new SiteUrlHearingProvider(url, date);
    }

    public HearingProvider providerForUrl(List<String> urls, LocalDate date, Executor executor) {
        List<HearingProvider> providers = urls.stream()
                                              .map(url -> providerForUrl(date, url))
                                              .collect(Collectors.toList());

        return new CompoundHearingProvider(providers, executor);
    }

    public HearingProvider providerDaysAhead(List<String> urls, int daysAheadCount,
                                             Executor executor, int loadTimeout) {
        List<HearingProvider> providers = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < daysAheadCount; i++) {
            for (String url : urls) {
                providers.add(providerForUrl(date, url));
            }
            date = date.plusDays(1);
        }

        return new CompoundHearingProvider(providers, executor, loadTimeout);
    }

}
