package ru.coursework.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dividend {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public List<String[]> getDividends() {
        String selectQuery = "SELECT * FROM public.dividend_info";
        List<String[]> dividendList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                String companyName = rs.getString("company_name");
                String dividendAmount = String.valueOf(rs.getLong("dividend_amount"));
                String dividendYield = String.valueOf(rs.getDouble("dividend_yield_percent"));
                String paymentDate = rs.getString("payment_date");

                dividendList.add(new String[]{companyName, dividendAmount, dividendYield, paymentDate});
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dividendList;
    }
}
