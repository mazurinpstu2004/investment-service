package ru.coursework.jdbc;

import ru.coursework.security.PasswordHasher;

import java.sql.*;

public class User {

    private static final PasswordHasher hasher = new PasswordHasher();

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty1";

    public void createUser(String login, String password) {
        String insertQuery = "INSERT INTO public.user (login, password) VALUES (?, ?)";
        String selectQuery = "SELECT * FROM public.user WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setString(1, login);
            ResultSet rs = selectStatement.executeQuery();

            if (!rs.next()) {
                insertStatement.setString(1, login);
                insertStatement.setString(2, password);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getUserId(String login) {
        String selectQuery = "SELECT id FROM public.user WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setString(1, login);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String authenticateUser(String login, String password) {
        String selectQuery = "SELECT password FROM public.user WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setString(1, login);
            ResultSet resultSet = selectStatement.executeQuery();
            if (!resultSet.next()) {
                return "Такого пользователя не существует";
            }
            String hash = resultSet.getString("password");
            if (hasher.checkPassword(password, hash)) {
                return "Вы авторизованы";
            } else {
                return "Неверный пароль";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}