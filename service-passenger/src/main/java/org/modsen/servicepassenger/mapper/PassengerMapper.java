package org.modsen.servicepassenger.mapper;

import org.mapstruct.Mapper;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.model.Passenger;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    Passenger toPassenger(PassengerRequestDto passengerRequestDto);
    PassengerResponseDto toPassengerResponseDto(Passenger passenger);
}