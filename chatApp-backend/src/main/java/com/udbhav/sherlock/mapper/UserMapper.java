package com.udbhav.sherlock.mapper;

import com.udbhav.sherlock.model.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("userId"));
        user.setUserName(rs.getString("userName"));
        user.setEmailId(rs.getString("emailId"));
        return user;
    }
}
