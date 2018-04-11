package com.github.mozvip.subtitles;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.mozvip.subtitles.cli.LocaleConverter;
import com.github.mozvip.subtitles.cli.PathConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadForFolder {

	@Parameter(names={"-o", "-overwrite"}, description="Overwrite existing subtitles")
	private boolean overwrite = false;

	@Parameter(names={"-f", "-folder"}, description = "Folder that contains videos", required=true, converter= PathConverter.class)
	private List<Path> folders;

	@Parameter(names={"-l", "-lang"}, description = "Language for subtitles", required=true, converter=LocaleConverter.class)
	private Locale locale;

	@Parameter(names={"-i", "-ignore"}, description = "Ignore files whose name contain this pattern")
	private List<String> ignorePatterns;

	private final static Logger LOGGER = LoggerFactory.getLogger( DownloadForFolder.class);

	public static List<Path> getContents(Path folder, Filter<Path> filter, boolean recursive) throws IOException {
		List<Path> results = new ArrayList<>();
		List<Path> folderResults = new ArrayList<>();
		if (Files.isReadable( folder )) {
			try (DirectoryStream<Path> ds = filter != null ? Files.newDirectoryStream(folder, filter) : Files.newDirectoryStream(folder)) {
				for (Path p : ds) {
					folderResults.add( p );
				}
			}
			for (Path p : folderResults) {
				if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) && recursive) {
					results.addAll( getContents( p, filter, recursive) );
				} else {
					results.add( p );
				}
			}
		} else {
			LOGGER.warn("Folder {} can not be read", folder.toAbsolutePath().toString());
		}
		return results;
	}

	public static void main(String[] argv) throws IOException {

		DownloadForFolder main = new DownloadForFolder();

		JCommander.newBuilder()
				.addObject(main)
				.build()
				.parse(argv);

		SubtitleDownloader downloader = new SubtitleDownloader();

		// -f \\192.168.0.201\Volume_1\series -f \\192.168.0.201\Volume_2\series -f \\192.168.0.202\Volume_1\series -f \\192.168.0.202\Volume_2\series -f \\192.168.0.203\Volume_1\series -f \\192.168.0.203\Volume_2\series -i VOST -l fr

		for (Path folder: main.folders) {
			LOGGER.info("Parsing source folder {}", folder.toAbsolutePath().toString());
			List<Path> contents = getContents(folder, VideoFileFilter.getInstance(), true);
			for (Path path : contents) {

				boolean ignore = false;
				if (main.ignorePatterns != null) {
					for (String pattern: main.ignorePatterns) {
						if (path.getFileName().toString().contains(pattern)) {
							ignore = true;
							break;
						}
					}
				}

				if (ignore) {
					continue;
				}

				try {
					if (downloader.findSubtitlesFor(path, main.locale, main.overwrite)) {
						LOGGER.info("Successfully subtitled {} in {}", path.toAbsolutePath().toString(), main.locale.getLanguage());
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}


	}

}
