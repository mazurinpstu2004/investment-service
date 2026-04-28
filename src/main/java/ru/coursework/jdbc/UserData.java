package ru.coursework.jdbc;

import java.sql.*;

public class UserData {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public void createUserData(Long userId, String fullname, String email, String number) {
        String insertQuery = "INSERT INTO public.user_data (user_id, fullname, email, number) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setLong(1, userId);
            insertStatement.setString(2, fullname);
            insertStatement.setString(3, email);
            insertStatement.setString(4, number);

            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}