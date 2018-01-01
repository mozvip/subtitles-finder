package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Locale;

import static org.junit.Assert.*;

public class OpenSubtitlesOrgTest {

    @Test
    public void downloadEnglish() throws Exception {
        OpenSubtitlesOrg openSubtitlesOrg = new OpenSubtitlesOrg();
        String fileHash = "b1d1cf2cb577e1a2";
        long size = 6065762652l;
        RemoteSubTitles subTitles = openSubtitlesOrg.downloadSubtitlesForFileHash(fileHash, size, Locale.ENGLISH);
        assertTrue( subTitles != null );
    }

    @Test
    public void downloadPOB() throws Exception {
        OpenSubtitlesOrg openSubtitlesOrg = new OpenSubtitlesOrg();
        String fileHash = "b1d1cf2cb577e1a2";
        long size = 6065762652l;
        RemoteSubTitles subTitles = openSubtitlesOrg.downloadSubtitlesForFileHash(fileHash, size, Locale.forLanguageTag("pob"));
        assertTrue( subTitles != null );
    }

}