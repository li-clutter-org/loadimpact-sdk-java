package integration_tests;

import com.loadimpact.resource.LoadZone;
import com.loadimpact.util.ListUtils;
import com.loadimpact.util.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * Verifies it can fetch load-zone configurations and that they are all defined
 * in  {@link com.loadimpact.resource.LoadZone}.
 *
 * @user jens
 * @date 2015-05-15
 */
public class FetchingLoadZones extends AbstractIntegrationTestBase {

    @Test
    public void fetchSingleTestZoneShouldPass() throws Exception {
        checkSingleZone("amazon", "us", "ashburn");
        checkSingleZone("amazon", "au", "sydney");
        checkSingleZone("rackspace", "uk", "london");
    }

    private void checkSingleZone(String provider, String country, String city) {
        String zoneId = String.format("%s:%s:%s", provider, country, city);

        LoadZone zone = client.getLoadZone(zoneId);
        assertThat(zone, notNullValue());

        assertThat(zone.uid, is(zoneId));
        assertThat(zone.provider.name().toLowerCase(), is(provider));
        assertThat(zone.country.name().toLowerCase(), is(country));
        assertThat(zone.city.toLowerCase(), is(city));
    }

    @Test
    public void fetchAllTestZonesShouldPass() throws Exception {
        List<LoadZone> zones = client.getLoadZone();
        assertThat(zones, notNullValue());
        assertThat(LoadZone.values().length - 1, lessThanOrEqualTo(zones.size()));

        for (LoadZone z : LoadZone.values()) {
            assertThat(zones, hasItem(z));
        }
    }

}
