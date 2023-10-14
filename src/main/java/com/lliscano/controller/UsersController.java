package com.lliscano.controller;

import com.lliscano.dto.ResponseDTO;
import com.lliscano.dto.UserDTO;
import com.lliscano.dto.UsersPageDTO;
import com.lliscano.service.UsersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {
    private final UsersService service;
    @GetMapping(value = "/all")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        return new ResponseEntity<>(this.service.getAllUsers(), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<ResponseDTO<UsersPageDTO>> getUsersPagination(
            @RequestParam(name = "page", defaultValue = "0") @NotNull Integer page,
            @RequestParam(name = "size", defaultValue = "10") @NotNull Integer size
    ) {
        return new ResponseEntity<>(this.service.getUsersPagination(page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(
            @PathVariable(name = "id")
            @Min(value = 1, message = "Minimum value {1}")
            @Max(value = Long.MAX_VALUE, message = "Maximun value {"+Long.MAX_VALUE+"}")
            Long id
    ) {
        return new ResponseEntity<>(this.service.getUserById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<Long>> createUser(
            @RequestBody @Valid UserDTO userDTO
    ) {
        return new ResponseEntity<>(this.service.saveUser(userDTO),HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(
            @RequestBody @Valid UserDTO userDTO
    ) {
        return new ResponseEntity<>(this.service.updateUser(userDTO),HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteUser(
            @PathVariable(name = "id") Long id
    ) {
        return new ResponseEntity<>(this.service.deleteUser(id),HttpStatus.OK);
    }
}
