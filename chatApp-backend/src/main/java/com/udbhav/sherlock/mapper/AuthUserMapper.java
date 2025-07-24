package com.udbhav.sherlock.mapper;

import com.udbhav.sherlock.model.AuthUser;
import com.udbhav.sherlock.model.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthUserMapper implements RowMapper<AuthUser> {
    @Override
    public AuthUser map(ResultSet rs, StatementContext ctx) throws SQLException {
        AuthUser user = new AuthUser();
        user.setUserId(rs.getString("userId"));
        user.setUserName(rs.getString("userName"));
        user.setEmailId(rs.getString("emailId"));
        user.setPassword(rs.getString("password"));

        return user;
    }
}
