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

	protected boolean mustIgnore(Path path) {
		if (ignorePatterns != null) {
			for (String pattern: ignorePatterns) {
				if (path.getFileName().toString().contains(pattern)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Path> getContents(Path folder, Filter<Path> filter) throws IOException {
		List<Path> results = new ArrayList<>();
		List<Path> folderResults = new ArrayList<>();
		if (Files.isReadable( folder ) && !mustIgnore(folder)) {
			try (DirectoryStream<Path> ds = filter != null ? Files.newDirectoryStream(folder, filter) : Files.newDirectoryStream(folder)) {
				for (Path p : ds) {
					if (!mustIgnore(p)) {
						folderResults.add( p );
					}
				}
			}
			for (Path p : folderResults) {
				if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
					results.addAll( getContents( p, filter) );
				} else {
					results.add( p );
				}
			}
		} else {
			LOGGER.warn("Folder {} can not be read", folder.toAbsolutePath().toString());
		}
		return results;
	}

	public static DownloadForFolder getInstanceFromCmdLine(String[] argv) {
		DownloadForFolder instance = new DownloadForFolder();

		JCommander.newBuilder()
				.addObject(instance)
				.build()
				.parse(argv);

		return instance;
	}

	public static void main(String[] argv) {
		final DownloadForFolder instance = getInstanceFromCmdLine(argv);
		instance.run();
	}

	public int run() {
		SubtitleDownloader downloader = new SubtitleDownloader();

		for (Path folder: folders) {
			LOGGER.info("Parsing source folder {}", folder.toAbsolutePath().toString());
			List<Path> contents;
			try {
				contents = getContents(folder, VideoFileFilter.getInstance());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				continue;
			}
			for (Path path : contents) {
				try {
					if (downloader.findSubtitlesFor(path, locale, overwrite) != null) {
						LOGGER.info("Successfully subtitled {} in {}", path.toAbsolutePath().toString(), locale.getLanguage());
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		return downloader.getDownloadedCount();
	}

}
