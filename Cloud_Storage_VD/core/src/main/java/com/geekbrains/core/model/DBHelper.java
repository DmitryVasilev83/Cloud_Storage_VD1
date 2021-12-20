package com.geekbrains.core.model;

import org.sqlite.JDBC;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private Statement stmt;
    private Connection conn;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(JDBC.PREFIX + "myDB");
            stmt = conn.createStatement();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(User user) throws SQLException {
        String insertQuery = String.format("insert into users values('%s', '%s');",
                user.getLogin(), user.getPassword());
        stmt.execute(insertQuery);
    }


    public void init() throws SQLException {
        String createTable = "create table if not exists users(login TEXT, password TEXT);";
        stmt.execute(createTable);
    }

    public void update(User user) {
    }



    public List<User> select() throws SQLException {       // Метод для DBaseTest
        String query = "select * from users;";
        ResultSet rs = stmt.executeQuery(query);
        ArrayList<User> users = new ArrayList<>();
        while (rs.next()) {
            String login = rs.getString("login");
            String password = rs.getString("password");
            users.add(new User(login, password));
        }
        return users;
    }

    // (Авторизация через базу данных, если юзер отсутствует создаем нового)
    public String selectAuth(String log, String pass) throws SQLException {
        String query = "select * from users WHERE login='" + log + "';";
        ResultSet rs = stmt.executeQuery(query);
        boolean hasResult = rs.next();
        if (hasResult){
            String password = rs.getString("password");
            if (password.equals(pass)){
                return log;
            }else {
                return "Неправильный логин или пароль";
            }
        }else{
            return "Неправильный логин или пароль";
        }
    }

    // регистрация в бд
    public String registrationBD (String log, String pass) throws SQLException{
        String query = "select * from users WHERE login='" + log + "';";
        ResultSet rs = stmt.executeQuery(query);
        boolean hasResult = rs.next();
        if (hasResult){
            return "Имя занято";
        }else{
            insert(new User(log, pass));
            System.out.println("создан новый юзер");
            return log;
        }
    }



    // проверка на уникальность ника
    public boolean checkUniNickname(String nick) {
        String query = "select * from users WHERE nickname='" + nick + "';";
        ResultSet rs1 = null;
        try {
            rs1 = stmt.executeQuery(query);
            String nickname = rs1.getString("nickname");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
