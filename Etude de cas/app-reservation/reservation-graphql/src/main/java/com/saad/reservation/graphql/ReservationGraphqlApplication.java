package com.saad.reservation.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.saad.reservation.core", "com.saad.reservation.graphql"})
public class ReservationGraphqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationGraphqlApplication.class, args);
    }
}