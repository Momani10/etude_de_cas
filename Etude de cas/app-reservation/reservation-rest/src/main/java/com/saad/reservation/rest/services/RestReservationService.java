package com.saad.reservation.rest.services;

import com.saad.reservation.core.dtos.ReservationDTO;

import java.util.List;

public interface RestReservationService {
    List<ReservationDTO> getAllReservations();

    ReservationDTO getReservationById(Long id);

    ReservationDTO createReservation(ReservationDTO reservationDTO);

    ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO);

    void deleteReservation(Long id);
}
