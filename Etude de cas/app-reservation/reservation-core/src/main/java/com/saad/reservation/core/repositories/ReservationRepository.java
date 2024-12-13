package com.saad.reservation.core.repositories;

import com.saad.reservation.core.entities.Reservation;
import com.saad.reservation.core.entities.enums.StatusReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByClientId(Long clientId);

    List<Reservation> findByChambreId(Long chambreId);

    List<Reservation> findByStatus(StatusReservation status);
}
