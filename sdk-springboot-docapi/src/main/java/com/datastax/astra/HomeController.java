package com.datastax.astra;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller, we want to show the gate.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Controller
public class HomeController {
    
    @GetMapping(value="", produces = "application/html")
    public String hello() {
        return "{ \"api_person\": \"/persons\"}";
    }
}
