package com.udbhav.sherlock.dao;

import com.udbhav.sherlock.model.AuthUser;
import com.udbhav.sherlock.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface AuthUserDao {

    @SqlUpdate("INSERT INTO users (userId, userName, emailId, password) VALUES (:userId, :userName, :emailId, :password)")
    void insertUser(
            @Bind("userId") String userId,
            @Bind("userName") String userName,
            @Bind("emailId") String emailId,
            @Bind("password") String password);

    @SqlQuery("SELECT * FROM users WHERE emailId = :emailId AND password = :password")
    @RegisterBeanMapper(AuthUser.class)
    Optional<AuthUser> findByEmailAndPassword(@Bind("emailId") String emailId, @Bind("password") String password);


}