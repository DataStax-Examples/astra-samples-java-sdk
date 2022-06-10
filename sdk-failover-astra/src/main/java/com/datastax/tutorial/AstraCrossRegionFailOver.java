package com.datastax.tutorial;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.databases.domain.Datacenter;

public class AstraCrossRegionFailOver {
    
    static final Logger LOGGER = LoggerFactory.getLogger(AstraCrossRegionFailOver.class);
    
    public static final String ASTRA_DB_TOKEN    = "AstraCS:gdZaqzmFZszaBTOlLgeecuPs:edd25600df1c01506f5388340f138f277cece2c93cb70f4b5fa386490daa5d44";
    public static final String ASTRA_DB_ID       = "3ed83de7-d97f-4fb6-bf9f-82e9f7eafa23";
    public static final String ASTRA_DB_REGION   = "eu-west-1";
    public static final String ASTRA_DB_KEYSPACE = "netflix";
    
    
    public static void main(String[] args) throws Exception {
        

        // 1) Initialize Astra Client on a DB with multiple REGIONS
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_TOKEN)
                .withDatabaseId(ASTRA_DB_ID)
                .withDatabaseRegion(ASTRA_DB_REGION)
                .enableCql()
                .build()) {

           // 2) list Regions 
           List<String> allRegions = astraClient
                   .apiDevopsDatabases()
                   .database(ASTRA_DB_ID)
                   .find().get().getInfo()
                   .getDatacenters().stream()
                   .map(Datacenter::getRegion)
                   .collect(Collectors.toList());
           LOGGER.info("DB Regions {}", allRegions);
           
           // Loop on one region after the other every 3 invocations
           int currentRegion = 0;
           while(true) {
               
               for(int idx = 0;idx<3;idx++) {
                   showCurrentDCWithRestApi(astraClient);
                   showCurrentDCWithCqlSession(astraClient);
                   Thread.sleep(2000);
               }
               
               // Go the 'next' region in the list
               currentRegion = (currentRegion+1) % allRegions.size();
               LOGGER.info("Enforcing failover to [{}]", allRegions.get(currentRegion));
               astraClient.useRegion(allRegions.get(currentRegion));

               // We could also enfore a failover of currentDC
               //astraClient.getStargateClient().getStargateHttpClient().failoverDatacenter();
           }
        }
    }
    
    public static final void showCurrentDCWithRestApi(AstraClient astraClient) {
        LOGGER.info("Current DC name as retrieved by Rest API {}", astraClient
                    .apiStargateData()
                    .keyspace(ASTRA_DB_KEYSPACE).find().get()
                    .getDatacenters().get(0).getName());
    }

    public static final void showCurrentDCWithCqlSession(AstraClient astraClient) {
        LOGGER.info("Current DC name as retrieved by CqlSession {}", astraClient
                .cqlSession()
                .execute("SELECT data_center FROM system.local")
                .one().getString("data_center"));
    }
}
