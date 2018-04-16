package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.model.FileHasher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHasherTest {

    @Test
    public void computeHash() throws IOException, InterruptedException, URISyntaxException {
        URL resource = getClass().getResource("/video_folder/SampleVideo_1280x720_1mb.mkv");
        Path path = Paths.get(resource.toURI());
        String s = FileHasher.computeHash(path);
        Assert.assertTrue(s.equals("056f2727bb620820"));
    }
}