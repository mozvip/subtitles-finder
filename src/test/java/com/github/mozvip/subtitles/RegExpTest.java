package com.github.mozvip.subtitles;

import com.github.mozvip.subtitles.utils.RegExp;
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