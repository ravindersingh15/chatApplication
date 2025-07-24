package com.udbhav.sherlock.dao;

import com.udbhav.sherlock.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    @SqlUpdate("INSERT INTO users (userId, userName, emailId, password) VALUES (:userId, :userName, :emailId, :password)")
    void insertUser(
            @Bind("userId") String userId,
            @Bind("userName") String userName,
            @Bind("emailId") String emailId,
            @Bind("password") String password);

    @SqlQuery("SELECT * FROM users WHERE userId = :userId")
    @RegisterBeanMapper(User.class)
    Optional<User> getUserById(@Bind("userId") String userId);

    @SqlQuery("SELECT * FROM users")
    @RegisterBeanMapper(User.class)
    List<User> getAllUsers();

    @SqlQuery("SELECT * FROM users WHERE emailId = :emailId AND password = :password")
    @RegisterBeanMapper(User.class)
    Optional<User> findByEmailAndPassword(@Bind("emailId") String emailId, @Bind("password") String password);

    @SqlQuery("SELECT userId, userName, emailId FROM users WHERE userId IN (<ids>)")
    List<User> getUsersByIds(@BindList("ids") List<String> ids);

    @SqlQuery("SELECT * FROM users WHERE emailId = :emailId")
    @RegisterBeanMapper(User.class)
    Optional<User> getUserByEmail(@Bind("emailId") String emailId);
}