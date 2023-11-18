package com.chatapp.webchat.database;

import com.chatapp.webchat.entities.ChatRoom;
import com.chatapp.webchat.entities.Message;
import com.chatapp.webchat.entities.User;
import com.chatapp.webchat.entities.UserChatRoom;
import com.chatapp.webchat.websocket.MessageSocket;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Stateless
public class DatabaseRepositoryAsync {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public DatabaseRepositoryAsync() {
    }

    public static void createMessageAsync(int userId, int chatRoomId, String content){
        // Schedule the completion of the method after a certain delay (e.g., 5 seconds)
        scheduler.schedule(() -> {
            try {
                createMessage(userId, chatRoomId, content);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Asynchronous
    public static void createMessage(int userId, int chatRoomId, String content) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO messages (user_id, chat_room_id, content) VALUES (?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, chatRoomId);
            preparedStatement.setString(3, content);
            preparedStatement.executeUpdate();

            List<User> userList = getUsersInChatRoom(chatRoomId);
            List<Integer> userIdList = new ArrayList<>();
            for(User user : userList){
                userIdList.add(user.getUserId());
                System.out.println("List of userIds: " + user.getUserId());
            }

            MessageSocket.notifyClientsOnMessageChange(userIdList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static List<User> getUsersInChatRoom(int chatRoomId) throws NamingException, SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
            conn = ds.getConnection();

            // SQL query to get users associated with the specified chat room
            String sql = "SELECT u.* FROM users u INNER JOIN user_chat_rooms ucr ON u.user_id = ucr.user_id WHERE ucr.chat_room_id = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, chatRoomId);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                // Set other user-related fields as needed
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return users;
    }


    public static void createUserChatRoomRelationship(int userId, int chatRoomId) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO user_chat_rooms (user_id, chat_room_id) VALUES (?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, chatRoomId);
            System.out.println("user id is " + userId + " chatroomId is " + chatRoomId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // Shutdown the scheduler when the application is shutting down
    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
    }


}
