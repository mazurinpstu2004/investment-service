package ru.coursework.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockTransaction {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public void createStockTransaction(Long userId, Long stockId, int count, int total_sum, Date date, Time time) {
        String insertQuery = "INSERT INTO public.stock_transaction (user_id, stock_id, count, total_sum, date, time) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setLong(1, userId);
            insertStatement.setLong(2, stockId);
            insertStatement.setInt(3, count);
            insertStatement.setInt(4, total_sum);
            insertStatement.setDate(5, date);
            insertStatement.setTime(6, time);

            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> getStockTransactionInfo(long userId) {
        String selectQuery = "SELECT * FROM public.stock_transaction_info WHERE user_id = ? ORDER BY transaction_date DESC, transaction_time DESC";

        List<String[]> transactionList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setLong(1, userId);

            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                String stockName = rs.getString("stock_name");
                String quantity = String.valueOf(rs.getInt("quantity"));
                String totalAmount = String.valueOf(rs.getInt("total_amount"));
                String transactionDate = rs.getDate("transaction_date").toString();
                String transactionTime = rs.getTime("transaction_time").toString();

                transactionList.add(new String[]{
                        stockName,
                        quantity,
                        totalAmount,
                        transactionDate,
                        transactionTime
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactionList;
    }
}
