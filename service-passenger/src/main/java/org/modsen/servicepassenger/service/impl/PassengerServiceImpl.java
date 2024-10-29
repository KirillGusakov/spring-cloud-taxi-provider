package org.modsen.servicepassenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.exception.DuplicateResourceException;
import org.modsen.servicepassenger.mapper.PassengerMapper;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.modsen.servicepassenger.service.PassengerService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDto findById(Long id) {
        log.info("Finding passenger by id: {}", id);
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger with id = " + id + " not found"));
        return passengerMapper.toPassengerResponseDto(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerResponseDto> findAll(Pageable pageable, String email, String name, String phone,
                                              Boolean isDeleted) {
        log.info("Finding all passengers with filters: email={}, name={}, phone={}, isDeleted={}", email, name, phone, isDeleted);
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
                .map(passengerMapper::toPassengerResponseDto);
    }

    @Override
    public PassengerResponseDto save(PassengerRequestDto passengerRequestDto) {
        log.info("Saving new passenger: {}", passengerRequestDto);
        extracted(passengerRequestDto, 0L);

        Passenger passenger = passengerMapper.toPassenger(passengerRequestDto);
        passenger.setIsDeleted(false);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toPassengerResponseDto(savedPassenger);
    }

    @Override
    public PassengerResponseDto update(Long id, PassengerRequestDto passengerRequestDto) {
        log.info("Updating passenger with id: {}. New data: {}", id, passengerRequestDto);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger with id = " + id + " not found"));
        extracted(passengerRequestDto, id);

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
        log.info("Deleting passenger with id: {}", id);
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger with id = " + id + " not found"));
        passenger.setIsDeleted(true);
        passengerRepository.save(passenger);
    }

    private void extracted(PassengerRequestDto passengerRequestDto, Long id) {
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
}