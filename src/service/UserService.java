package service;

import database.UserDAO;
import model.User;

import java.util.List;

public class UserService implements BaseService<User> {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean add(User user) {
        return userDAO.addUser(user);
    }

    @Override
    public boolean update(User user) {
        // UserDAO doesn't have an update method, needs implementation
        return false; 
    }

    @Override
    public boolean delete(int id) {
        return userDAO.deleteUser(id);
    }

    @Override
    public User findById(int id) {
        // UserDAO doesn't have findById, needs implementation
        return null; 
    }

    @Override
    public List<User> getAll() {
        return userDAO.getAllUsers();
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }
}