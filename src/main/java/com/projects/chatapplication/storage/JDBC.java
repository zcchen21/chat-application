package com.projects.chatapplication.storage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC {
    private String dbUsername = "username";
    private String dbPassword = "password";
    private static JDBC instance;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public static synchronized JDBC getInstance() {
        if (instance == null) {
            instance = new JDBC();
        }
        return instance;
    }

    public ResultSet getUsers() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT username FROM users");
        return resultSet;
    }

    // add the created username and password to the database
    public void addUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(password.getBytes());
        byte[] bytes = messageDigest.digest();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        password = s.toString();
        preparedStatement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
    }

    // authenticate the sign in of an user
    public boolean userAuthentication(String username, String password) throws SQLException, NoSuchAlgorithmException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(password.getBytes());
        byte[] bytes = messageDigest.digest();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        password = s.toString();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM users");
        while (resultSet.next()) {
            if (resultSet.getString("username").equals(username) &&
                    resultSet.getString("password").equals(password)) {
                return true;
            }
        }
        return false;
    }

    // check if a particular user exists in the database
    public boolean userExists(String username) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT username FROM users");
        while (resultSet.next()) {
            if (resultSet.getString("username").equals(username)) {
                return true;
            }
        }
        return false;
    }

    // add a new contact of the user to the database
    public boolean addContact(String user, String friend) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        String query = "SELECT friend FROM contacts WHERE user = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getString("friend").equals(friend)) {  // already added as a contact
                return false;
            }
        }
        preparedStatement = connection.prepareStatement("INSERT INTO contacts (user, friend) VALUES (?, ?)");
        preparedStatement.setString(1, user);
        preparedStatement.setString(2, friend);
        preparedStatement.executeUpdate();
        return true;
    }

    // get all the contacts of an user from the database
    public List<String> getContacts(String user) throws SQLException {
        List<String> result = new ArrayList<>();
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        String query = "SELECT friend FROM contacts WHERE user = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString("friend"));
        }
        return result;
    }

    // store the message to the database
    public void storeMessage(String sender, String receiver, String message) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        preparedStatement = connection.prepareStatement("INSERT INTO messages (sender, receiver, message) VALUES (?, ?, ?)");
        preparedStatement.setString(1, sender);
        preparedStatement.setString(2, receiver);
        preparedStatement.setString(3, message);
        preparedStatement.executeUpdate();
    }

    // get the 10 most recent messages from the database from the conversation between the sender and receiver
    public List<String[]> getMessages(String sender, String receiver) throws SQLException {
        List<String[]> result = new ArrayList<>();
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", dbUsername, dbPassword);
        String query = "SELECT sender, message FROM messages WHERE (sender = ? and receiver = ?) or (sender = ? and receiver = ?) " +
                        "ORDER BY ? LIMIT ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, sender);
        preparedStatement.setString(2, receiver);
        preparedStatement.setString(3, receiver);
        preparedStatement.setString(4, sender);
        preparedStatement.setString(5, "timestamp");
        preparedStatement.setInt(6, 10);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String[] messageInfo = {resultSet.getString("sender"), resultSet.getString("message")};
            result.add(messageInfo);
        }
        return result;
    }
}
