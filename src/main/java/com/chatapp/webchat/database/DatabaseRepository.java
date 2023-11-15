package com.chatapp.webchat.database;

import com.chatapp.webchat.entities.Message;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.chatapp.webchat.entities.ChatRoom;
import com.chatapp.webchat.entities.User;
import com.chatapp.webchat.entities.UserChatRoom;

public class DatabaseRepository {


    public DatabaseRepository() {
    }

    public static List<Message> getMessages(int chatRoomId) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM messages WHERE chat_room_id = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, chatRoomId);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setMessageId(rs.getInt("message_id"));
                message.setUserId(rs.getInt("user_id"));
                message.setChatRoomId(rs.getInt("chat_room_id"));
                message.setContent(rs.getString("content"));
                message.setCreatedAt((rs.getString("created_at")));
                System.out.println("Retrieved message: " + message); // Add this line for debugging
                messages.add(message);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return messages;
    }
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

    public static void createChat(List<UserChatRoom> userChatRooms) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;


        try {

                String sql = "INSERT INTO chat_rooms (name) VALUES (?)";
                preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, userChatRooms.get(0).getChatRoomName());
                preparedStatement.executeUpdate();

                // Get the generated chat_room_id
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                int chatRoomId;
                if (generatedKeys.next()) {
                    chatRoomId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to create chat, no ID obtained.");
                }

            for(UserChatRoom userChatRoom : userChatRooms) {
                // Insert into user_chat_rooms to associate the user with the chat
                createUserChatRoomRelationship(userChatRoom.getUserId(), chatRoomId);
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
    }

    public static List<ChatRoom> getChatsByUserId(int userId) throws NamingException, SQLException {
        List<ChatRoom> chats = new ArrayList<>();
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT c.* FROM chat_rooms c " +
                    "JOIN user_chat_rooms ucr ON c.chat_room_id = ucr.chat_room_id " +
                    "WHERE ucr.user_id = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            // Logging: Print the executed SQL query and userId
            System.out.println("Executed SQL Query: " + preparedStatement.toString());
            System.out.println("User ID: " + userId);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                ChatRoom chat = new ChatRoom();
                chat.setChatRoomId(rs.getInt("chat_room_id"));
                chat.setName(rs.getString("name"));
                chat.setCreatedAt(rs.getString("created_at"));
                chat.setUpdatedAt(rs.getString("updated_at"));
                chats.add(chat);
                System.out.println(chat.getChatRoomId() + " " + chat.getName());
            }

            // Logging: Print the result set size
            System.out.println("Result Set Size: " + chats.size());

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

        return chats;
    }


    public static List<User> getAllUsers() throws NamingException, SQLException {
        List<User> users = new ArrayList<>();
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM users";
            preparedStatement = conn.prepareStatement(sql);
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

    public static void createUser(String username, String passwordHash) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);
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

    public static User getUserByUsername(String username) throws NamingException, SQLException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/webchat");
        Connection conn = ds.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        User user = new User();

        try {

            String sql = "SELECT * FROM webchat.users WHERE username = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            // Logging: Print the executed SQL query and username
            System.out.println("Executed SQL Query: " + preparedStatement.toString());
            System.out.println("Username: " + username);

            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                // Set other user-related fields as needed
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

        return user;
    }






}
