package com.geekbrains.core.model;

import java.io.IOException;

public class UpdateListServerRequest extends AbstractMessage {


    public UpdateListServerRequest() throws IOException {
    }

    @Override
    public CommandType getType() {
        return CommandType.UPDATE_LIST_SERVER_REQUEST;
    }


}
