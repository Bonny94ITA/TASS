package com.project.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SharedModel {
    private Map<Integer, WebSocketSession> socketClients = new HashMap<>();

    public SharedModel() { }

    public Map<Integer, WebSocketSession> getSocketClients() {
        return socketClients;
    }

    public void setSocketClients(Map<Integer, WebSocketSession> socketClients) {
        this.socketClients = socketClients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedModel that = (SharedModel) o;
        return Objects.equals(socketClients, that.socketClients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketClients);
    }
}
