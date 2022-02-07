package com.datastax.astra.person;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.stargate.sdk.doc.Document;

@RestController
public class PersonRestController {
    
    private final PersonRepository repo;
    
    public PersonRestController(PersonRepository repo) {
        this.repo = repo;
    }
    
    @GetMapping("/persons")
    public List<Document<Person>> findAll() {
        return repo.findAll().collect(Collectors.toList());
    }

}
