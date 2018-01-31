package com.github.mozvip.subtitles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public class DownloadAfterSonarr {

    private final static Logger LOGGER = LoggerFactory.getLogger(DownloadAfterSonarr.class);

    private Map<String, String> environment;

    public DownloadAfterSonarr(Map<String, String> environment) {

        this.environment = environment;

        LOGGER.info("======== Environment Variables");
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            LOGGER.info("{}={}", entry.getKey(), entry.getValue());
        }

    }

    public void download() throws Exception {

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
            downloader.findSubtitlesFor(path, Locale.FRENCH, true);
        }

    }

    public static void main(String[] args) throws Exception {
        new DownloadAfterSonarr( System.getenv() ).download();
    }

}
