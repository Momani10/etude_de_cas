package com.saad.reservation.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.saad.reservation.core", "com.saad.reservation.soap"})
public class ReservationSoapApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationSoapApplication.class, args);
    }
}
