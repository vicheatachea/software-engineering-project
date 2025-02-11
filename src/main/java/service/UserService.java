package service;

import dao.UserDAO;
import entity.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(User user) {
        if (userDAO.getUserByEmail(user.getEmail()) != null) {
            System.out.println("User with this email already exists!");
            return false;
        }

        // Hash password before saving
        user.setPassword(hashPassword(user.getPassword()));
        userDAO.saveUser(user);
        return true;
    }

    public boolean loginUser(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user != null && checkPassword(password, user.getPassword())) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Invalid credentials.");
            return false;
        }
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}