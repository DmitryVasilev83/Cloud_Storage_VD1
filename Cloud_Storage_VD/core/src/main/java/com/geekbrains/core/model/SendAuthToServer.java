package com.geekbrains.core.model;

import java.io.IOException;


public class SendAuthToServer extends AbstractMessage {

    String login;
    String pass;

    public SendAuthToServer(String login, String pass) throws IOException {
        this.login = login;
        this.pass = pass;
    }

    @Override
    public CommandType getType() {
        return CommandType.SEND_AUTH_TO_SERVER;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
