package com.loadimpact.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class StringUtilsTest {
    @Test
    public void testIsBlank() {
        assertThat(StringUtils.isBlank(null), is(true));
        assertThat(StringUtils.isBlank(""), is(true));
        assertThat(StringUtils.isBlank(" "), is(true));
        assertThat(StringUtils.isBlank("    "), is(true));
        assertThat(StringUtils.isBlank("\t"), is(true));
        assertThat(StringUtils.isBlank("\n"), is(true));
        assertThat(StringUtils.isBlank("\r"), is(true));
        assertThat(StringUtils.isBlank(" \t\r\n"), is(true));
        assertThat(StringUtils.isBlank("  0   "), is(false));
        assertThat(StringUtils.isBlank("  a   "), is(false));
        assertThat(StringUtils.isBlank("  #   "), is(false));
        assertThat(StringUtils.isBlank("  .   "), is(false));
    }

    @Test
    public void testToInitialCase() {
        assertThat(StringUtils.toInitialCase(null), nullValue());
        assertThat(StringUtils.toInitialCase("  "), is("  "));
        assertThat(StringUtils.toInitialCase("a"), is("A"));
        assertThat(StringUtils.toInitialCase("abc"), is("Abc"));
        assertThat(StringUtils.toInitialCase("abcDef"), is("AbcDef"));
        assertThat(StringUtils.toInitialCase(" abcDef"), is(" abcDef"));
    }

    @Test
    public void testStartsWith() {
        assertThat(StringUtils.startsWith("abcdef", "ab"), is(true));
        assertThat(StringUtils.startsWith(" abcdef", " ab"), is(true));
        assertThat(StringUtils.startsWith("abcdef", "x"), is(false));
        assertThat(StringUtils.startsWith(null, "x"), is(false));
        assertThat(StringUtils.startsWith("abc", null), is(false));
        assertThat(StringUtils.startsWith(null, null), is(false));
    }

    @Test
    public void testFixEmpty() {
        assertThat(StringUtils.fixEmpty(null), nullValue());
        assertThat(StringUtils.fixEmpty(""), nullValue());
        assertThat(StringUtils.fixEmpty("  "), nullValue());
        assertThat(StringUtils.fixEmpty("  x "), is("x"));
    }

    @Test
    public void testReplicate() {
        assertThat(StringUtils.replicate("#", 5), is("#####"));
        assertThat(StringUtils.replicate("#", 0), is(""));
        assertThat(StringUtils.replicate("#", -3), is(""));
        assertThat(StringUtils.replicate("", 10), is(""));
        assertThat(StringUtils.replicate(null, 1), nullValue());
        assertThat(StringUtils.replicate(null, 0), nullValue());
        assertThat(StringUtils.replicate(null, -1), nullValue());
    }

    @Test
    public void testMd5() throws Exception {
        String plain = "http://www.ribomation.se/";
        String md5 = "7f03a04ddf64226fc04f601a51783ecf";
        assertThat(StringUtils.md5(plain), is(md5));
    }
}
