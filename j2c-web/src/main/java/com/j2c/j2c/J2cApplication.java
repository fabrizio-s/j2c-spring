package com.j2c.j2c;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@Slf4j
@SpringBootApplication
public class J2cApplication {

	@Value("${server.port}")
	private int port;

	public static void main(final String[] args) {
		new SpringApplicationBuilder(J2cApplication.class)
				.properties("spring.config.additional-location=optional:file:j2c.properties")
				.run(args);
	}

	@EventListener
	@Order
	public void onApplicationReadyEvent(final ApplicationReadyEvent event) {
		log.info("Application running on port " + port);
	}

}
