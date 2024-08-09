import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const ChatPage = () => {
  const { id } = useParams(); // 채팅방 ID
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const messageEndRef = useRef(null);
  const stompClient = useRef(null);

  useEffect(() => {
    // WebSocket 연결 설정
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect({}, () => {
      // 채팅방에 구독
      stompClient.current.subscribe(`/topic/chat/${id}`, (message) => {
        onMessageReceived(JSON.parse(message.body));
      });
    });

    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, [id]);

  const onMessageReceived = (message) => {
    setMessages((prevMessages) => [...prevMessages, message]);
  };

  const handleSendMessage = () => {
    if (newMessage.trim() !== '') {
      const chatMessage = {
        sender: 'You', // 실제로는 사용자 정보에서 닉네임을 가져오는 것이 좋습니다.
        text: newMessage,
        roomId: id
      };

      // 전송된 메시지를 로컬 상태에 즉시 추가
      setMessages((prevMessages) => [...prevMessages, chatMessage]);

      // WebSocket을 통해 서버로 메시지 전송
      stompClient.current.send(`/app/chat.sendMessage/${id}`, {}, JSON.stringify(chatMessage));
      setNewMessage('');
    }
  };

  useEffect(() => {
    // 새로운 메시지가 추가될 때 스크롤을 최신 메시지로 이동
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div style={styles.container}>
      <div style={styles.chatContainer}>
        {messages.map((msg, index) => (
          <div
            key={index}
            style={{
              ...styles.message,
              alignSelf: msg.sender === 'You' ? 'flex-end' : 'flex-start',
              backgroundColor: msg.sender === 'You' ? '#dcf8c6' : '#ffffff',
            }}
          >
            {msg.text}
          </div>
        ))}
        <div ref={messageEndRef} />
      </div>
      <div style={styles.inputContainer}>
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          style={styles.input}
          placeholder="메시지 입력..."
        />
        <button onClick={handleSendMessage} style={styles.sendButton}>
          전송
        </button>
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100vh',
    // backgroundColor: '#e1e1e1',
  },
  chatContainer: {
    flex: 1,
    padding: '20px',
    backgroundColor: '#dfe7fd',  // 채팅방 배경 색상 적용
    overflowY: 'scroll',
    display: 'flex',
    flexDirection: 'column',
  },
  message: {
    padding: '10px 15px',
    borderRadius: '20px',
    marginBottom: '10px',
    maxWidth: '60%',
    wordWrap: 'break-word',
  },
  inputContainer: {
    display: 'flex',
    padding: '10px',
    backgroundColor: '#ffffff',
    borderTop: '1px solid #ddd',
  },
  input: {
    flex: 1,
    padding: '10px',
    borderRadius: '20px',
    border: '1px solid #ddd',
    marginRight: '10px',
  },
  sendButton: {
    padding: '10px 20px',
    borderRadius: '20px',
    backgroundColor: '#61dafb',
    border: 'none',
    cursor: 'pointer',
  },
};

export default ChatPage;
