package com.geekbrains.core.model;



public class RegistrationOK extends AbstractMessage{

    String login;

    public RegistrationOK (String login){
        this.login = login;
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION_OK;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}

