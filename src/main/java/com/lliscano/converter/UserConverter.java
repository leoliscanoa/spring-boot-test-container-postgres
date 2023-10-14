package com.lliscano.converter;



import com.lliscano.dto.UserDTO;
import com.lliscano.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(Users user);
    Users toEntity(UserDTO userDTO);
}
