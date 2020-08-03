package org.home.hearing.collector.provider;

import org.home.hearing.collector.Hearing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class SiteUrlHearingProvider implements HearingProvider {
    private static final Logger log = LoggerFactory.getLogger(HearingProvider.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int CONNECT_RETRIES_COUNT = 5;
    private static final int CONNECT_TIMEOUT = 2000;

    private final String url;
    private final LocalDate date;

    SiteUrlHearingProvider(String url, LocalDate date) {
        this.url = url;
        this.date = date;
    }

    @Override
    public List<Hearing> getHearings() {
        String requestUrl = addRequestParametersToUrl(url, date);
        return loadDocument(requestUrl).map(document -> parseDocument(document, date))
                                       .orElse(Collections.emptyList());
    }

    private String addRequestParametersToUrl(String url, LocalDate date) {
        return url + "?" +
                "name=sud_delo" +
                "&srv_num=1" +
                "&H_date=" + date.format(formatter);
    }

    private Optional<Document> loadDocument(String url) {
        log.info("Loading table from {}", url);

        int retryNumber = 0;
        while (retryNumber < CONNECT_RETRIES_COUNT && !Thread.currentThread().isInterrupted()) {
            ++retryNumber;

            try {
                Document document = Jsoup.connect(url).timeout(CONNECT_TIMEOUT).get();
                return Optional.of(document);
            } catch (SocketTimeoutException exception) {
                log.debug("Connection timeout for '{}'", url);
            } catch (IOException exception) {
                log.error("Cannot connect to url: " + url, exception);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private List<Hearing> parseDocument(Document document, LocalDate date) {
        List<Hearing> result = new ArrayList<>();
        String courtName = document.select("head title").text();

        Elements elements = document.select("#tablcont tr");
        if (elements.size() <= 1) {
            return Collections.emptyList();
        }

        String sectionName = "";
        for (int i = 1; i < elements.size(); i++) {
            Element tableRow = elements.get(i);
            List<String> hearingTableData = tableRow.select("td").stream()
                                                    .map(Element::text)
                                                    .collect(Collectors.toList());
            if (hearingTableData.size() == 1) {
                sectionName = hearingTableData.get(0);
            } else if (hearingTableData.size() == 8) {
                result.add(getHearing(hearingTableData, sectionName, courtName, date));
            }
        }

        return result;
    }

    private Hearing getHearing(List<String> data, String sectionName, String courtName, LocalDate date) {
        LocalTime time = LocalTime.parse(data.get(2));
        LocalDateTime dateTime = date.atTime(time);

        return Hearing.builder().setId(data.get(1))
                      .setTime(data.get(2))
                      .setRoom(data.get(3))
                      .setInfo(data.get(4))
                      .setJudge(data.get(5))
                      .setResult(data.get(6))
                      .setType(sectionName)
                      .setCourtName(courtName)
                      .setDateTime(dateTime)
                      .setLaw(getLaw(data.get(4)))
                      .build();
    }

    private String getLaw(String info) {
        List<String> patterns = Arrays.asList("КАТЕГОРИЯ: ",
                                              "КоАП: ст. ",
                                              "КоАП: ст.",
                                              "ст. ",
                                              "ст.");

        for (String pattern : patterns) {
            int startIndex = info.indexOf(pattern);
            if (startIndex == -1) {
                continue;
            }

            int endIndex = info.indexOf(" ", startIndex + pattern.length() + 1);
            return endIndex == -1 ? info.substring(startIndex) : info.substring(startIndex, endIndex);
        }

        return "";
    }
}
