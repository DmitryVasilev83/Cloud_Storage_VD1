package com.geekbrains.core.model;


public class AuthOK extends AbstractMessage {
    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    String login;

    public AuthOK (String login){
        this.login = login;
    }

    @Override
    public CommandType getType() {
        return CommandType.AUTH_OK;
    }
}
