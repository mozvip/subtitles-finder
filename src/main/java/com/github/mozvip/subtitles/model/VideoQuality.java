package com.github.mozvip.subtitles.model;

import org.apache.commons.lang3.StringUtils;

public enum VideoQuality {
	
	SD( new String[] {" SD ", "DVDRIP", "dvd9" } ),
	_720p(new String[] {"720p"}),
	_1080p(new String[] {"1080p"});
	
	private String[] identifiers;
	
	private VideoQuality(String[] identifiers) {
		this.identifiers = identifiers;
	}
	
	public String[] getAliases() {
		return identifiers;
	}
	
	public boolean match( String text ) {
		if (identifiers != null) {
			for (String identifier : identifiers) {
				if (StringUtils.containsIgnoreCase(text, identifier)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static VideoQuality findMatch( String text ) {
		for (VideoQuality quality : VideoQuality.values()) {
			if (quality.match( text )) {
				return quality;
			}
		}
		return null;
	}

}