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
import java.util.concurrent.Callable;

import com.github.mozvip.subtitles.cli.LocaleConverter;
import com.github.mozvip.subtitles.cli.PathConverter;
import com.github.mozvip.subtitles.utils.SubtitleDownloader;
import com.github.mozvip.subtitles.utils.VideoFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "downloadForFolder", description = "Download subtitles for all videos in a folder.")
public class DownloadForFolder implements Callable<Integer> {

	@CommandLine.Option(names={"-o", "-overwrite"}, description="Overwrite existing subtitles")
	private boolean overwrite = false;

	@CommandLine.Option(names={"-f", "-folder"}, description = "Folder that contains videos", required=true, converter= PathConverter.class)
	private List<Path> folders;

	@CommandLine.Option(names={"-l", "-lang"}, description = "Language for subtitles", required=true, converter=LocaleConverter.class)
	private Locale locale;

	@CommandLine.Option(names={"-i", "-ignore"}, description = "Ignore files whose name contain this pattern")
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

	public static void main(String[] argv) {
		int exitCode = new CommandLine(new DownloadForFolder()).execute(argv);
		System.exit(exitCode);
	}


	@Override
	public Integer call() throws Exception {
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
