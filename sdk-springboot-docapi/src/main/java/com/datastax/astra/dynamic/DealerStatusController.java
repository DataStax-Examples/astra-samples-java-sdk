package com.datastax.astra.dynamic;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DealerStatusController {
    
    private DealerStatusRepository repo;
    
    public DealerStatusController(DealerStatusRepository r) {
        this.repo = r;
    }
    
    @GetMapping("/dealers/{status}")
    public List<String> findNames(@PathVariable("status") String status) throws Exception {
        return repo.findDealersNames(status);
    }
    

}
