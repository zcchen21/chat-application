package com.projects.chatapplication.controller;

import com.projects.chatapplication.model.MessageModel;
import com.projects.chatapplication.storage.JDBC;
import com.projects.chatapplication.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.sql.SQLException;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{receiver}")
    // Messages that are sent to a selected receiver are first being processed here, then being sent to the receiver
    public void sendMessage(@DestinationVariable String receiver, MessageModel messageModel) throws SQLException {
        String sender = messageModel.getSender();
        String message = messageModel.getContent();
        System.out.println("handling sent message: {" + message + "} from {" + sender + "} to: {" + receiver + "}");
        if (JDBC.getInstance().userExists(receiver)) {
            simpMessagingTemplate.convertAndSend("/topic/" + receiver, messageModel); // send message to the receiver
            try {
                JDBC.getInstance().storeMessage(sender, receiver, message);
            } catch (Exception e) {
                System.out.println("Something went wrong with the server...");
            }
        }
    }
}
