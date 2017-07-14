package com.github.mozvip.subtitles;

public class SubTitleFinderFactory {

	public static EpisodeSubtitlesFinder createEpisodeSubtitlesFinder(Class<? extends EpisodeSubtitlesFinder> finderClass) throws InstantiationException, IllegalAccessException {
		Class<?>[] classes = finderClass.getClasses();
		for (Class<?> class1 : classes) {
			
		}
		return finderClass.newInstance();
	}

	public static MovieSubtitlesFinder createMovieSubtitlesFinder(Class<? extends MovieSubtitlesFinder> finderClass) throws InstantiationException, IllegalAccessException {
		Class<?>[] classes = finderClass.getClasses();
		for (Class<?> class1 : classes) {
			
		}		
		return finderClass.newInstance();
	}

}
