package com.kamaathedj.CustomerService.utilities;

import com.kamaathedj.CustomerService.models.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
@Component
public class Util {

    private final String[] customerArray = "james smith, orlando bloom, john rake, kate einsleigh".split(",");
    private final AtomicInteger atomicInteger = new AtomicInteger();
    public  final Flux<Customer> customers = Flux.fromStream(
            Stream.generate(new Supplier<Customer>() {
                @Override
                public Customer get() {
                    var id = atomicInteger.incrementAndGet();
                    return new Customer(id,customerArray[id%customerArray.length]);
                }
            })
    ).delayElements(Duration.ofSeconds(3));

    @Bean
    Flux<Customer> Customers(){
        return customers.publish().autoConnect();
    }
}
