package com.github.mozvip.subtitles;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DownloadAfterSonarr {

    private final static Logger LOGGER = LoggerFactory.getLogger(DownloadAfterSonarr.class);

    public static void main(String[] args) {

        Map<String, String> environment = System.getenv();

        LOGGER.info("======== Environment Variables");
        for (Map.Entry<String, String> entry:environment.entrySet()) {
            LOGGER.info("{}={}", entry.getKey(), entry.getValue());
        }

        String sonarr_eventtype = environment.get("sonarr_eventtype");
        if (sonarr_eventtype == null || !"Download".equals(sonarr_eventtype)) {
            System.exit(-1);
        }

        Path path = Paths.get( System.getenv("sonarr_episodefile_path") );

        SubtitleDownloader downloader = new SubtitleDownloader();
        try {
            downloader.findSubtitlesFor(path, Locale.FRENCH);
            System.exit(0);
        } catch (Exception e) {
            LOGGER.error(String.format("Unexpected error while downloading subtitles for %s", path.toAbsolutePath().toString()), e);
            System.exit(-1);
        }
    }

}
