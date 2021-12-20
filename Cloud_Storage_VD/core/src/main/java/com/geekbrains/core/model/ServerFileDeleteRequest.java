package com.geekbrains.core.model;


public class ServerFileDeleteRequest extends AbstractMessage{

    String fileName;
    public ServerFileDeleteRequest(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.SERVER_FILE_DELETE_REQUEST;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
