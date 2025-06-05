package huyquangngo.main.job_hiring.services;

import huyquangngo.main.job_hiring.models.User;

import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User>  getUserByEmail (String email);
    User updateUser(User user);
    Optional<User> getUserById (Long id);

}
