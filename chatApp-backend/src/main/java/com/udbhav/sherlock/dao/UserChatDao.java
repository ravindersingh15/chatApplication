package com.udbhav.sherlock.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface UserChatDao {

    @SqlQuery("SELECT "
            + "CASE "
            + "WHEN user1_id = :userId THEN user2_id "
            + "ELSE user1_id "
            + "END AS other_user_id "
            + "FROM chat_user_mapping "
            + "WHERE user1_id = :userId OR user2_id = :userId "
            + "ORDER BY last_interaction DESC")
    List<String> getOtherUserIds(@Bind("userId") String userId);

    @SqlUpdate("INSERT INTO chat_user_mapping (id, user1_id, user2_id) " +
            "VALUES (:id, :user1Id, :user2Id) " +
            "ON DUPLICATE KEY UPDATE last_interaction = CURRENT_TIMESTAMP")
    void insertChatUserMapping(
            @Bind("id") String id,
            @Bind("user1Id") String user1Id,
            @Bind("user2Id") String user2Id);

}
