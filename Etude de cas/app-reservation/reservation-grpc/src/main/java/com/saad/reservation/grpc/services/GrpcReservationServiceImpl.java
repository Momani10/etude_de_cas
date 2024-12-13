package com.saad.reservation.grpc.services;

import com.saad.reservation.core.entities.Chambre;
import com.saad.reservation.core.entities.Client;
import com.saad.reservation.core.entities.Reservation;
import com.saad.reservation.core.entities.enums.StatusReservation;
import com.saad.reservation.core.repositories.ChambreRepository;
import com.saad.reservation.core.repositories.ClientRepository;
import com.saad.reservation.core.repositories.ReservationRepository;
import com.saad.reservation.grpc.stubs.Chambre.ChambreResponse;
import com.saad.reservation.grpc.stubs.Client.ClientResponse;
import com.saad.reservation.grpc.stubs.Common.DeleteResponse;
import com.saad.reservation.grpc.stubs.Common.Empty;
import com.saad.reservation.grpc.stubs.Reservation.CreateReservationRequest;
import com.saad.reservation.grpc.stubs.Reservation.ReservationListResponse;
import com.saad.reservation.grpc.stubs.Reservation.ReservationRequest;
import com.saad.reservation.grpc.stubs.Reservation.ReservationResponse;
import com.saad.reservation.grpc.stubs.ReservationServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("grpcReservationServiceImpl")
public class GrpcReservationServiceImpl extends ReservationServiceGrpc.ReservationServiceImplBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    @Timed(value = "grpc_reservation_getReservations", extraTags = {"method", "getReservations"})
    public void getReservations(Empty request, StreamObserver<ReservationListResponse> responseObserver) {
        try {
            List<ReservationResponse> reservationResponses = reservationRepository.findAll().stream()
                    .map(reservation -> ReservationResponse.newBuilder()
                            .setId(reservation.getId())
                            .setClient(ClientResponse.newBuilder()
                                    .setId(reservation.getClient().getId())
                                    .setNom(reservation.getClient().getNom())
                                    .build())
                            .setChambre(ChambreResponse.newBuilder()
                                    .setId(reservation.getChambre().getId())
                                    .setTypeChambre(reservation.getChambre().getTypeChambre().toString())
                                    .build())
                            .setDateDebut(DATE_FORMAT.format(reservation.getDateDebut()))
                            .setDateFin(DATE_FORMAT.format(reservation.getDateFin()))
                            .setStatus(reservation.getStatus().toString())
                            .setNombrePersonnes(reservation.getNombrePersonnes())
                            .build())
                    .collect(Collectors.toList());

            ReservationListResponse response = ReservationListResponse.newBuilder()
                    .addAllReservations(reservationResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            meterRegistry.counter("grpc_reservation_exceptions", Tags.of("method", "getReservations")).increment();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error fetching reservations: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Timed(value = "grpc_reservation_createReservation", extraTags = {"method", "createReservation"})
    public void createReservation(CreateReservationRequest request, StreamObserver<ReservationResponse> responseObserver) {
        try {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Client with ID " + request.getClientId() + " not found.")
                            .asRuntimeException());

            Chambre chambre = chambreRepository.findById(request.getChambreId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Chambre with ID " + request.getChambreId() + " not found.")
                            .asRuntimeException());

            Date dateDebut;
            Date dateFin;
            try {
                dateDebut = DATE_FORMAT.parse(request.getDateDebut());
                dateFin = DATE_FORMAT.parse(request.getDateFin());
            } catch (ParseException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid date format. Expected yyyy-MM-dd.")
                        .asRuntimeException());
                return;
            }

            StatusReservation status;
            try {
                status = StatusReservation.valueOf(request.getStatus());
            } catch (IllegalArgumentException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid status value: " + request.getStatus())
                        .asRuntimeException());
                return;
            }

            Reservation reservation = new Reservation(null, client, chambre, dateDebut, dateFin, status, request.getNombrePersonnes());
            Reservation savedReservation = reservationRepository.save(reservation);

            ReservationResponse response = ReservationResponse.newBuilder()
                    .setId(savedReservation.getId())
                    .setClient(ClientResponse.newBuilder()
                            .setId(savedReservation.getClient().getId())
                            .setNom(savedReservation.getClient().getNom())
                            .build())
                    .setChambre(ChambreResponse.newBuilder()
                            .setId(savedReservation.getChambre().getId())
                            .setTypeChambre(savedReservation.getChambre().getTypeChambre().toString())
                            .build())
                    .setDateDebut(DATE_FORMAT.format(savedReservation.getDateDebut()))
                    .setDateFin(DATE_FORMAT.format(savedReservation.getDateFin()))
                    .setStatus(savedReservation.getStatus().toString())
                    .setNombrePersonnes(savedReservation.getNombrePersonnes())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            meterRegistry.counter("grpc_reservation_exceptions", Tags.of("method", "createReservation")).increment();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating reservation: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Timed(value = "grpc_reservation_deleteReservation", extraTags = {"method", "deleteReservation"})
    public void deleteReservation(ReservationRequest request, StreamObserver<DeleteResponse> responseObserver) {
        try {
            if (reservationRepository.existsById(request.getId())) {
                reservationRepository.deleteById(request.getId());
                DeleteResponse response = DeleteResponse.newBuilder()
                        .setSuccess(true)
                        .build();
                responseObserver.onNext(response);
            } else {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Reservation with ID " + request.getId() + " not found.")
                        .asRuntimeException());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            meterRegistry.counter("grpc_reservation_exceptions", Tags.of("method", "deleteReservation")).increment();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error deleting reservation: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}


