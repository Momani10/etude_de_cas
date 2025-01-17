package com.saad.reservation.rest.services;

import com.saad.reservation.core.dtos.ClientDTO;

import java.util.List;

public interface RestClientService {
    List<ClientDTO> getAllClients();

    ClientDTO getClientById(Long id);

    ClientDTO createClient(ClientDTO clientDTO);

    ClientDTO updateClient(Long id, ClientDTO clientDTO);

    void deleteClient(Long id);
}
