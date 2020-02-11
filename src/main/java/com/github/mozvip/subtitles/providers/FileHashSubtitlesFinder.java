package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;

import java.util.Locale;

public interface FileHashSubtitlesFinder {
	
	public RemoteSubTitles downloadSubtitlesForFileHash	(String fileHash, long videoByteSize, Locale locale ) throws Exception;
	

}
