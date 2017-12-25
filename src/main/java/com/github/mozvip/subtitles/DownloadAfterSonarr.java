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

        String eventType = environment.get("sonarr_eventtype");
        if (eventType == null) {
            eventType = environment.get("radarr_eventtype");
        }
        if (eventType != null && !"Grab".equals(eventType)) {
            String videoFilePath = environment.get("sonarr_episodefile_path");
            if (videoFilePath == null) {
                videoFilePath = environment.get("radarr_moviefile_path");
            }
            Path path = Paths.get(videoFilePath);

            SubtitleDownloader downloader = new SubtitleDownloader();
            try {
                downloader.findSubtitlesFor(path, Locale.FRENCH, true);
            } catch (Exception e) {
                LOGGER.error(String.format("Unexpected error while downloading subtitles for %s", path.toAbsolutePath().toString()), e);
            }
        }
    }

}
