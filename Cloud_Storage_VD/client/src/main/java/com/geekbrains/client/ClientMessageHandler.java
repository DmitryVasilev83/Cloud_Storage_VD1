package com.geekbrains.client;

import com.geekbrains.core.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ClientMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final OnMessageReceived callback;

    public ClientMessageHandler(OnMessageReceived callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage abstractMessage) throws Exception {

        callback.onReceive(abstractMessage);

    }
}

