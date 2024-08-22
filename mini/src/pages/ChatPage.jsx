import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const ChatPage = ({ updateLastMessage = () => {} }) => {
  const { id: chatRoomId } = useParams();
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [ws, setWs] = useState(null);
  const [nicknames, setNicknames] = useState({});
  const localUserId = localStorage.getItem('UID');

  useEffect(() => {
    const uniqueId = localUserId;
    const wsInstance = new WebSocket(`ws://chatex.p-e.kr:12000/ws/message?userId=${uniqueId}&chatRoomId=${chatRoomId}`);

    wsInstance.onopen = () => {
      console.log('WebSocket 연결 성공');
      setWs(wsInstance);
      updateMessage(chatRoomId);
    };

    wsInstance.onmessage = async (event) => {
      const newMessage = JSON.parse(event.data);
      const { userId } = newMessage;

      if (!nicknames[userId]) {
        try {
          const response = await getNicknameByUniqueId(userId);
          setNicknames(prev => ({ ...prev, [userId]: response.data.nickname }));
        } catch (error) {
          console.error('Failed to fetch nickname:', error);
        }
      }

      setMessages(prevMessages => {
        const updatedMessages = [...prevMessages, newMessage];
        return updatedMessages.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
      });

      updateLastMessage(chatRoomId, newMessage.messageContent, newMessage.userId);
    };

    wsInstance.onclose = () => {
      console.log('WebSocket 연결 종료');
    };

    return () => {
      if (wsInstance) {
        wsInstance.close();
      }
    };
  }, [chatRoomId]);

  const updateMessage = async (chatRoomId) => {
    try {
      const response = await axios.get(`http://chatex.p-e.kr/api/message/sender/${chatRoomId}/read-all`);
      setMessages(response.data);
    } catch (error) {
      console.error('Failed to update messages:', error);
    }
  };

  const getNicknameByUniqueId = async (uniqueId) => {
    try {
      return await axios.get(`http://chatex.p-e.kr/api/chat/user/${uniqueId}`);
    } catch (error) {
      console.error('Failed to fetch nickname:', error);
      throw error;
    }
  };

  const sendMessage = async () => {
    if (message.trim() !== '') {
      const messageData = {
        userId: localUserId,
        messageContent: message,
        chatRoomId: chatRoomId,
      };

      try {
        await axios.post(`http://chatex.p-e.kr/api/message/receiver`, messageData);

        if (ws && ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(messageData));
          const newMessage = { ...messageData, sendTime: new Date().toISOString() };
          setMessages([...messages, newMessage]);
          setMessage('');

          updateLastMessage(chatRoomId, newMessage.messageContent, localUserId);
        } else {
          console.error('WebSocket is not connected');
        }
      } catch (error) {
        console.error('Failed to send message:', error.response ? error.response.data : error.message);

        if (error.response && error.response.status === 500) {
          console.error('서버에서 반환된 메시지:', error.response.data);
          alert('메시지를 전송할 수 없습니다. 해당 사용자가 채팅방에 포함되어 있는지 확인해주세요.');
        }
      }
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto', backgroundColor: '#ffffff', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }}>
      {/* 문제의 요소를 삭제하거나 주석 처리 */}
      <h2 style={{ marginBottom: '20px', textAlign: 'center', fontSize: '24px', fontWeight: 'bold', color: '#333' }}>채팅방: {chatRoomId}</h2>
      <div style={{ marginBottom: '20px', backgroundColor: '#f9f9f9', padding: '15px', borderRadius: '8px', maxHeight: '400px', overflowY: 'auto', display: 'flex', flexDirection: 'column' }}>
        {Array.isArray(messages) && messages.map((msg, index) => (
          <div key={index} style={{ marginBottom: '10px', padding: '10px', borderRadius: '8px', maxWidth: '60%', wordWrap: 'break-word', alignSelf: msg.userId === localUserId ? 'flex-end' : 'flex-start', backgroundColor: msg.userId === localUserId ? '#DCF8C6' : '#FFFFFF' }}>
            <b style={{ fontWeight: 'bold', color: '#007BFF' }}>{nicknames[msg.userId] || msg.userId}</b>: {msg.messageContent}
          </div>
        ))}
      </div>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <input type="text" value={message} onChange={e => setMessage(e.target.value)} onKeyPress={e => e.key === 'Enter' ? sendMessage() : null} style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #ccc', marginRight: '10px' }} placeholder="메시지를 입력하세요..." />
        <button onClick={sendMessage} style={{ padding: '10px 20px', backgroundColor: '#007BFF', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>전송</button>
      </div>
    </div>
  );
};

export default ChatPage;
