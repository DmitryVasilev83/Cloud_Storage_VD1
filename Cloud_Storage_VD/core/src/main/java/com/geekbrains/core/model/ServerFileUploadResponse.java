package com.geekbrains.core.model;


public class ServerFileUploadResponse extends AbstractMessage {


    private String msg = "file uploaded";

    public ServerFileUploadResponse() {

    }

    @Override
    public CommandType getType() {
        return CommandType.SERVER_FILE_UPLOAD_RESPONSE;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
