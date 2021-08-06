package com.kamaathedj.UserService.controller;

import com.kamaathedj.UserService.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/v1")
public class UserController {
    private final Flux<User> usersFlux;

    public UserController(Flux<User> usersFlux) {
        this.usersFlux = usersFlux;
    }


    @GetMapping(value = "/users",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getUsers(){
        return this.usersFlux;
    }

}
