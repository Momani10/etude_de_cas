package com.saad.reservation.rest.services;

import com.saad.reservation.core.dtos.ChambreDTO;

import java.util.List;

public interface RestChambreService {
    List<ChambreDTO> getAllChambres();

    ChambreDTO getChambreById(Long id);

    ChambreDTO createChambre(ChambreDTO chambreDTO);

    ChambreDTO updateChambre(Long id, ChambreDTO chambreDTO);

    void deleteChambre(Long id);
}
