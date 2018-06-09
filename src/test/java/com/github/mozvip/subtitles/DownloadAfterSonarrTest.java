package com.github.mozvip.subtitles;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DownloadAfterSonarrTest {

    private RemoteSubTitles downloadSubtitles(Path path) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("sonarr_eventtype", "Download");
        env.put("sonarr_episodefile_path", path.toAbsolutePath().toString());
        DownloadAfterSonarr downloadAfterSonarr = new DownloadAfterSonarr(env);
        return downloadAfterSonarr.download();
    }

    @Test
    public void testDownload() throws Exception {
        URL resource = getClass().getResource("/video_folder/Feud.S01E08.PROPER.720p.HDTV.x264-KILLERS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        RemoteSubTitles subTitles = downloadSubtitles(path);
        Assert.assertTrue(subTitles.getTitle().contains("KILLERS"));
    }

    @Test
    public void testTheLastMan() throws Exception {
        URL resource = getClass().getResource("/video_folder/The.Last.Man.On.Earth.S03E15.720p.HDTV.x264-AVS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        RemoteSubTitles subTitles = downloadSubtitles(path);
        Assert.assertTrue(subTitles.getTitle().contains("AVS"));
    }


}