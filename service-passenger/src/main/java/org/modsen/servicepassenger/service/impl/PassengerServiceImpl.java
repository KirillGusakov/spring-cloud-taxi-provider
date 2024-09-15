package org.modsen.servicepassenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.exception.DuplicateResourceException;
import org.modsen.servicepassenger.mapper.PassengerMapper;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.modsen.servicepassenger.service.PassengerService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDto findById(Long id) {
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new NoSuchElementException("Passenger with this id not found"));
        return passengerMapper.toPassengerResponseDto(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerResponseDto> findAll(Pageable pageable, String email, String name, String phone,
                                              Boolean isDeleted) {
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

        Example<Passenger> example = Example.of(passenger, matcher);

        return passengerRepository.findAll(example, pageable)
                .stream()
                .map(passengerMapper::toPassengerResponseDto)
                .toList();
    }

    @Override
    public PassengerResponseDto save(PassengerRequestDto passengerRequestDto) {
        extracted(passengerRequestDto);

        Passenger passenger = passengerMapper.toPassenger(passengerRequestDto);
        passenger.setIsDeleted(false);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toPassengerResponseDto(savedPassenger);
    }

    @Override
    public PassengerResponseDto update(Long id, PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Passenger with this id not found"));
        extracted(passengerRequestDto);

        passenger.setId(id);
        passenger.setEmail(passengerRequestDto.getEmail());
        passenger.setFirstName(passengerRequestDto.getFirstName());
        passenger.setLastName(passengerRequestDto.getLastName());
        passenger.setPhoneNumber(passengerRequestDto.getPhoneNumber());

        Passenger save = passengerRepository.save(passenger);
        return passengerMapper.toPassengerResponseDto(save);

    }

    @Override
    public void delete(Long id) {
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() ->
                new NoSuchElementException("Passenger with this id not found"));
        passenger.setIsDeleted(true);
        passengerRepository.save(passenger);
    }

    private void extracted(PassengerRequestDto passengerRequestDto) {
        boolean isExisted = passengerRepository.existsByEmail(passengerRequestDto.getEmail());
        if (isExisted) {
            throw new DuplicateResourceException("Passenger with " +
                    passengerRequestDto.getEmail() + " already exists");
        }

        isExisted = passengerRepository.existsByPhoneNumber(passengerRequestDto.getPhoneNumber());
        if (isExisted) {
            throw new DuplicateResourceException("Passenger with " +
                    passengerRequestDto.getPhoneNumber() + " already exists");
        }
    }
}