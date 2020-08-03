package org.home.hearing.collector.provider;

import org.home.hearing.collector.Hearing;

import java.util.List;

public class AheadHearingProvider implements HearingProvider {

    private final int daysLoadAhead;
    private final String url;

    public AheadHearingProvider(int daysLoadAhead, String url) {
        this.daysLoadAhead = daysLoadAhead;
        this.url = url;
    }

    @Override
    public List<Hearing> getHearings() {
        return null;
    }
}
