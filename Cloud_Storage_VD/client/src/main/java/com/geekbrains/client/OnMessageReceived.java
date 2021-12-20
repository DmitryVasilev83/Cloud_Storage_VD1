package com.geekbrains.client;

import com.geekbrains.core.model.AbstractMessage;

import java.io.IOException;

public interface OnMessageReceived {

    void onReceive(AbstractMessage msg) throws IOException;

}
