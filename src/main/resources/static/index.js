"use strict";

let signin_buttons = document.querySelectorAll(".signin-btn");
for (let i = 0; i < signin_buttons.length; i++) {
    signin_buttons[i].addEventListener("click", function() {
        id("start-page").classList.add("hidden");
        id("signup-page").classList.add("hidden");
        id("signin-page").classList.remove("hidden");
    });
}

let signup_buttons = document.querySelectorAll(".signup-btn");
for (let i = 0; i < signup_buttons.length; i++) {
    signup_buttons[i].addEventListener("click", function() {
        id("start-page").classList.add("hidden");
        id("signin-page").classList.add("hidden");
        id("signup-page").classList.remove("hidden");
    });
}

id("signup-form").addEventListener("submit", signup);
id("signin-form").addEventListener("submit", signin);
id("new-contact-wrapper").addEventListener("submit", addContact);
id("chat-box").addEventListener("submit", sendMessage);

let stompClient = null;
let username = null;
let selectedReceiver = null;

// Register the user with the username entered
function signup(event) {
    username = id("signup-username").value.trim();
    let password = id("signup-password").value.trim();
    if (username && password) {
        $.get("/signup/" + username + "/" + password, function() {
            connect();
        }).fail(function (error) {
            if (error.status === 400) {
                alert("Name: " + username + " is already taken!");
            } else if (error.status === 500) {
                alert("Something went wrong with the server...");
            }
        });
    } else {
        alert("Please enter a valid username and password!");
    }
    event.preventDefault();
}

function signin(event) {
    username = id("username").value.trim();
    let password = id("password").value.trim();
    if (username && password) {
        $.get("/signin/" + username + "/" + password, function() {
            connect();
        }).fail(function (error) {
            if (error.status === 400) {
                alert("Invalid username or password!");
            } else if (error.status === 500) {
                alert("Something went wrong with the server...");
            }
        });
    } else {
        alert("Please enter a valid username and password!");
    }
    event.preventDefault();
}

// Connect a websocket upon the success of registration
function connect() {
    id("signup-page").classList.add("hidden");
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
    displayAllContacts();
}

// Send a message to a selected receiver
function sendMessage(event) {
	let messageContent = id("chatMessage").value.trim();
	if (messageContent && stompClient) {
	    let chatMessage = {
            sender : username,
            content : messageContent
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
    if (input) {
        $.get("/addContact/" + username + "/" + input, function() {
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
                populateChatHistory(username, selectedReceiver, newChatList.id);
            });
        }).fail(function (error) {
            if (error.status === 400) {
                alert("Unable to add " + input + " as a contact!");
            } else if (error.status == 500) {
                alert("Something went wrong with the server...");
            }
        });
    }
    event.preventDefault();
}

function displayAllContacts() {
    $.get("/getContacts/" + username, function(response) {
        for (let i = 0; i < response.length; i++) {
            let friend = response[i];
            let newContact = document.createElement("p");
            newContact.id = friend;
            newContact.textContent = friend;
            newContact.classList.add("contacts-box");
            id("contacts-list").appendChild(newContact);
            let lineBreak = document.createElement("hr");
            id("contacts-list").appendChild(lineBreak);
            let newChatList = document.createElement("div");
            newChatList.id = "message-list-with-" + friend;
            newChatList.classList.add("chat-list");
            id("chat-panel").appendChild(newChatList);
            newChatList.classList.add("hidden");
            newContact.addEventListener("click", function() {
                id("profile-username").textContent = friend;
                newChatList.classList.remove("hidden");
                id("chatMessage").disabled = false;
                id("send-message-btn").disabled = false;
                if (selectedReceiver) {
                    id(selectedReceiver).classList.remove("selected");
                    id("message-list-with-" + selectedReceiver).classList.add("hidden");
                }
                selectedReceiver = friend;
                newContact.classList.add("selected");
                populateChatHistory(username, selectedReceiver, newChatList.id);
            });
        }
    }).fail(function (error) {
        if (error.status == 500) {
            alert("Something went wrong with the server...");
        }
    });
}

function populateChatHistory(sender, receiver, messageBoard) {
    console.log(id(messageBoard).children.length);
    if (id(messageBoard).children.length == 0) {
        $.get("/getMessageHistory/" + sender + "/" + receiver, function(response) {
            console.log(response);
            for (let i = 0; i < response.length; i++) {
                console.log(response[i]);
                let messageInfo = response[i];
                let messageElement = document.createElement("p");
                messageElement.textContent = messageInfo[1];
                id(messageBoard).appendChild(messageElement);
                if (messageInfo[0] === sender) {
                    messageElement.classList.add("chat-bubble-right");
                } else {
                    messageElement.classList.add("chat-bubble-left");
                }
                id(messageBoard).scrollTop = id(messageBoard).scrollHeight;
            }
        }).fail(function (error) {
           if (error.status == 500) {
               alert("Something went wrong with the server...");
           }
        });
    }
}

// returns the DOM object of the given id
function id(id) {
  return document.getElementById(id);
}