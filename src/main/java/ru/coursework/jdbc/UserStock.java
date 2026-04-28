package ru.coursework.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserStock {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public List<String[]> getUserStocks(Long userId) {
        String selectQuery = "SELECT * FROM public.user_stock_summary WHERE user_id = ?";

        List<String[]> userStockList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setLong(1, userId);

            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                String stockName = rs.getString("stock_name");
                String stockCount = String.valueOf(rs.getInt("stock_count"));
                String totalPrice = String.valueOf(rs.getInt("total_price"));

                userStockList.add(new String[]{stockName, stockCount, totalPrice});
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userStockList;
    }

    public void addUserStock(Long userId, Long stockId, int count) {
        String checkQuery = "SELECT count FROM public.user_stock WHERE user_id = ? AND stock_id = ?";
        String updateQuery = "UPDATE public.user_stock SET count = count + ? WHERE user_id = ? AND stock_id = ?";
        String insertQuery = "INSERT INTO public.user_stock (user_id, stock_id, count) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setLong(1, userId);
                checkStmt.setLong(2, stockId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, count);
                        updateStmt.setLong(2, userId);
                        updateStmt.setLong(3, stockId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setLong(1, userId);
                        insertStmt.setLong(2, stockId);
                        insertStmt.setInt(3, count);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getUserStockNames(long userId) {
        String query = "SELECT s.company_name FROM public.stock s " +
                "JOIN public.user_stock us ON s.id = us.stock_id " +
                "WHERE us.user_id = ?";
        List<String> stockNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stockNames.add(rs.getString("company_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockNames;
    }

    public int getUserStockCount(Long userId, Long stockId) {
        String selectQuery = "SELECT SUM(count) as total FROM public.user_stock WHERE user_id = ? AND stock_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setLong(1, userId);
            selectStatement.setLong(2, stockId);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getUserStockSummary(long userId) {
        String sql = "CALL calculate_user_stocks_summary(?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.setLong(1, userId);
            callableStatement.registerOutParameter(2, Types.BIGINT);
            callableStatement.execute();

            return callableStatement.getLong(2);

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при расчете стоимости портфеля", e);
        }
    }
}
