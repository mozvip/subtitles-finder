package com.github.mozvip.subtitles.cli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter implements CommandLine.ITypeConverter<Path> {
    @Override
    public Path convert(String value) {
        return Paths.get(value);
    }
}
