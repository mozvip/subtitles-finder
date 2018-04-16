package com.github.mozvip.subtitles.srt;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class SRTItem {
    private int index;
    private LocalTime start;
    private LocalTime end;
    private String text;

    protected static DateTimeFormatter withoutMsFormatter = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addText(String line) {
        if (this.text == null) {
            this.text = line;
        } else {
            this.text += "\n" + line;
        }
    }

    public byte[] getSyncID() {
        return String.format("%s%s%s", index, getStart().format(withoutMsFormatter), getEnd().format(withoutMsFormatter)).getBytes();
    }
}
