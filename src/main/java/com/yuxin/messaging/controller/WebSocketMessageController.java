package com.yuxin.messaging.controller;

import com.yuxin.messaging.model.User;
import com.yuxin.messaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

// client <-> server
// 1. client: subscribe/connect
// 2. server: send message
// 3. server/client: close connection
@Controller
public class WebSocketMessageController extends TextWebSocketHandler {

    @Autowired
    private UserService userService;

    private Map<User, List<WebSocketSession>> userSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // ws://localhost:8080
        List<String> loginTokens = session.getHandshakeHeaders().get("Login-Token");
        User user = this.userService.authenticate(loginTokens.get(0));
        if (!userSessions.containsKey(user)) {
            userSessions.put(user, new ArrayList<>());
        }
        userSessions.get(user).add(session);
    }

    @Scheduled(fixedRate = 5000)
    void sendPeriodicMessage() throws IOException {
        for (var entry : userSessions.entrySet()) {
            for (var session : entry.getValue()) {
                session.sendMessage(new TextMessage("Hey! " + new Date()));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
    }

}
