package database;

import model.User;
import java.util.List;

public interface IUserRepository {
    boolean authenticate(String username, String password);
    List<User> getAllUsers();
    boolean addUser(User user);
}