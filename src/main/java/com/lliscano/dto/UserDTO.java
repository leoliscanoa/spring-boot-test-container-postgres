package com.lliscano.commons.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "Required field")
    @Length(max = 100, message = "Maximun {100} characters")
    private String firstname;

    @NotBlank(message = "Required field")
    @Length(max = 100, message = "Maximun {100} characters")
    private String lastname;

    @Length(max = 100, message = "Maximun {100} characters")
    private String gender;

}
