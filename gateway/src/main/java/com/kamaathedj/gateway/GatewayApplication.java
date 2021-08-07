package com.kamaathedj.gateway;

import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.datatype.Duration;
import java.time.ZonedDateTime;

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
				.route("customers",routeSpec->routeSpec
						.path("/customers")
						.and()
						.between(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(10))
						.filters(filterSpec ->filterSpec.prefixPath("/api/v1"))
						.uri("lb://customers")
				)
				.route("users",routeSpec -> routeSpec
						.path("/users")
						.filters(filterSpec -> filterSpec.
								prefixPath("/api/v1"))
						.uri("lb://users"))
				.route("rewrite path", routeSpec -> routeSpec
//						.host("*.twitter.com")
						.path("/twitter/**").and().host("*.twitter.com")
                        .and()
                        .asyncPredicate(new AsyncPredicate<ServerWebExchange>() {
                            @Override
                            public Publisher<Boolean> apply(ServerWebExchange serverWebExchange) {
                                return Mono.just(serverWebExchange.getRequest().getPath().equals(true));
                            }
                        })
						.filters(filterSpec -> filterSpec.rewritePath("/twitter/(?<segment>.*)",
								"/${segment}"))
						.uri("http://twitter.com/@"))
				.route("host", routeSpec -> routeSpec
						.path("/kamaathedj")

						.uri("http://github.com/kamaathedj"))
				.build();
	}
	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception{
		return http.httpBasic().and()
				.csrf().disable()
				.authorizeExchange()
				.pathMatchers("api/v1").authenticated()
				.anyExchange().permitAll()
				.and()
				.build();
	}
	@Bean
	public MapReactiveUserDetailsService reactiveUserDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
		return new MapReactiveUserDetailsService(user);
	}

//
//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		//@formatter:off
//		return builder.routes()
//				.route("path_route", r -> r.path("/get")
//						.uri("http://httpbin.org"))
//				.route("host_route", r -> r.host("*.myhost.org")
//						.uri("http://httpbin.org"))
//				.route("rewrite_route", r -> r.host("*.rewrite.org")
//						.filters(f -> f.rewritePath("/foo/(?<segment>.*)",
//								"/${segment}"))
//						.uri("http://httpbin.org"))
//				.route("hystrix_route", r -> r.host("*.hystrix.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
//						.uri("http://httpbin.org"))
//				.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//						.uri("http://httpbin.org"))
//				.route("limit_route", r -> r
//						.host("*.limited.org").and().path("/anything/**")
//						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//						.uri("http://httpbin.org"))
//				.route("websocket_route", r -> r.path("/echo")
//						.uri("ws://localhost:9000"))
//				.build();
//		//@formatter:on
//	}

}
