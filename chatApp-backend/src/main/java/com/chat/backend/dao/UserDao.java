package com.chat.backend.dao;

import com.chat.backend.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    @SqlUpdate("INSERT INTO user (user_id, user_name, email_id, password) VALUES (:userId, :userName, :emailId, :password)")
    void insertUser(
            @Bind("userId") Long userId,
            @Bind("userName") String userName,
            @Bind("emailId") String emailId,
            @Bind("password") String password);

    @SqlQuery("SELECT * FROM user WHERE user_id = :userId")
    Optional<User> getUserById(@Bind("userId") Long userId);

    @SqlQuery("SELECT * FROM user")
    List<User> getAllUsers();

    @SqlQuery("SELECT * FROM user WHERE email_id = :emailId AND password = :password")
    Optional<User> findByEmailAndPassword(@Bind("emailId") String emailId, @Bind("password") String password);

    @SqlQuery("SELECT user_id, user_name, email_id, last_seen, is_online FROM user WHERE user_id IN (<ids>)")
    List<User> getUsersByIds(@BindList("ids") List<Long> ids);

    @SqlQuery("SELECT * FROM user WHERE email_id = :emailId")
    Optional<User> getUserByEmail(@Bind("emailId") String emailId);

    @SqlUpdate("UPDATE user SET is_online = :isOnline, last_seen = :lastSeen WHERE user_id = :userId")
    void updateOnlineStatus(@Bind("userId") Long userId, @Bind("isOnline") boolean isOnline, @Bind("lastSeen") Timestamp lastSeen);
}
