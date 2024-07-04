package dev.reed.asyncmarspicservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class AsyncMarsPicServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsyncMarsPicServiceApplication.class, args);
    }
}
