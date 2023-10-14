package com.lliscano.service;

import com.lliscano.converter.UserConverter;
import com.lliscano.dto.ResponseDTO;
import com.lliscano.dto.UserDTO;
import com.lliscano.dto.UsersPageDTO;
import com.lliscano.entity.Users;
import com.lliscano.exception.RecordNotFoundException;
import com.lliscano.repository.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UsersService {

    private final UsersRepository repository;
    private final UserConverter converter;

    public ResponseDTO<UserDTO> getUserById(Long id) {
        return ResponseDTO.<UserDTO>builder()
                .timestamp(Instant.now())
                .message("User found successfully")
                .data(this.converter.toDto(
                        this.repository
                                .findById(id)
                                .orElseThrow(() -> new RecordNotFoundException("User not found by given id: " + id))))
                .build();
    }

    public ResponseDTO<List<UserDTO>> getAllUsers() {
        return ResponseDTO.<List<UserDTO>>builder()
                .timestamp(Instant.now())
                .message("Users found successfully")
                .data(this.repository.findAll().stream().map(this.converter::toDto).toList())
                .build();
    }

    public ResponseDTO<UsersPageDTO> getUsersPagination(Integer page, Integer size) {
        Page<Users> usersPage = this.repository.findAll(PageRequest.of(page, size));
        return ResponseDTO.<UsersPageDTO>builder()
                .timestamp(Instant.now())
                .message("Users found successfully")
                .data(UsersPageDTO.builder()
                        .users(usersPage.getContent().stream().map(this.converter::toDto).toList())
                        .totalElements(usersPage.getTotalElements())
                        .totalPages(usersPage.getTotalPages())
                        .page(usersPage.getNumber())
                        .size(usersPage.getSize())
                        .build())
                .build();
    }

    public ResponseDTO<Long> saveUser(UserDTO userDTO) {
        Users user = this.repository.save(this.converter.toEntity(userDTO));
        return ResponseDTO.<Long>builder()
                .timestamp(Instant.now())
                .data(user.getId())
                .message("User created successfully")
                .build();
    }

    public ResponseDTO<UserDTO> updateUser(UserDTO userDTO) {
        this.repository.findById(userDTO.getId())
                .orElseThrow(() -> new RecordNotFoundException("No existe el usuario con id: " + userDTO.getId()));
        this.repository.save(this.converter.toEntity(userDTO));
        return ResponseDTO.<UserDTO>builder()
                .timestamp(Instant.now())
                .data(userDTO)
                .message("User updated successfully")
                .build();
    }

    public ResponseDTO<String> deleteUser(Long id) {
        this.repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No existe el usuario con id: " + id));
        this.repository.deleteById(id);
        return ResponseDTO.<String>builder()
                .timestamp(Instant.now())
                .message("User deleted successfully")
                .build();
    }
}
