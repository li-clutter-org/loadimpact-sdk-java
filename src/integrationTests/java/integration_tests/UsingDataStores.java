package integration_tests;

import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.DataStore;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Verifies that it can create/fetch/delete a DS.
 *
 * @user jens
 * @date 2015-05-15
 */
public class UsingDataStores extends AbstractIntegrationTestBase {
    public static final String DATASTORE_RESOURCE = "datastore.csv";

    @Test 
//    @Ignore("Server-side hangs during upload")
    public void create_get_delete_of_ds_should_pass() throws Exception {
        // Prepare
        String dsName = "integration_test_" + System.nanoTime();
        File   dsFile = File.createTempFile("datastore", ".csv");
        copy(getClass().getResourceAsStream(DATASTORE_RESOURCE), new FileOutputStream(dsFile));

        // Create DS
        DataStore ds = client.createDataStore(dsFile, dsName, 2, DataStore.Separator.SEMICOLON, DataStore.StringDelimiter.DOUBLEQUOTE);
        assertThat(ds, notNullValue());
        assertThat(ds.name, is(dsName));

        // Fetch and wait for it's ready
        final int maxRetries = 10;
        final int delay      = 5 * 1000;
        final int dsId       = ds.id;
        int       retry      = 1;
        do { //TODO: use waitFor() instead 
            ds = client.getDataStore(dsId);
            if (ds.status == DataStore.Status.READY) break;

            System.out.printf("*** Waiting for data-store '" + dsName + "' to become ready. # %d(%d)%n", retry, maxRetries);
            Thread.sleep(delay);
        } while (++retry <= maxRetries);
        assertThat("Waiting for DS to be ready, but it's taken way too long time", ds.status, is(DataStore.Status.READY));
        assertThat(ds.rows, is(3));

        // Fetch all DS
        List<DataStore> allDS = client.getDataStores();
        assertThat(allDS, notNullValue());
        assertThat(allDS.size(), greaterThanOrEqualTo(1));

        // Delete it
        client.deleteDataStore(dsId);
        try {
            client.getDataStore(ds.id);
            fail("Expected exception: NotFound");
        } catch (ApiException ignore) {
        }
    }


    private void copy(InputStream in, OutputStream out) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("Classpath resource not found");
        }
        if (out == null) {
            throw new IllegalArgumentException("File not found");
        }

        final int EOF = -1;
        int       b;
        while ((b = in.read()) != EOF) out.write(b);
        in.close();
        out.close();
    }

}
