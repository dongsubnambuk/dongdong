import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const ChatOverviewPage = () => {
  const navigate = useNavigate();
  const [chats, setChats] = useState([
    { id: 1, participants: ['User1', 'User2'], lastMessage: 'Hello!', unreadCount: 0 },
    { id: 2, participants: ['User3', 'User1'], lastMessage: 'How are you?', unreadCount: 0 },
  ]);

  useEffect(() => {
    // WebSocket 연결 설정
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      // 모든 채팅방에 대한 메시지 구독
      chats.forEach(chat => {
        stompClient.subscribe(`/topic/chat/${chat.id}`, (message) => {
          onMessageReceived(chat.id, JSON.parse(message.body));
        });
      });
    });

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [chats]);

  const onMessageReceived = (chatId, message) => {
    // 새 메시지를 받은 채팅방의 unreadCount 증가
    setChats(prevChats => prevChats.map(chat => 
      chat.id === chatId ? { ...chat, lastMessage: message.text, unreadCount: chat.unreadCount + 1 } : chat
    ));
  };

  const openChat = (chatId) => {
    // 채팅방을 열면 unreadCount를 0으로 초기화
    setChats(prevChats => prevChats.map(chat => 
      chat.id === chatId ? { ...chat, unreadCount: 0 } : chat
    ));
    navigate(`/chat/${chatId}`);
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>채팅</h2>
      <ul style={styles.chatList}>
        {chats.map((chat) => (
          <li key={chat.id} style={styles.chatItem} onClick={() => openChat(chat.id)}>
            <div style={styles.chatInfo}>
              <p style={styles.participants}>{chat.participants.join(', ')}</p>
              <p style={styles.lastMessage}>{chat.lastMessage}</p>
            </div>
            {chat.unreadCount > 0 && (
              <div style={styles.unreadCount}>{chat.unreadCount}</div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    backgroundColor: '#dfe7fd',  // 채팅방 배경 색상 적용
    height: '100vh',
    display: 'flex',
    flexDirection: 'column',
  },
  header: {
    fontSize: '24px',
    fontWeight: 'bold',
    marginBottom: '20px',
  },
  chatList: {
    listStyleType: 'none',
    padding: '0',
    margin: '0',
    flex: 1,
    overflowY: 'auto', // 채팅 목록 스크롤 가능하게 설정
  },
  chatItem: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '15px',
    backgroundColor: '#ffffff', // 각 채팅 아이템의 배경 색상
    borderRadius: '8px',
    marginBottom: '10px',
    cursor: 'pointer',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)', // 약간의 그림자 추가
  },
  chatInfo: {
    marginLeft: '10px',
  },
  participants: {
    fontSize: '16px',
    fontWeight: 'bold',
    margin: '0',
  },
  lastMessage: {
    fontSize: '14px',
    color: '#888',
    margin: '0',
  },
  unreadCount: {
    backgroundColor: 'red',
    color: 'white',
    borderRadius: '50%',
    padding: '5px 10px',
    fontSize: '12px',
    fontWeight: 'bold',
  },
};

export default ChatOverviewPage;
