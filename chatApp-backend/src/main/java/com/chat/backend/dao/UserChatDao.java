package com.chat.backend.dao;

import com.chat.backend.model.Chat;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UserChatDao {

    @SqlUpdate("INSERT INTO chats (chat_id, chat_type, chat_name, created_by) " +
            "VALUES (:chatId, :chatType, :chatName, :createdBy)")
    void createChat(
            @Bind("chatId") Long chatId,
            @Bind("chatType") String chatType,
            @Bind("chatName") String chatName,
            @Bind("createdBy") Long createdBy);

    @SqlUpdate("INSERT INTO chat_members (id, chat_id, user_id, role) " +
            "VALUES (:id, :chatId, :userId, :role)")
    void addChatMember(
            @Bind("id") Long id,
            @Bind("chatId") Long chatId,
            @Bind("userId") Long userId,
            @Bind("role") String role);

    @SqlQuery("SELECT 1 FROM chat_members WHERE chat_id = :chatId AND user_id = :userId")
    Optional<Integer> isUserInChat(@Bind("chatId") Long chatId, @Bind("userId") Long userId);

    @SqlQuery("SELECT c.* " +
              "FROM chats c " +
              "JOIN chat_members cm ON c.chat_id = cm.chat_id " +
              "LEFT JOIN messages m ON c.last_message_id = m.message_id " +
              "WHERE cm.user_id = :userId " +
              "ORDER BY COALESCE(m.created_at, c.created_at) DESC")
    List<Chat> getChatsForUser(@Bind("userId") Long userId);

    @SqlQuery("SELECT CAST(user_id AS CHAR) FROM chat_members WHERE chat_id = :chatId")
    List<String> getChatMemberIds(@Bind("chatId") Long chatId);

    @SqlQuery("SELECT chat_id FROM chat_members " +
              "WHERE chat_id IN (SELECT chat_id FROM chats WHERE chat_type = 'direct') " +
              "GROUP BY chat_id " +
              "HAVING SUM(CASE WHEN user_id = :user1Id THEN 1 ELSE 0 END) > 0 " +
              "   AND SUM(CASE WHEN user_id = :user2Id THEN 1 ELSE 0 END) > 0 " +
              "   AND COUNT(DISTINCT user_id) = (CASE WHEN :user1Id = :user2Id THEN 1 ELSE 2 END) " +
              "LIMIT 1")
    Optional<Long> findDirectChatBetweenUsers(@Bind("user1Id") Long user1Id, @Bind("user2Id") Long user2Id);

    @SqlUpdate("UPDATE chat_members SET last_read_message_id = :messageId " +
               "WHERE chat_id = :chatId AND user_id = :userId")
    void updateLastReadMessage(
            @Bind("chatId") Long chatId, 
            @Bind("userId") Long userId, 
            @Bind("messageId") Long messageId);

    @SqlUpdate("UPDATE chats SET last_message_id = :messageId WHERE chat_id = :chatId")
    void updateLastMessage(
            @Bind("chatId") Long chatId,
            @Bind("messageId") Long messageId);

}
