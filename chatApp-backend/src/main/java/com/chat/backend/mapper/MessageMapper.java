package com.chat.backend.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.chat.backend.model.Message;

public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message map(ResultSet rs, StatementContext ctx) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getLong("message_id"));
        message.setChatId(rs.getLong("chat_id"));
        message.setSenderId(rs.getLong("sender_id"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at"));
        
        // This assumes you might do a JOIN to get sender's name
        try {
            message.setSenderName(rs.getString("sender_name"));
        } catch (SQLException e) {
            // sender_name might not be in the result set depending on the query
        }

        return message;
    }
}