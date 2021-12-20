package com.geekbrains.core.model;


public class ServerFileDownloadRequest extends AbstractMessage{

    String fileName;
    public ServerFileDownloadRequest(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.SERVER_FILE_DOWNLOAD_REQUEST;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

