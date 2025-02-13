package dto;

import entity.Role;

import java.sql.Timestamp;

public record UserDTO(String username, String password, String salt, String firstName, String lastName,
                      Timestamp dateOfBirth, String socialNumber, String role) {
}
