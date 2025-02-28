package controller;

import dto.UserDTO;
import model.UserModel;

public class UserController {
    private UserModel userModel;
    private boolean userLoggedIn = false;

    public UserController() {
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean status) {
        userLoggedIn = status;
    }

    public boolean authenticateUser(String username, String password) {
        return userModel.authenticate(username, password);
    }

    public boolean registerUser(UserDTO userDTO) {
        return userModel.register(userDTO);
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}