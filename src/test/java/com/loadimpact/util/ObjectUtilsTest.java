package com.loadimpact.util;

import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.configuration.LoadClip;
import com.loadimpact.resource.configuration.LoadTrack;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class ObjectUtilsTest {
    
    @Test
    public void testCopy() throws Exception {
        List<LoadClip> clips = new ArrayList<LoadClip>();
        clips.add( new LoadClip(25, 111));
        clips.add( new LoadClip(25, 222));
        clips.add( new LoadClip(50, 333));
        LoadTrack track = new LoadTrack(LoadZone.AMAZON_US_ASHBURN, clips);

        LoadTrack copy = (LoadTrack) ObjectUtils.copy(track);
        assertThat(copy, notNullValue());
        assertThat(copy, is(track));
        assertThat(copy.clips.size(), is(track.clips.size()));
        assertThat(copy.clips, is(track.clips));
    }
    
}
