package com.saad.reservation.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.saad.reservation.core", "com.saad.reservation.rest"})
public class ReservationRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationRestApplication.class, args);
    }
}
