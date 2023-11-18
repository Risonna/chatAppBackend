package com.chatapp.webchat.websocket;

import com.chatapp.webchat.entities.User;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/messageSocket/{userId}")
public class MessageSocket {
    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        sessions.add(session);
        session.getUserProperties().put("userId", userId);

        // Optionally, notify other clients that a new user has joined
    }


    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // Process the message, e.g., save it to the database

    }


    public static void notifyClientsOnMessageChange(List<Integer> listOfUserIds) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                Integer userId = (Integer) session.getUserProperties().get("userId");
                System.out.println("USER ID IN SESSION IS " + userId);
                for(Integer userIda : listOfUserIds){
                    System.out.println("UserIds in a list are: " + userIda);
                }
                System.out.println(listOfUserIds.size());
                if (userId != null && listOfUserIds.contains(userId)) {
                    try {
                        session.getBasicRemote().sendText("Messages_Updated");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("NotifyClientIf Doesnt Work!!!!");
                }
            }
        }
    }

    public static void notifyClientsOnChatsChange(List<Integer> listOfUserIds){
        for (Session session : sessions) {
            if (session.isOpen()) {
                Integer userId = (Integer) session.getUserProperties().get("userId");
                System.out.println("USER ID IN SESSION IS " + userId);
                for(Integer userIda : listOfUserIds){
                    System.out.println("UserIds in a list are: " + userIda);
                }
                if (userId != null && listOfUserIds.contains(userId)) {
                    try {
                        session.getBasicRemote().sendText("Chats_Updated");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("NotifyClientIf Doesnt Work!!!!");
                }
            }
        }
    }
}
