package com.geekbrains.core.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileMessage extends AbstractMessage {

    private String fileName;
    private byte[] bytes;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_MESSAGE;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}


