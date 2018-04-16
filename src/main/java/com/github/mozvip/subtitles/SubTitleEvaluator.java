package com.github.mozvip.subtitles;

import com.github.mozvip.subtitles.model.Release;
import com.github.mozvip.subtitles.model.VideoSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

public class SubTitleEvaluator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubTitleEvaluator.class);

    public static int evaluateSubtitleForRelease(SubtitlesFinder sourceFinder, String subtitleName, Locale desiredLocale, String release, VideoSource source) {
        return evaluateSubtitleForRelease(sourceFinder, subtitleName, null, desiredLocale, release, source);
    }

    public static int evaluateSubtitleForRelease(SubtitlesFinder sourceFinder, String subtitleName, List<String> compatibleReleases, Locale desiredLocale, String videoRelease, VideoSource source) {

        if ((StringUtils.containsIgnoreCase(subtitleName, ".VF.") || StringUtils.containsIgnoreCase(subtitleName, ".FR.") || StringUtils.containsIgnoreCase(subtitleName, ".FR-") ) && !desiredLocale.getLanguage().equals("fr")) {
            return -1;
        }

        if ((StringUtils.containsIgnoreCase(subtitleName, ".EN.") || StringUtils.containsIgnoreCase(subtitleName, ".EN-") || StringUtils.containsIgnoreCase(subtitleName, ".VO-") || StringUtils.containsIgnoreCase(subtitleName, ".VO.") || StringUtils.containsIgnoreCase(subtitleName, ".VOsync.") ) && !desiredLocale.getLanguage().equals("en")) {
            return -1;
        }

        int score = 1;

        if (videoRelease != null) {

            if (compatibleReleases != null && !compatibleReleases.isEmpty()) {
                for (String compatibleRelease : compatibleReleases) {
                    Release matchedRelease = Release.firstMatch(compatibleRelease);
                    if (matchedRelease != null && matchedRelease.match( videoRelease )) {
                        score = 15;
                        break;
                    }
                }
            } else {
                Release subtitleRelease = Release.firstMatch(subtitleName);
                if (subtitleRelease != null && subtitleRelease.match( videoRelease )) {
                    score = 15;
                }
            }

        }

        VideoSource subtitleSource = VideoSource.findMatch(subtitleName);
        if (subtitleSource != null && source != null && subtitleSource.equals( source )) {
            score += 5;
        }

        LOGGER.info("{} - Evaluated {} - score = {}", sourceFinder.getClass().getSimpleName(), subtitleName, score);
        return score;
    }

}
