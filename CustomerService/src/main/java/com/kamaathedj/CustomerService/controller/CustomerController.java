package com.kamaathedj.CustomerService.controller;

import com.kamaathedj.CustomerService.models.Customer;
import com.kamaathedj.CustomerService.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CustomerController {
    private final Flux<Customer> customerFlux;

    public CustomerController(Flux<Customer> customerFlux) {
        this.customerFlux = customerFlux;
    }

    @GetMapping(value = "/customers",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> get(){
        return this.customerFlux;
    }

    @GetMapping("/meme")
    public String getData(){
        return "kamaa";
    }
}
