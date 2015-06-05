package com.loadimpact;

import com.loadimpact.resource.LoadZone;

import java.util.List;

/**
 * Application that checks if the given API token can logon to load-impact.
 *
 * @user jens
 * @date 2015-05-24
 */
public class CheckApp {
    public static void main(String[] args) {
        CheckApp app = new CheckApp();
        app.parseArgs(args);
        app.check();
        app.run();
    }

    private String apiToken;

    public void parseArgs(String[] args) {
        if (args.length > 0) {
            apiToken = args[0];
        }
    }

    public void check() {
        if (apiToken == null) {
            System.err.println("Missing API token");
            System.err.println("usage: java -jar <path to LoadImpact SDK jar> <api token>");
            System.exit(1);
        }
    }

    public void run() {
        ApiTokenClient client = new ApiTokenClient(apiToken);
        client.setDebug(true);
        
        List<LoadZone> zones = client.getLoadZone();
        for (LoadZone zone : zones) {
            System.out.println(zone);
        }
    }

}
