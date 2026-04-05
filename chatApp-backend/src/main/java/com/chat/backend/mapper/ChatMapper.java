package com.chat.backend.mapper;

import com.chat.backend.model.Chat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatMapper implements RowMapper<Chat> {
    @Override
    public Chat map(ResultSet rs, StatementContext ctx) throws SQLException {
        Chat chat = new Chat();
        chat.setChatId(rs.getLong("chat_id"));
        chat.setChatType(rs.getString("chat_type"));
        chat.setChatName(rs.getString("chat_name"));
        chat.setCreatedBy(rs.getLong("created_by"));
        chat.setCreatedAt(rs.getTimestamp("created_at"));
        chat.setLastMessageId(rs.getLong("last_message_id"));
        return chat;
    }
}
