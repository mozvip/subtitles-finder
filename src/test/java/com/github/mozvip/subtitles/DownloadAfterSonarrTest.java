package com.github.mozvip.subtitles;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DownloadAfterSonarrTest {

    private RemoteSubTitles downloadAfterSonarr(Path path) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("sonarr_eventtype", "Download");
        env.put("sonarr_episodefile_path", path.toAbsolutePath().toString());
        DownloadAfterSonarr downloadAfterSonarr = new DownloadAfterSonarr(env);
        return downloadAfterSonarr.download();
    }

    private RemoteSubTitles downloadAfterRadarr(Path path) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("radarr_eventtype", "Download");
        env.put("radarr_moviefile_path", path.toAbsolutePath().toString());
        DownloadAfterSonarr downloadAfterSonarr = new DownloadAfterSonarr(env);
        return downloadAfterSonarr.download();
    }

    @Test
    public void testDownload() throws Exception {
        URL resource = getClass().getResource("/video_folder/Feud.S01E08.PROPER.720p.HDTV.x264-KILLERS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        RemoteSubTitles subTitles = downloadAfterSonarr(path);
        Assert.assertTrue(subTitles.getTitle().contains("KILLERS"));
    }

    @Test
    public void testTheLastMan() throws Exception {
        URL resource = getClass().getResource("/video_folder/The.Last.Man.On.Earth.S03E15.720p.HDTV.x264-AVS[eztv].mkv");
        Path path = Paths.get(resource.toURI());

        RemoteSubTitles subTitles = downloadAfterSonarr(path);
        Assert.assertTrue(subTitles.getTitle().contains("AVS"));
    }

    @Test
    public void testAfterRadarr() throws Exception {
        URL resource = getClass().getResource("/video_folder/Wind.River.2017.1080p.BluRay.x264-GECKOS[rarbg].mkv");
        Path path = Paths.get(resource.toURI());

        RemoteSubTitles subTitles = downloadAfterRadarr(path);
        Assert.assertTrue(subTitles.getTitle().contains("GECKOS"));
    }

}