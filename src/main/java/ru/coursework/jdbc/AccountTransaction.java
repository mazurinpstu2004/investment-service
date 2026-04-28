package ru.coursework.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountTransaction {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public void createAccountTransaction(Long userId, int amount, Date date, Time time) {
        String insertQuery = "INSERT INTO public.account_transaction (account_id, amount, date, time) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setLong(1, userId);
            insertStatement.setInt(2, amount);
            insertStatement.setDate(3, date);
            insertStatement.setTime(4, time);

            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> getAccountTransactions(Long userId) {
        String sql = "SELECT amount, date, time FROM account_transaction WHERE account_id = ? ORDER BY date DESC, time DESC";
        List<String[]> transactions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String amount = String.valueOf(rs.getInt("amount"));
                String date = rs.getDate("date").toString();
                String time = rs.getTime("time").toString();

                transactions.add(new String[]{amount, date, time});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }
}
