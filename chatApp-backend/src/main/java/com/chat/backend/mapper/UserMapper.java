package com.chat.backend.mapper;

import com.chat.backend.model.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUserName(rs.getString("user_name"));
        user.setEmailId(rs.getString("email_id"));
        user.setLastSeen(rs.getTimestamp("last_seen"));
        user.setOnline(rs.getBoolean("is_online"));
        return user;
    }
}