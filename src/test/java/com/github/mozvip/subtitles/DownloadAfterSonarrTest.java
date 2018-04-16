package com.github.mozvip.subtitles;

import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DownloadAfterSonarrTest {

    @Test
    public void testDownload() throws Exception {
        URL resource = getClass().getResource("/video_folder/Feud.S01E08.PROPER.720p.HDTV.x264-KILLERS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        Map<String, String> env = new HashMap<>();
        env.put("sonarr_eventtype", "Download");
        env.put("sonarr_episodefile_path", path.toAbsolutePath().toString());
        new DownloadAfterSonarr(env).download();
    }


}