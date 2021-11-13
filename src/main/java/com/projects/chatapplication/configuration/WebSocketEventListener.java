package com.projects.chatapplication.configuration;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    // On when a websocket is connected
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection.");
    }

    @EventListener
    // On when a websocket is disconnected
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("Ended a new web socket connection.");
    }
}
