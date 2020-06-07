package com.project.websocket;

import com.project.controller.BookingController;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebSocketHandler extends AbstractWebSocketHandler {
    private static String KEY = "j4d6854439t1g854";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        String payload = message.getPayload();
        Integer id = Integer.parseInt(payload.substring(4, payload.indexOf(",")));
        String key = payload.substring(payload.indexOf("key:") + 5, payload.length());
        System.out.println(key);
        System.out.println(id);

        if (key.equals(KEY)) {
            BookingController.sharedModel.getSocketClients().put(id, session);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)
            throws IOException, JSONException { }
}
