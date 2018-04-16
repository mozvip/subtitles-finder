package com.github.mozvip.subtitles;

import com.github.mozvip.subtitles.model.MovieInfo;
import com.github.mozvip.subtitles.model.TVShowEpisodeInfo;
import com.github.mozvip.subtitles.model.VideoInfo;
import com.github.mozvip.subtitles.model.VideoNameParser;
import com.github.mozvip.subtitles.model.FileHasher;
import com.github.mozvip.subtitles.srt.SRTFile;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SubtitleDownloader {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubtitleDownloader.class);

    Set<Class<? extends FileHashSubtitlesFinder>> fileHashSubtitlesFinders;
    Set<Class<? extends MovieSubtitlesFinder>> movieSubtitlesFinders;
    Set<Class<? extends EpisodeSubtitlesFinder>> episodeSubtitlesFinders;

    public SubtitleDownloader() {
        Reflections reflections = new Reflections("com.github.mozvip.subtitles");
        fileHashSubtitlesFinders = reflections.getSubTypesOf(FileHashSubtitlesFinder.class);
        movieSubtitlesFinders = reflections.getSubTypesOf(MovieSubtitlesFinder.class);
        episodeSubtitlesFinders= reflections.getSubTypesOf(EpisodeSubtitlesFinder.class);
    }

    public boolean isBlackListed(RemoteSubTitles subTitles, Set<String> blackListedMD5s) throws IOException, NoSuchAlgorithmException {
        if (blackListedMD5s != null && !blackListedMD5s.isEmpty()) {
            SRTFile srt = SRTFile.from(subTitles.getData());
            if (blackListedMD5s.contains( srt.computeSyncMD5() )) {
                LOGGER.info("CRC for this subtitle is blacklisted");
                return true;
            }
        }
        return false;
    }

    public boolean findSubtitlesFor(Path path, Locale locale, boolean overwrite) throws Exception {

        LOGGER.info("Handling {}", path.getFileName().toString());

        String fileName = path.getFileName().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        Path parentFolder = path.getParent();
        Path destinationFile = parentFolder.resolve(String.format("%s.%s.srt", baseName, locale.getLanguage()));

        if (Files.exists(destinationFile) && !overwrite) {
            LOGGER.info("An existing subtitle has been found and overwrite is not allowed");
            return false;
        }

        Set<String> blackListedMD5s = new HashSet<>();

        String badFileGlob = destinationFile.getFileName().toString();
        badFileGlob = String.format("%s.bad*", badFileGlob.replace('[', '?'));
        DirectoryStream<Path> badFiles = Files.newDirectoryStream(parentFolder, badFileGlob);
        for (Path badFile : badFiles) {
            LOGGER.debug("A 'bad' file is present");
            SRTFile badSRTFile = SRTFile.fromPath(badFile);
            blackListedMD5s.add( badSRTFile.computeSyncMD5() );
        }

        VideoInfo videoInfo = VideoNameParser.getVideoInfo(path);

        LOGGER.info("Searching for subtitles for {}, release = {}, source = {}, lang = {}", path.toAbsolutePath().toString(), videoInfo != null ? videoInfo.getRelease() : "UNKNOWN", videoInfo != null ? videoInfo.getSource() : "UNKNOWN", locale.getLanguage());

        String fileHash = FileHasher.computeHash(path);
        long videoByteSize = Files.size( path );

        RemoteSubTitles currentSubTitles = null;

        for (Class<? extends FileHashSubtitlesFinder> finderClass : fileHashSubtitlesFinders) {
            LOGGER.info("== Searching with {}", finderClass.getName());
            try {
                currentSubTitles = SubTitleFinderFactory.createInstance(finderClass).downloadSubtitlesForFileHash(fileHash, videoByteSize, locale);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (currentSubTitles == null) {

            if (videoInfo == null) {
                return true;
            }

            if (videoInfo instanceof TVShowEpisodeInfo) {

                TVShowEpisodeInfo episodeInfo = (TVShowEpisodeInfo) videoInfo;

                int currentScore = 0;
                for (Class<? extends EpisodeSubtitlesFinder> finderClass : episodeSubtitlesFinders) {
                    try {
                        LOGGER.info("== Searching with {}", finderClass.getName());
                        RemoteSubTitles subTitles = SubTitleFinderFactory.createInstance(finderClass).downloadEpisodeSubtitle(episodeInfo.getName(), episodeInfo.getSeason(), episodeInfo.getFirstEpisode(), episodeInfo.getRelease(), episodeInfo.getSource(), locale);
                        if (subTitles != null && subTitles.getScore() > currentScore) {
                            if (isBlackListed(subTitles, blackListedMD5s)) {
                                continue;
                            }
                            currentScore = subTitles.getScore();
                            currentSubTitles = subTitles;
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

            } else if (videoInfo instanceof MovieInfo) {

                MovieInfo movieInfo = (MovieInfo) videoInfo;

                BigDecimal fps = BigDecimal.valueOf( 25.0d );

                int currentScore = 0;
                for (Class<? extends MovieSubtitlesFinder> finderClass : movieSubtitlesFinders) {
                    RemoteSubTitles subTitles = SubTitleFinderFactory.createInstance(finderClass).downloadMovieSubtitles(movieInfo.getName(), movieInfo.getYear(), movieInfo.getRelease(), movieInfo.getSource(), fps, locale);
                    if (subTitles != null && subTitles.getScore() > currentScore) {
                        if (isBlackListed(subTitles, blackListedMD5s)) {
                            continue;
                        }
                        currentScore = subTitles.getScore();
                        currentSubTitles = subTitles;
                    }
                }
            }

        }

        if (currentSubTitles != null) {
            try (OutputStream output = Files.newOutputStream(destinationFile)) {
                output.write(currentSubTitles.getData());
            }
            return true;
        }

        return false;
    }


}
