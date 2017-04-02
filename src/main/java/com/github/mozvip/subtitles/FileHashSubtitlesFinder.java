package com.github.mozvip.subtitles;

import java.util.Locale;

public interface FileHashSubtitlesFinder {
	
	public RemoteSubTitles downloadSubtitlesForFileHash	( String fileHash, long videoByteSize, Locale locale ) throws Exception;
	

}
