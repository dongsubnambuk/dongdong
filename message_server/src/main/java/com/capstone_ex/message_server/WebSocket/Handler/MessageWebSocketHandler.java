package com.capstone_ex.message_server.WebSocket.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MessageWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = new HashSet<>();

    public MessageWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트가 연결되었을 때 세션을 저장
        sessions.add(session);
        System.out.println("새로운 WebSocket 연결: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 받은 메시지를 처리
        System.out.println("메시지 수신: " + message.getPayload());
        broadcastMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 클라이언트가 연결을 닫았을 때 세션을 제거
        sessions.remove(session);
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

    private void broadcastMessage(WebSocketSession senderSession, TextMessage message) throws IOException {
        // 모든 연결된 세션에 메시지를 전송 (브로드캐스트)
        for (WebSocketSession session : sessions) {
            if (session.isOpen() && !session.getId().equals(senderSession.getId())) {
                session.sendMessage(message);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // WebSocket 전송 중 오류가 발생했을 때 처리
        System.out.println("WebSocket 오류: " + exception.getMessage());
    }
}
