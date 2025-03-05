package dto;

import java.time.LocalDateTime;

public record UserDTO(String username, String password, String firstName, String lastName,
                      LocalDateTime dateOfBirth, String socialNumber, String role) {
}
