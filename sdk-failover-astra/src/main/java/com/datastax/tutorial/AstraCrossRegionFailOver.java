package com.datastax.tutorial;

import java.util.List;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.databases.domain.Datacenter;

public class AstraCrossRegionFailOver {
    
    public static final String ASTRA_DB_TOKEN    = "CHANGE_ME";
    public static final String ASTRA_DB_ID       = "CHANGE_ME";
    public static final String ASTRA_DB_REGION   = "CHANGE_ME";
    public static final String ASTRA_DB_KEYSPACE = "CHANGE_ME";
    
    public static void main(String[] args) throws Exception {

        // 1) Initialize Astra Client on a DB with multiple REGIONS
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_TOKEN)
                .withDatabaseId(ASTRA_DB_ID)
                .withDatabaseRegion(ASTRA_DB_REGION)
                .build()) {

           // 2) list Regions 
           List<String> allRegions = astraClient
                   .apiDevopsDatabases()
                   .database(ASTRA_DB_ID)
                   .find().get().getInfo()
                   .getDatacenters().stream()
                   .map(Datacenter::getRegion)
                   .collect(Collectors.toList());
           System.out.println("DB Regions " + allRegions);
           
           // Loop on one region after the other every 3 invocations
           int currentRegion = 0;
           while(true) {
               for(int idx = 0;idx<3;idx++) {
                   showCurrentDCWithRestApi(astraClient);
                   showCurrentDCWithCqlSession(astraClient);
                   Thread.sleep(2000);
               }
               currentRegion = (currentRegion+1) % allRegions.size();
               astraClient.useRegion(allRegions.get(currentRegion));
               // We could also simulate a failover of currentDC
               //astraClient.getStargateClient().getStargateHttpClient().failoverDatacenter();
           }
        }
    }
    
    public static final void showCurrentDCWithRestApi(AstraClient astraClient) {
        System.out.println("DC (api)" + astraClient
                    .apiStargateData()
                    .keyspace(ASTRA_DB_KEYSPACE).find().get()
                    .getDatacenters().get(0).getName());
    }

    public static final void showCurrentDCWithCqlSession(AstraClient astraClient) {
        System.out.println("DC (cql)" + astraClient
                .cqlSession()
                .execute("SELECT data_center FROM system.local")
                .one().getString("data_center"));
    }
}
