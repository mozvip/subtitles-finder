package com.github.mozvip.subtitles.cli;

import com.beust.jcommander.IStringConverter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class LocaleConverter implements IStringConverter<Locale> {
    @Override
    public Locale convert(String value) {
        return Locale.forLanguageTag(value);
    }
}
