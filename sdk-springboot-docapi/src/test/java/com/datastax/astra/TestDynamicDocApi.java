package com.datastax.astra;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.datastax.astra.dynamic.DealerStatusRepository;

@SpringBootTest
public class TestDynamicDocApi {

    @Autowired
    private DealerStatusRepository dealerStatusRepo;
    
    private static final String SAMPLE_DOC_ID = "bmw_pleasanton";
    
    @Test
    public void createDoc() {
       
        // Delete is already exist
       if (dealerStatusRepo.find(SAMPLE_DOC_ID).isPresent()) {
           dealerStatusRepo.delete(SAMPLE_DOC_ID);
       }
        
        // Create a document with JSON
        dealerStatusRepo.create(SAMPLE_DOC_ID, " {"
                + "\"dealerName\": \"East Bay BMW Inc.\", "
                + "\"address\": \"4341 Rosewood Dr\", "
                + "\"city\": \"Pleasanton\", "
                + "\"state\": \"California\", "
                + "\"zip\":94588, "
                + "\"telephone\": \"(925)746-3245\", "
                + "\"score\":4.50, "
                + "\"status\":\"EXCEPTIONAL\""
                + "}");
        
        // Retrieve Document as a JSON
        Optional<String> dealer = dealerStatusRepo.find(SAMPLE_DOC_ID);
        System.out.println(dealer.get());
        
        // Update The Address with a JSON
        dealerStatusRepo.changeAddress(SAMPLE_DOC_ID, " {"
                + "\"street\": \"Rosewood Dr\", "
                + "\"number\":4341"
                + "}");
        System.out.println("Address updated:");
        System.out.println(dealerStatusRepo.find(SAMPLE_DOC_ID).get());
        
        // Delete the address from the document
        dealerStatusRepo.deleteAddress(SAMPLE_DOC_ID);
        System.out.println("Address deleted:");
        System.out.println(dealerStatusRepo.find(SAMPLE_DOC_ID).get());
        
        // Insert a new sub document with attribute "country"
        dealerStatusRepo.addSubDocument(SAMPLE_DOC_ID, "/country"," {"
                + "\"name\": \"U.S.A\", "
                + "\"continent\":\"America\""
                + "}");
        System.out.println(dealerStatusRepo.find(SAMPLE_DOC_ID).get());
        
    }
}
