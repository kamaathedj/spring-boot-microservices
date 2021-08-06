package com.kamaathedj.UserService.util;

import com.kamaathedj.UserService.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
@Component
public class util {
    private final AtomicInteger atomicInteger = new AtomicInteger();
    String[] user = "scarlet johnson, cynthia ruthrock, Ryan renolds, Ryan gigs, jason momoa".split(",");
    private final Flux<User> usersFlux = Flux.fromStream(
            Stream.generate(new Supplier<User>() {
                @Override
                public User get() {
                    var id = atomicInteger.incrementAndGet();
                    return new User(id,user[id% user.length]);
                }
            })
    ).delayElements(Duration.ofSeconds(3));

    @Bean
    Flux<User> Users(){
        return usersFlux.publish().autoConnect();
    }
}