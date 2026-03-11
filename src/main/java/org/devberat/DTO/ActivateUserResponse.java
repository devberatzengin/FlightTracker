package org.devberat.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devberat.model.UserType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivateUserResponse {

    private UserType userType;
    private String firstName;
    private String email;

}
