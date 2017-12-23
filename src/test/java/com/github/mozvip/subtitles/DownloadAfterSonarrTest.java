package com.github.mozvip.subtitles;

import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DownloadAfterSonarrTest {

    @Test
    public void testMain() throws URISyntaxException {

        URL resource = getClass().getResource("/video_folder/Feud.S01E08.PROPER.720p.HDTV.x264-KILLERS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        ProcessBuilder builder = new ProcessBuilder();
        builder.environment().put("sonarr_eventtype", "Download");
        builder.environment().put("sonarr_episodefile_path", path.toAbsolutePath().toString());

        builder.command();

        DownloadAfterSonarr.main( null );
    }
}