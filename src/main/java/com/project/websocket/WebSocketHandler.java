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
import java.util.*;

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
        String key = payload.substring(payload.indexOf("key: ") + 5, payload.length());

        if (key.equals(KEY)) {
            Map<Integer, List<WebSocketSession>> socketClients = BookingController.sharedModel.getSocketClients();
            if (socketClients.get(id) != null) {
                socketClients.get(id).removeIf(s -> s.getRemoteAddress().getAddress().equals(session.getRemoteAddress().getAddress()));
                socketClients.get(id).add(session);
            } else {
                List<WebSocketSession> socketSessions = new LinkedList<>();
                socketSessions.add(session);
                socketClients.put(id, socketSessions);
            }
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)
            throws IOException, JSONException { }
}
