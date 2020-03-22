package com.github.mozvip.subtitles.cli;

import picocli.CommandLine;

import java.util.Locale;

public class LocaleConverter implements CommandLine.ITypeConverter<Locale> {
    @Override
    public Locale convert(String value) {
        return Locale.forLanguageTag(value);
    }
}
