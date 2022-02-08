package com.datastax.astra.dynamic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    /** Logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(DealerStatusRepository.class);
    
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
        // Retrieve the client to work with a collection
        dealerCollection = astraClient
                .apiStargateDocument()
                .namespace(namespace)
                .collection(dealerCollectionName);
        // Create if not exist at startup
        if (!dealerCollection.exist()) {
            dealerCollection.create();
            LOGGER.info("Collection {} has been created in keyspace {}", dealerCollectionName, namespace);
        }
    }
   
    public void delete(String dealerid) {
        dealerCollection.document(dealerid).delete();
    }
    
    public void create(String dealerid, String dealerJon) {
        dealerCollection.document(dealerid).upsert(dealerJon);
    }
    
    public void deleteAddress(String dealerid) {
        dealerCollection.document(dealerid).deleteSubDocument("/address");
    }
    
    public Optional<String> find(String dealerid) {
        return dealerCollection.document(dealerid).find();
    }
    
    public void changeAddress(String documentID, String json) {
        dealerCollection
            .document(documentID)
            .updateSubDocument("address", json);
    }
    
    public void addSubDocument(String documentID, String name, String jsonValue) {
        dealerCollection
            .document(documentID)
            .updateSubDocument(name, jsonValue);
    }
    
    public List<String> findDealersNames(String status) throws Exception {
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