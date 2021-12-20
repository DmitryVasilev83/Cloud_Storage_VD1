package com.geekbrains.server;

import com.geekbrains.core.model.*;
import java.sql.SQLException;

public class BaseAuthService implements AuthService{


    @Override
    public String getAuthByLoginAndPassword(String login, String password) throws SQLException {
        DBHelper helper = new DBHelper();
        helper.connect();
        helper.init();
        return helper.selectAuth(login, password);
    }

    @Override
    public String getRegByLoginAndPassword(String login, String password) throws SQLException {
        DBHelper helper = new DBHelper();
        helper.connect();
        helper.init();
        return helper.registrationBD(login, password);
    }



}

