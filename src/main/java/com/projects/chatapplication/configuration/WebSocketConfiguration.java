package com.projects.chatapplication.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // enables WebSocket message handling backed by a message broker(STOMP)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint for websocket connections
        registry.addEndpoint("/websocketApp").withSockJS();
    }

    @Override
    // enables a message broker to carry the messages back to the client on destinations prefixed with "/topic"
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // enable a simple message broker to carry the message back to the client on destinations prefixed with "/topic"
        registry.enableSimpleBroker("/topic");
        // prefix "/app" is used to define all the message mappings
        registry.setApplicationDestinationPrefixes("/app");
    }
}
