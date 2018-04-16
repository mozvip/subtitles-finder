package com.github.mozvip.subtitles.srt;

import com.github.mozvip.subtitles.DownloadForFolder;
import com.github.mozvip.subtitles.DownloadForFolderTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SRTFileTest {

    static Path subTitlesPath;

    @BeforeClass
    public static void init() throws URISyntaxException {
        URL subTitlesFolder = DownloadForFolderTest.class.getResource("/subtitles");
        subTitlesPath = Paths.get(subTitlesFolder.toURI());
    }

    @Test
    public void getContents() throws IOException, NoSuchAlgorithmException {
        List<Path> contents = DownloadForFolder.getContents(subTitlesPath, (Path p) -> p.getFileName().toString().endsWith(".srt"), true);
        for (Path content : contents) {
            SRTFile srt = SRTFile.fromPath(content);
            srt.computeSyncMD5();
        }
    }

}