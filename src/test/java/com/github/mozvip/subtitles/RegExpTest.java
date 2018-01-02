package com.github.mozvip.subtitles;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.PatternSyntaxException;

public class RegExpTest {

    @Test
    public void testBadPattern() {
        try {
            RegExp.getPattern("\\f;//\\");
            Assert.fail();
        } catch (PatternSyntaxException e) {
            Assert.assertTrue(true);
        }
    }

}