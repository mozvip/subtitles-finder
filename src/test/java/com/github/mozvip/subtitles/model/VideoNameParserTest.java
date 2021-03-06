package com.github.mozvip.subtitles.model;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VideoNameParserTest {

    @Test
    public void testEpisodes() throws IOException, URISyntaxException {
        URL url = VideoNameParserTest.class.getResource("/episodes.txt");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(url.toURI()))) {
            String title;
            while ((title = reader.readLine()) != null) {
                String[] parts = title.split(";");
                VideoInfo info = VideoNameParser.getVideoInfo(parts[0]);
                Assert.assertTrue(parts[0], info != null && info instanceof TVShowEpisodeInfo);
                if (parts.length > 1) {
                    Assert.assertNotNull(String.format("%s : release is not known", parts[0]), info.getRelease());
                    Assert.assertTrue(parts[0], info.getRelease().equalsIgnoreCase(parts[1]));
                }
            }
        }
    }

    @Test
    public void testMovies() throws IOException, URISyntaxException {
        URL url = VideoNameParserTest.class.getResource("/movies.txt");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(url.toURI()))) {
            String title;
            while ((title = reader.readLine()) != null) {
                String[] parts = title.split(";");
                VideoInfo info = VideoNameParser.getMovieInfo(parts[0]);
                Assert.assertTrue(parts[0], info != null && info instanceof MovieInfo);
                if (parts.length > 1) {
                    Assert.assertTrue(parts[0], info.getRelease().equalsIgnoreCase(parts[1]));
                }
            }
        }
    }

    @Test
    public void testParser() throws URISyntaxException {
        URL url = VideoNameParserTest.class.getResource("/video_folder/Wind.River.2017.1080p.BluRay.x264-GECKOS[rarbg].mkv");
        VideoInfo videoInfo = VideoNameParser.getVideoInfo(Paths.get(url.toURI()));
        Assert.assertTrue(videoInfo instanceof MovieInfo && videoInfo.getSource().equals(VideoSource.BLURAY));
    }
}