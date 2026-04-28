package ru.coursework.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Stock {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public List<String[]> getStocks() {
        String selectQuery = "SELECT company_name, price FROM public.stock";
        List<String[]> stockList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                String companyName = rs.getString("company_name");
                String price = String.valueOf(rs.getInt("price"));

                stockList.add(new String[]{companyName, price});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockList;
    }

    public List<String> getStockNames() {
        String selectQuery = "SELECT company_name FROM public.stock";
        List<String> stockNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                stockNames.add(rs.getString("company_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockNames;
    }

    public Long getStockId(String name) {
        String selectQuery = "SELECT id FROM public.stock WHERE company_name = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setString(1, name);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getStockPrice(Long stockId) {
        String selectQuery = "SELECT price FROM public.stock WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setLong(1, stockId);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
