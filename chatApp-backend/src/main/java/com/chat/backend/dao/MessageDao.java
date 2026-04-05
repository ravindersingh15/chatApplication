package com.chat.backend.dao;

import com.chat.backend.model.Message;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;

import java.util.List;

public interface MessageDao {

    @SqlUpdate("INSERT INTO messages (message_id, chat_id, sender_id, content) " +
            "VALUES (:messageId, :chatId, :senderId, :content)")
    void insertMessage(
            @Bind("messageId") Long messageId,
            @Bind("chatId") Long chatId,
            @Bind("senderId") Long senderId,
            @Bind("content") String content);

    @SqlQuery("SELECT m.*, u.user_name AS sender_name " +
               "FROM messages m " +
               "JOIN user u ON m.sender_id = u.user_id " +
               "WHERE m.chat_id = :chatId " +
               "ORDER BY m.created_at ASC")
    List<Message> getMessagesForChat(@Bind("chatId") Long chatId);

}