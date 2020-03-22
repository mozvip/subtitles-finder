package com.github.mozvip.subtitles;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class DownloadForFolderTest {

    static Path videoPath;

    @BeforeClass
    public static void init() throws URISyntaxException {
        URL video_folder = DownloadForFolderTest.class.getResource("/video_folder");
        videoPath = Paths.get(video_folder.toURI());
    }

    @Test
    public void getContents() throws IOException {
        List<Path> contents = new DownloadForFolder().getContents(videoPath, (Path p) -> true);
        Assert.assertTrue(contents != null && contents.size() > 0);
    }

    @Test
    public void testDownloadSubtitles() throws IOException {
        int downloaded = new CommandLine(new DownloadForFolder()).execute(new String[]{"-f", videoPath.toAbsolutePath().toString(), "-i", "VOSTFR", "-l", "fr", "-o"});
        Assert.assertEquals(5, downloaded);
    }
}