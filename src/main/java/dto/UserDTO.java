package dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a user in the system.
 * Contains authentication credentials, personal information, and system role designation.
 */
public record UserDTO(String username, String password, String firstName, String lastName,
                      LocalDateTime dateOfBirth, String socialNumber, String role) {
}
