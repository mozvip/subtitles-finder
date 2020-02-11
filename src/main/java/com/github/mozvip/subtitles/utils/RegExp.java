package com.github.mozvip.subtitles.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExp {

	private static LoadingCache<String, Pattern> patterns = CacheBuilder.newBuilder()
		       .build(
		           new CacheLoader<String, Pattern>() {
		        	   @Override
		        	   public Pattern load(String regex) {
		        		   return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		        	   }
		           });

	private RegExp() {
	}

	public static String[] parseGroups( String text, String regex ) {
		
		text = text.replace('\n', ' ');

		Pattern pattern = getPattern( regex );
		Matcher matcher = pattern.matcher( text );

		List<String> collection = new ArrayList<String>();
		if (matcher.matches()) {
			int count = matcher.groupCount();
			for (int i=1;i<=count; i++) {
				collection.add( matcher.group(i));
			}
		}

		if (!collection.isEmpty()) {
			return collection.toArray(new String[collection.size()]);
		}

		return null;
	}
	
	public static String extract( String text, String regex ) {
		String[] groups = parseGroups(text, regex);
		return groups != null ? groups[0] : null;
	}

	public static Pattern getPattern( String regex ) {
		try {
			return patterns.getUnchecked(regex);
		} catch (UncheckedExecutionException e) {
			throw (PatternSyntaxException) e.getCause();
		}
	}

}
