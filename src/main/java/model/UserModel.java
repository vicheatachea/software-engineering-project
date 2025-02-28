package model;

import dto.UserDTO;
import java.time.LocalDate;

public class UserModel {
    private String username;
    private String password;
    private String salt;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String socialNumber;
    private String role;

    public UserModel(UserDTO userDTO) {
        this.username = userDTO.username();
        this.password = userDTO.password();
        this.salt = userDTO.salt();
        this.firstName = userDTO.firstName();
        this.lastName = userDTO.lastName();
        this.dateOfBirth = userDTO.dateOfBirth();
        this.socialNumber = userDTO.socialNumber();
        this.role = userDTO.role();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSocialNumber() {
        return socialNumber;
    }

    public String getRole() {
        return role;
    }

    public boolean isValid() {
        return isUsernameValid() && isPasswordValid() && isFirstNameValid() && isLastNameValid() &&
                isDateOfBirthValid() && isSocialNumberValid() && isRoleValid();
    }

    private boolean isUsernameValid() {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isPasswordValid() {
        return password != null && password.length() >= 8;
    }

    private boolean isFirstNameValid() {
        return firstName != null && !firstName.trim().isEmpty();
    }

    private boolean isLastNameValid() {
        return lastName != null && !lastName.trim().isEmpty();
    }

    private boolean isDateOfBirthValid() {
        return dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now());
    }

    private boolean isSocialNumberValid() {
        return socialNumber != null && socialNumber.matches("\\d{3}-\\d{2}-\\d{4}");
    }

    private boolean isRoleValid() {
        return role != null && (role.equals("STUDENT") || role.equals("TEACHER"));
    }
}