package org.yamdut.dao;

import org.yamdut.model.User;


public interface UserDAO {

    void save(User user);

    void markVerified(String email);

    User getUserByUsername(String email);
}
