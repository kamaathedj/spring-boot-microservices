package com.kamaathedj.CustomerService.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamaathedj.CustomerService.models.Customer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@RequiredArgsConstructor

public class CustomerWebsocket {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String from(Customer customer){
        return this.objectMapper.writeValueAsString(customer);
    }
    @Bean
    WebSocketHandler webSocketHandler(Flux<Customer> customerFlux){
        return webSocketSession -> {
            var map = customerFlux.map(this::from)
                    .map(webSocketSession::textMessage);

            return webSocketSession.send(map);
        };
    }
    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler customerwsh){
        return new SimpleUrlHandlerMapping(Map.of("/ws/customers",customerwsh),10);
    }
}
