package org.modsen.servicepassenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PageResponse;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.exception.AccessDeniedException;
import org.modsen.servicepassenger.mapper.PassengerMapper;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.modsen.servicepassenger.service.PassengerService;
import org.modsen.servicepassenger.util.PassengerUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerUtil passengerUtil;
    private final PassengerMapper passengerMapper;
    private final PassengerRepository passengerRepository;

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDto findById(Long id, String subject) {
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger with id = " + id + " not found"));

        if (checkIsAdmin() || (passenger.getSub() != null && passenger.getSub().toString().equals(subject))) {
            return passengerMapper.toPassengerResponseDto(passenger);
        }

        throw new AccessDeniedException("You can view only your profile");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Pageable pageable,
                                                String email,
                                                String name,
                                                String phone,
                                                Boolean isDeleted) {
        Example<Passenger> example = passengerUtil.createPassengerExample(email, name, phone, isDeleted);

        Page<PassengerResponseDto> passengerPage = passengerRepository.findAll(example, pageable)
                .map(passengerMapper::toPassengerResponseDto);

        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(passengerPage.getNumber())
                .totalItems(passengerPage.getTotalElements())
                .totalPages(passengerPage.getTotalPages())
                .pageSize(passengerPage.getSize())
                .build();

        response.put("passengers", passengerPage.getContent());
        response.put("pageInfo", pageResponse);

        return response;
    }

    @Override
    public PassengerResponseDto save(PassengerRequestDto passengerRequestDto,
                                     String subject) {
        boolean isExist = passengerRepository.existsBySub(UUID.fromString(subject));

        if(isExist) {
            throw new AccessDeniedException("You already have an account");
        }
        passengerUtil.extracted(passengerRequestDto, 0L);

        Passenger passenger = passengerMapper.toPassenger(passengerRequestDto);
        passenger.setIsDeleted(false);
        passenger.setSub(UUID.fromString(subject));

        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toPassengerResponseDto(savedPassenger);
    }

    @Override
    public PassengerResponseDto update(Long id,
                                       PassengerRequestDto passengerRequestDto,
                                       String subject) {

        Passenger passenger = passengerRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Passenger with id = " + id + " not found"));

        if (checkIsAdmin() || (passenger.getSub() != null && passenger.getSub().toString().equals(subject))) {
            passengerUtil.extracted(passengerRequestDto, id);

            passenger.setEmail(passengerRequestDto.getEmail());
            passenger.setFirstName(passengerRequestDto.getFirstName());
            passenger.setLastName(passengerRequestDto.getLastName());
            passenger.setPhoneNumber(passengerRequestDto.getPhoneNumber());

            Passenger savedPassenger = passengerRepository.save(passenger);
            return passengerMapper.toPassengerResponseDto(savedPassenger);
        }
        throw new AccessDeniedException("You can update only your profile");
    }

    @Override
    public void delete(Long id, String subject) {
        Passenger passenger = passengerRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() ->
                new NoSuchElementException("Passenger with id = " + id + " not found"));

        if (checkIsAdmin() || (passenger.getSub() != null && passenger.getSub().toString().equals(subject))) {
            passenger.setIsDeleted(true);
            passengerRepository.save(passenger);
            return;
        }

        throw new AccessDeniedException("You can delete only your profile");
    }

    private boolean checkIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}