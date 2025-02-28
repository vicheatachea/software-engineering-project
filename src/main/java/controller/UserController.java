package controller;

import dao.UserDAO;
import dto.UserDTO;
import entity.UserEntity;
import model.UserModel;

public class UserController {
    private UserModel userModel;
    private boolean userLoggedIn = false;

    public UserController(UserDTO userDTO) {
        this.userModel = new UserModel(userDTO);
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean status) {
        userLoggedIn = status;
    }

    public boolean authenticateUser(String username, String password) {
        UserDAO userDAO = new UserDAO();
        return userDAO.authenticate(username, password);
    }

    public boolean registerUser(UserDTO userDTO) {
        UserModel userModel = new UserModel(userDTO);
        if (!userModel.isValid()) {
            return false;
        }

        UserDAO userDAO = new UserDAO();
        userDAO.persist(new UserEntity(userDTO));
        return true;
    }

    public UserModel getUserModel() {
        return userModel;
    }
}