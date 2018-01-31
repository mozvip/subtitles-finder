package com.github.mozvip.subtitles.model;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import com.github.mozvip.subtitles.RegExp;

public class VideoNameParser {

	private final static String NAME_REGEXP = ".+";
	public final static String SEPARATOR_REGEXP = "[\\s\\.]+";
	
	private static String[] filters = new String[] {
			"(.*)" + SEPARATOR_REGEXP + "READNFO" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "FANSUB" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "UNRATED" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "FASTSUB" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "REPACK" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "Theatrical\\s+Cut" + SEPARATOR_REGEXP + "(.*)"
	};

	public static String clean( String title, String[] filtersRegExps ) {
		
		title = title.replace('_', ' ');
		title = title.replaceAll("\\s+", " ");

		boolean mustContinue = true;
		while (mustContinue) {
			mustContinue = false;
			for (String filter : filtersRegExps) {
				String[] groups = RegExp.parseGroups( title,  filter );
				if (groups != null) {
					title = StringUtils.join( groups, " ");
					mustContinue = true;
				}
			}
		}
	
		return title;
	}

    public static VideoInfo getVideoInfo( Path path ) {
	    String title = getTitle(path);
	    return getVideoInfo(title);
    }

	public static VideoInfo getVideoInfo( String title ) {
        VideoInfo info = getEpisodeInfo(null, title);
        if (info == null) {
            info = getMovieInfo(title);
		}
		return info;
	}

	private static String getName(String string) {
		String name = string;
		name = name.replace('.', ' ');
		name = name.replace('_', ' ');

		return name.trim();
	}

	public static TVShowEpisodeInfo getEpisodeInfo( String tvshow, String title ) {

        VideoQuality quality = null;
        if (title.contains("1080")) {
            title = title.replace("1080p", "");
            title = title.replace("1080", "");
            quality = VideoQuality._1080p;
        }
        if (title.contains("2160")) {
            title = title.replace("2160p", "");
            title = title.replace("2160", "");
            quality = VideoQuality._2160p;
        }
        if (title.contains("x264")) {
            title = title.replace("x264", "");
        }

        // remove year from title
        title = title.replaceAll("19\\d{2}|20\\d{2}", "");

        String [] patterns = new String[]{
            "(.*)s(\\d{2})\\.?e(\\d{2})\\.?e(\\d{2})(.*)",
            "(.*)s(\\d{2})\\.?e(\\d{2})(.*)",
            "(.*)(\\d{1,2})\\s+-\\s+(\\d{1,2})\\s+(.*)",
            "(.*)(\\d+)x(\\d+)(.*)",
            "(.*)(\\d{1})(\\d{2})(\\D{1}.*)",
            "(.*)(\\d{2})e?(\\d{2})(.*)",
            "(.*)\\.Part\\.(\\d+)\\.(.*)"
        };

        VideoSource source = VideoSource.findMatch(title);

        for (String pattern:patterns) {
            String[] groups = RegExp.parseGroups(title, pattern);
            if (groups != null) {
                String name = getName( groups[0] );

                name = StringUtils.isEmpty(name) ? tvshow : name;

                if (groups.length == 3) {

                    int firstEpisode = Integer.parseInt(groups[1]);
                    int lastEpisode = firstEpisode;
                    String extraNameData = groups[groups.length - 1];

                    return new TVShowEpisodeInfo(tvshow != null ? tvshow : name, 1, firstEpisode, lastEpisode, quality, source, extraNameData);

                } else {

                    int extractedSeason = Integer.parseInt(groups[1]);
                    int firstEpisode = Integer.parseInt(groups[2]);
                    int lastEpisode = groups.length > 4 ? Integer.parseInt(groups[3]) : firstEpisode;
                    String extraNameData = groups[groups.length - 1];

                    return new TVShowEpisodeInfo(tvshow != null ? tvshow : name, extractedSeason, firstEpisode, lastEpisode, quality, source, extraNameData);
                }
            }
        }

        return null;
	}

	public static MovieInfo getMovieInfo( String title ) {

        VideoQuality quality = null;
        if (title.contains("1080")) {
            title = title.replace("1080p", "");
            title = title.replace("1080", "");
            quality = VideoQuality._1080p;
        }
        if (title.contains("2160")) {
            title = title.replace("2160p", "");
            title = title.replace("2160", "");
            quality = VideoQuality._2160p;
        }

        VideoSource source = VideoSource.findMatch(title);

        String[] groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "\\(?(19\\d{2}|20\\d{2})\\)?(.*)");
        if (groups != null) {
            String name = getName( groups[0] );
            return new MovieInfo( name, Integer.parseInt( groups[1]), quality, source, groups[2] );
        }

        return new MovieInfo( title, -1, null, source, null );
	}

    private static String getTitle(Path file) {
        String filename = file.getFileName().toString();
        String fileNameWithoutExtension = filename;
        fileNameWithoutExtension = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.lastIndexOf('.'));
        return clean( fileNameWithoutExtension, filters );
    }

}
