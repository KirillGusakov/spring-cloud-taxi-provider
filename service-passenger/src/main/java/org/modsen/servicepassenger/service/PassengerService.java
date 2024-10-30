package org.modsen.servicepassenger.service;

import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PassengerService {
    PassengerResponseDto findById(Long id, String subject);

    Map<String, Object> findAll(Pageable pageable, String email, String name, String phone, Boolean isDeleted);

    PassengerResponseDto save(PassengerRequestDto passengerRequestDto, String subject);

    PassengerResponseDto update(Long id, PassengerRequestDto passengerRequestDto, String subject);

    void delete(Long id, String subject);
}