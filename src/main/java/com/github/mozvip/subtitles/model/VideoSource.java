package com.github.mozvip.subtitles.model;

import org.apache.commons.lang3.StringUtils;

public enum VideoSource {

	DVD( new String[] {"dvdrip", "dvd-rip"}),
	BLURAY( new String[] {"blu-ray", "BDRip"}),
	HDTV,
	WEB_DL( new String[] {"WEB"});

	private String[] identifiers;

	private VideoSource(String[] identifiers) {
		this.identifiers = identifiers;
	}

	private VideoSource() {
	}

	public boolean match( String text ) {
		if (identifiers != null) {
			for (String identifier : identifiers) {
				if (StringUtils.containsIgnoreCase(text, identifier)) {
					return true;
				}
			}
		}
		return StringUtils.containsIgnoreCase(text, name());
	}

	public static VideoSource findMatch( String text ) {
		for (VideoSource source : VideoSource.values()) {
			if (source.match( text )) {
				return source;
			}
		}
		return null;
	}

}
