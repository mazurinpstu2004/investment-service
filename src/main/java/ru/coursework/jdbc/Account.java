package ru.coursework.jdbc;

import java.sql.*;

public class Account {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";


    public void updateUserAccount(Long userId, int amount) {
        String updateQuery = "UPDATE public.account SET amount = amount + ? WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            updateStatement.setLong(1, amount);
            updateStatement.setLong(2, userId);

            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getBalance(Long userId) {
        String selectQuery = "SELECT amount FROM public.account WHERE user_id = ?";
        int balance = 0;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setLong(1, userId);

            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                balance = rs.getInt("amount");
            }
            return balance;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}