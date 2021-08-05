package com.kamaathedj.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
		}
	@Bean
	ApplicationListener<RefreshRoutesResultEvent> listener(){
		return refreshRoutesResultEvent -> {
			System.out.println("Routes refreshed .............................");
			var crl  = (CachingRouteLocator) refreshRoutesResultEvent.getSource();
			Flux<Route> routes = crl.getRoutes();
			routes.subscribe(System.out::println);
		};
	}

	@Bean
	RouteLocator routeLocator(RouteLocatorBuilder rlb){
		return rlb.routes()
				.route(routeSpec->routeSpec
						.path("/customers")
						.uri("lb://customers")
				)
				.route(routeSpec -> routeSpec
						.path("/meme")
						.uri("lb://meme"))
				.build();
	}

}
