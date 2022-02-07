package com.datastax.astra.dynamic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.Document;
import com.datastax.stargate.sdk.doc.domain.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Work like Spring Data for Collections.
 */
@Repository
public class DealerStatusRepository {
    
    @Autowired
    private AstraClient astraClient;
    
    @Value("${wu.namespace}")
    private String namespace;
    
    @Value("${wu.dealers}")
    private String dealerCollectionName;
    
    /** Working collection with Document Api. */
    private CollectionClient dealerCollection;
    
    @PostConstruct
    public void initDealerCollection() {
        dealerCollection = astraClient
                .apiStargateDocument()
                .namespace(namespace)
                .collection(dealerCollectionName);
        if (!dealerCollection.exist()) {
            dealerCollection.create();
        }
    }
    
    public List<String> findDealersNames(String status) throws Exception {
        
        // Query docs
        Stream<Document<String>> documents = dealerCollection.findAll(Query.builder()
                .selectAll()
                .where("status").isEqualsTo(status)
                .build());
        
        // Custom Mapping
        return documents.map(Document::getDocument) // remove document ID
                 .map(t -> {
                    try {
                       JsonNode jsonNode = new ObjectMapper().readTree(t);
                       return jsonNode.get("dealerName").asText();
                    } catch (Exception e) {
                       throw new RuntimeException(e);
                    }
                  })
                 .collect(Collectors.toList());
    }
    
    
    
    
   
   
}