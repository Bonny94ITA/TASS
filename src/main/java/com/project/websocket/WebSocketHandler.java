package com.project.websocket;

import com.project.controller.BookingController;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

public class WebSocketHandler extends AbstractWebSocketHandler {
    private static String KEY = "j4d6854439t1g854";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)
            throws IOException, JSONException {
        System.out.println(new String(message.getPayload().toString()));
        JSONObject obj = new JSONObject(new String(message.getPayload().toString()));
        System.out.println("ddasd");

        if (((String)obj.get("key")).equals(KEY)) {
            BookingController.sharedModel.getSocketClients().put((Integer)obj.get("id"), session);
        }
    }
}
