package com.saad.reservation.grpc;

import com.saad.reservation.grpc.server.GrpcServer;
import com.saad.reservation.grpc.services.GrpcChambreServiceImpl;
import com.saad.reservation.grpc.services.GrpcClientServiceImpl;
import com.saad.reservation.grpc.services.GrpcReservationServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.saad.reservation.core", "com.saad.reservation.grpc"})
public class ReservationGrpcApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ReservationGrpcApplication.class, args);
        Thread.currentThread().join();

        GrpcServer server = new GrpcServer(
                new GrpcClientServiceImpl(),
                new GrpcReservationServiceImpl(),
                new GrpcChambreServiceImpl()
        );
        server.start();
        Thread.currentThread().join();
    }
}
