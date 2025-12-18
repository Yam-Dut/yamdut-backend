package org.yamdut.backend.dao;

import org.yamdut.backend.model.User;


public interface UserDAO {

    void save(User user);

    void markVerified(String email);

    User getUserByUsername(String email);
}
