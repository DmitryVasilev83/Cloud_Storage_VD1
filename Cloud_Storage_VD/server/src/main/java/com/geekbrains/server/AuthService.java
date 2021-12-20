package com.geekbrains.server;

import java.sql.SQLException;

public interface AuthService {
    String getAuthByLoginAndPassword(String login, String password) throws SQLException;
    String getRegByLoginAndPassword(String login, String password) throws SQLException;

}
