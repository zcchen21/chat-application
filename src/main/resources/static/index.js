"use strict";

id("welcome-form").addEventListener("submit", registration);
id("new-contact-wrapper").addEventListener("submit", addContact);
id("chat-box").addEventListener("submit", sendMessage);

let stompClient = null;
let username = null;
let selectedReceiver = null;

// Register the user with the username entered
function registration(event) {
    username = id("username").value.trim();
    if (username) {
        $.get("/registration/" + username, function() {
            connect();
        }).fail(function (error) {
            if (error.status === 400) {
                alert("Name: " + username + " is already taken!");
            }
        });
    } else {
        alert("Please enter a valid username!");
    }
    event.preventDefault();
}

// Connect a websocket upon the success of registration
function connect() {
    id("signin-page").classList.add("hidden");
    id("dialogue-page").classList.remove("hidden");
    let socket = new SockJS("/websocketApp");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connectionSuccess);
}

// Subscribe to an endpoint for receiving messages upon the success of
// a websocket connection
function connectionSuccess() {
	stompClient.subscribe("/topic/" + username, onMessageReceived);
    console.log(username + " successfully connected!");
    id("signin-username").textContent = "Signed in as: " + username;
}

// Send a message to a selected receiver
function sendMessage(event) {
	let messageContent = id("chatMessage").value.trim();
	if (messageContent && stompClient) {
	    let chatMessage = {
            sender : username,
            content : id("chatMessage").value
        };
        stompClient.send("/app/chat/" + selectedReceiver, {}, JSON.stringify(chatMessage));
        id("chatMessage").value = '';
        let messageElement = document.createElement("p");
        messageElement.textContent = messageContent;
        id("message-list-with-" + selectedReceiver).appendChild(messageElement);
        messageElement.classList.add("chat-bubble-right");
        id("message-list-with-" + selectedReceiver).scrollTop = id("message-list-with-" + selectedReceiver).scrollHeight;
	}
	event.preventDefault();
}

// Process and display a received message on the chat panel
function onMessageReceived(payload) {
	let message = JSON.parse(payload.body);
	let messageElement = document.createElement("p");
    messageElement.textContent = message.content;
    messageElement.classList.add("chat-bubble-left");
	id("message-list-with-" + message.sender).appendChild(messageElement);
	id("message-list-with-" + message.sender).scrollTop = id("message-list-with-" + message.sender).scrollHeight;
}

// Adds a particular user to the contact list
// The user intended to add must have already been registered
function addContact(event) {
    let input = id("new-contact-input").value.trim();
    if (input.length > 0) {
        $.get("/searchUser/" + input, function() {
            let newContact = document.createElement("p");
            newContact.id = input;
            newContact.textContent = input;
            newContact.classList.add("contacts-box");
            id("contacts-list").appendChild(newContact);
            let lineBreak = document.createElement("hr");
            id("contacts-list").appendChild(lineBreak);
            id("new-contact-input").value = "";
            let newChatList = document.createElement("div");
            newChatList.id = "message-list-with-" + input;
            newChatList.classList.add("chat-list");
            id("chat-panel").appendChild(newChatList);
            newChatList.classList.add("hidden");
            newContact.addEventListener("click", function() {
                id("profile-username").textContent = input;
                newChatList.classList.remove("hidden");
                id("chatMessage").disabled = false;
                id("send-message-btn").disabled = false;
                if (selectedReceiver) {
                    id(selectedReceiver).classList.remove("selected");
                    id("message-list-with-" + selectedReceiver).classList.add("hidden");
                }
                selectedReceiver = input;
                newContact.classList.add("selected");
            });
        }).fail(function (error) {
            if (error.status === 400) {
                alert("User: " + input + " does not exist!");
            }
        });
    }
    event.preventDefault();
}

// returns the DOM object of the given id
function id(id) {
  return document.getElementById(id);
}