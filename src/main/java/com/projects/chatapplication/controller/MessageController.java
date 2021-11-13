package com.projects.chatapplication.controller;

import com.projects.chatapplication.model.MessageModel;
import com.projects.chatapplication.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{receiver}")
    // Messages that are sent to a selected receiver are first being processed here, then being sent to the receiver
    public void sendMessage(@DestinationVariable String receiver, MessageModel messageModel) {
        System.out.println("handling sent message: " + messageModel + " to: " + receiver);
        // checks if the receiver actually exists in the system
        boolean receiverExists = UserStorage.getInstance().getUsers().contains(receiver);
        if (receiverExists) {
            simpMessagingTemplate.convertAndSend("/topic/" + receiver, messageModel); // send message to the receiver
        }
    }
}
