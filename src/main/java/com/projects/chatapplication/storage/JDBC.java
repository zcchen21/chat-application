package com.projects.chatapplication.storage;

import java.sql.*;

public class JDBC {
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
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", "user", "password");
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM users");
        return resultSet;
    }

    public void addUser(String username) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", "user", "password");
        preparedStatement = connection.prepareStatement("INSERT INTO users (username) VALUES (?)");
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
    }

    public boolean userExists(String username) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", "user", "password");
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM users");
        while (resultSet.next()) {
            if (resultSet.getString("username").equals(username)) {
                return true;
            }
        }
        return false;
    }
}
