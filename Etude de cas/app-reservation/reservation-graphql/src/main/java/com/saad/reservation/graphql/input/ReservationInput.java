package com.saad.reservation.graphql.input;

import com.saad.reservation.core.entities.enums.StatusReservation;
import lombok.Data;

@Data
public class ReservationInput {
    private String clientId;
    private String chambreId;
    private String dateDebut;
    private String dateFin;
    private StatusReservation status;
    private Integer nombrePersonnes;
}