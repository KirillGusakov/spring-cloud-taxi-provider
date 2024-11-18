package org.modsen.servicepassenger.util;

import lombok.RequiredArgsConstructor;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.exception.DuplicateResourceException;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerUtil {

    private final PassengerRepository passengerRepository;

    public void extracted(PassengerRequestDto passengerRequestDto, Long id) {
        boolean isExisted = passengerRepository.existsByEmailAndIdNot(passengerRequestDto.getEmail(), id);
        if (isExisted) {
            throw new DuplicateResourceException("Passenger with " +
                                                 passengerRequestDto.getEmail() + " already exists");
        }

        isExisted = passengerRepository.existsByPhoneNumberAndIdNot(passengerRequestDto.getPhoneNumber(), id);
        if (isExisted) {
            throw new DuplicateResourceException("Passenger with " +
                                                 passengerRequestDto.getPhoneNumber() + " already exists");
        }
    }

    public Example<Passenger> createPassengerExample(String email, String name, String phone, Boolean isDeleted) {
        Passenger passenger = Passenger.builder()
                .email(email)
                .firstName(name)
                .phoneNumber(phone)
                .isDeleted(isDeleted)
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("phoneNumber", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("isDeleted", ExampleMatcher.GenericPropertyMatchers.exact());

        return Example.of(passenger, matcher);
    }
}
