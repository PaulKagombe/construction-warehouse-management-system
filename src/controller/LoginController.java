package controller;

import database.UserDAO;
import model.User;

public class LoginController {

    private final UserDAO userDAO = new UserDAO();
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    /**
     * Attempts login. Returns User on success, null on failure.
     */
    public User login(String username, String password) {
        if (username.isBlank() || password.isBlank()) return null;
        User user = userDAO.login(username, password);
        if (user != null) {
            loginAttempts = 0; // reset on success
        } else {
            loginAttempts++;
        }
        return user;
    }

    public boolean isLocked() {
        return loginAttempts >= MAX_ATTEMPTS;
    }

    public int getRemainingAttempts() {
        return Math.max(0, MAX_ATTEMPTS - loginAttempts);
    }

    public void resetAttempts() {
        loginAttempts = 0;
    }
}