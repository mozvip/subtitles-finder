package com.github.mozvip.subtitles.srt;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.*;

public class SRTFile {

    public static final String UTF8_BOM = "\uFEFF";

    private static Pattern indexPattern = Pattern.compile("(\\d+)");
    private static Pattern startEndPattern = Pattern.compile("(\\d+:\\d+:\\d+,\\d+) \\-\\-\\> (\\d+:\\d+:\\d+,\\d+)");

    protected static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral(',')
            .appendFraction(MILLI_OF_SECOND, 0, 3, false)
            .toFormatter();

    protected List<SRTItem> items = new ArrayList<>();

    public static SRTFile from(byte[] subtitlesData) throws IOException {
        String string = new String(subtitlesData, Charset.forName("UTF8"));
        StringReader r = new StringReader(string);
        try (BufferedReader reader = new BufferedReader(r)) {
            return fromBufferedReader(reader);
        }
    }

    public static SRTFile fromPath(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return fromBufferedReader(reader);
        }
    }

    public static SRTFile fromBufferedReader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        for (;;) {
            String line = reader.readLine();
            if (line == null)
                break;
            lines.add(line);
        }

        SRTFile srt = new SRTFile();

        SRTItem currentItem = null;

        if (lines.get(0).startsWith(UTF8_BOM)) {
            lines.set(0, lines.get(0).substring(1));
        }

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            line = line.trim();

            Matcher indexMatcher = indexPattern.matcher(line);
            Matcher startEndMatcher = startEndPattern.matcher(line);

            if (indexMatcher.matches()) {

                if (currentItem != null) {
                    srt.items.add(currentItem);
                }

                currentItem = new SRTItem();
                currentItem.setIndex( Integer.parseInt( indexMatcher.group(1) )) ;
            } else if (startEndMatcher.matches()) {
                String start = startEndMatcher.group(1);
                String end = startEndMatcher.group(2);

                currentItem.setStart( LocalTime.from( formatter.parse(start) ) );
                currentItem.setEnd( LocalTime.from( formatter.parse(end) ) );

            } else {
                currentItem.addText(line);
            }
        }

        if (currentItem != null) {
            srt.items.add(currentItem);
        }

        return srt;
    }

    public List<SRTItem> getItems() {
        return items;
    }

    public String computeSyncMD5() throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (SRTItem item : items) {
            md5.update(item.getSyncID());
        }
        return HexBin.encode(md5.digest());
    }

}
