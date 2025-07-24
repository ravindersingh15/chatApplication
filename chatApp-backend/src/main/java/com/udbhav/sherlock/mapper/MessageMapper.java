package com.udbhav.sherlock.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.udbhav.sherlock.model.Message;

public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message map(ResultSet rs, StatementContext ctx) throws SQLException {
        Message message = new Message();
        message.setSenderName(rs.getString("sender_name"));
        message.setReceiverName(rs.getString("receiver_name"));
        message.setMessage(rs.getString("message"));
        return message;
    }


} 