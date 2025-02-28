package model;

import dao.UserDAO;
import dto.UserDTO;
import entity.UserEntity;

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

    public boolean authenticate(String username, String password) {
        UserDAO userDAO = new UserDAO();
        return userDAO.authenticate(username, password);
    }

    public boolean register(UserDTO userDTO) {
        if (!isValid(userDTO)) {
            return false;
        }

        UserDAO userDAO = new UserDAO();
        userDAO.persist(new UserEntity(userDTO));
        return true;
    }

    public boolean isValid(UserDTO userDTO) {
        return isUsernameValid(userDTO.username()) && isPasswordValid(userDTO.password()) &&
                isFirstNameValid(userDTO.firstName()) && isLastNameValid(userDTO.lastName()) &&
                isDateOfBirthValid(userDTO.dateOfBirth()) && isSocialNumberValid(userDTO.socialNumber()) &&
                isRoleValid(userDTO.role());
    }

    private boolean isUsernameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName != null && !firstName.trim().isEmpty();
    }

    private boolean isLastNameValid(String lastName) {
        return lastName != null && !lastName.trim().isEmpty();
    }

    private boolean isDateOfBirthValid(LocalDate dateOfBirth) {
        return dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now());
    }

    private boolean isSocialNumberValid(String socialNumber) {
        return socialNumber != null && socialNumber.matches("\\d{3}-\\d{2}-\\d{4}");
    }

    private boolean isRoleValid(String role) {
        return role != null && (role.equals("STUDENT") || role.equals("TEACHER"));
    }

    // Getters for the fields
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
}