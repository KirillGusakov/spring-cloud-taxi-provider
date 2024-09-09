package org.modsen.servicepassenger.service;

import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PassengerService {
    PassengerResponseDto findById(Long id);

    List<PassengerResponseDto> findAll(Pageable pageable, String email, String name, String phone, Boolean isDeleted);

    PassengerResponseDto save(PassengerRequestDto passengerRequestDto);

    PassengerResponseDto update(Long id, PassengerRequestDto passengerRequestDto);

    void delete(Long id);
}