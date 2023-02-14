package com.codergm.CloudGateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication

public class CloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGatewayApplication.class, args);
	}

	@Bean
	KeyResolver userKeyResolver(){
		return exchange -> Mono.just("userKey");
	}
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
		return factory -> factory.configureDefault(
				id -> new Resilience4JConfigBuilder(id)
						.circuitBreakerConfig(
								CircuitBreakerConfig.custom()
										.failureRateThreshold(5)
										.slowCallRateThreshold(5)
										.waitDurationInOpenState(Duration.ofMillis(1000))
										.slowCallDurationThreshold(Duration.ofSeconds(2))
										.permittedNumberOfCallsInHalfOpenState(3)
										.minimumNumberOfCalls(5)
										.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
										.slidingWindowSize(5)
										.automaticTransitionFromOpenToHalfOpenEnabled(true)
										.permittedNumberOfCallsInHalfOpenState(3)
										.build()
						).build()
		);
	}
}
