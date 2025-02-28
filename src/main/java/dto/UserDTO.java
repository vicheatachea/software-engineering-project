package dto;

import java.time.LocalDate;

public record UserDTO(String username, String password, String salt, String firstName, String lastName,
                      LocalDate dateOfBirth, String socialNumber, String role) {

}
