package org.home.hearing.collector.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private static final String PROPERTIES_FILENAME = "properties.xml";

    private static final String OUTPUT_FILENAME_PROPERTY = "collector.output.filename";
    private static final String LOAD_DAYS_PROPERTY = "collector.load.days";
    private static final String SOURCE_URLS_PROPERTY = "collector.source.urls";
    private static final String LOAD_TIMEOUT_PROPERTY = "collector.load.timeout.millis";

    private final String outputFilename;
    private final List<String> urls;
    private final int loadDays;
    private final int loadTimeoutMillis;

    private Configuration(Properties properties) {
        this.outputFilename = properties.getProperty(OUTPUT_FILENAME_PROPERTY);
        this.loadDays = Integer.parseInt(properties.getProperty(LOAD_DAYS_PROPERTY));
        this.loadTimeoutMillis = Integer.parseInt(properties.getProperty(LOAD_TIMEOUT_PROPERTY));

        String urlString = properties.getProperty(SOURCE_URLS_PROPERTY);
        this.urls = Arrays.stream(urlString.split("\n"))
                          .filter(s -> !s.isBlank())
                          .map(String::trim)
                          .collect(Collectors.toList());
    }

    public List<String> getSourceUrls() {
        return Collections.unmodifiableList(urls);
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public int getLoadDays() {
        return loadDays;
    }

    public int getLoadTimeout() {
        return loadTimeoutMillis;
    }

    public static Configuration load() {
        Properties properties = loadPropertiesFromFile();
        return new Configuration(properties);
    }

    private static Properties loadPropertiesFromFile() {
        Path path = Paths.get(PROPERTIES_FILENAME);
        log.info("Reading properties file: {}", PROPERTIES_FILENAME);
        try (InputStream inputStream = Files.newInputStream(path)) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Can't read property file", exception);
        }
    }

}
