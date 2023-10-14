package com.lliscano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersPageDTO {
    private List<UserDTO> users;
    private Integer totalPages;
    private Long totalElements;
    private Integer size;
    private Integer page;
}
