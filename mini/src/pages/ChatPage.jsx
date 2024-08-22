import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const ChatPage = ({ updateLastMessage = () => {} }) => {
  const { id: chatRoomId } = useParams();
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [ws, setWs] = useState(null);
  const [nicknames, setNicknames] = useState({});
  const localUserId = localStorage.getItem('UID');
  const [isSending, setIsSending] = useState(false); // 메시지 전송 중인지 확인
  const messagesEndRef = useRef(null); // 메시지 끝에 대한 참조

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
        // 메시지를 시간순서대로 정렬 (오래된 것이 위에 오도록)
        updatedMessages.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
        return updatedMessages;
      });

      updateLastMessage(chatRoomId, newMessage.messageContent, newMessage.userId);
      scrollToBottom(); // 새로운 메시지가 추가되면 스크롤을 맨 아래로 이동
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
      
      console.log('Received response:', response.data); // 서버 응답 전체를 확인
  
      // response.data가 배열인지 확인합니다.
      if (Array.isArray(response.data)) {
        const sortedMessages = response.data.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
        setMessages(sortedMessages);
      }
      scrollToBottom(); // 메시지를 불러오면 스크롤을 맨 아래로 이동
    } catch (error) {
      console.error('Failed to update messages:', error);
      alert('메시지를 업데이트하는 데 실패했습니다. 서버 연결을 확인하세요.');
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
    if (message.trim() !== '' && !isSending) {
      setIsSending(true); // 전송 시작
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
          setMessages(prevMessages => {
            const updatedMessages = [...prevMessages, newMessage];
            // 메시지를 시간순서대로 정렬 (오래된 것이 위에 오도록)
            return updatedMessages.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
          });
          setMessage('');
          scrollToBottom(); // 메시지가 추가되면 스크롤을 맨 아래로 이동
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
      } finally {
        setIsSending(false); // 전송 종료
      }
    }
  };

  const scrollToBottom = () => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto', backgroundColor: '#ffffff', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)', height: '80vh' }}> {/* 고정된 채팅방 크기 */}
      <h2 style={{ marginBottom: '20px', textAlign: 'center', fontSize: '24px', fontWeight: 'bold', color: '#333' }}>채팅방: {chatRoomId}</h2>
      <div style={{ flexGrow: 1, marginBottom: '20px', backgroundColor: '#f9f9f9', padding: '15px', borderRadius: '8px', maxHeight: 'calc(100% - 160px)', overflowY: 'auto', display: 'flex', flexDirection: 'column', height: '100%' }}>
        {Array.isArray(messages) && messages.map((msg, index) => (
          <div key={index} style={{ marginBottom: '10px', padding: '10px', borderRadius: '8px', maxWidth: '60%', wordWrap: 'break-word', alignSelf: msg.userId === localUserId ? 'flex-end' : 'flex-start', backgroundColor: msg.userId === localUserId ? '#DCF8C6' : '#FFFFFF' }}>
            {nicknames[msg.userId] && (
              <b style={{ fontWeight: 'bold', color: '#007BFF' }}>{nicknames[msg.userId]}</b>
            )}
            {msg.messageContent && <span>: {msg.messageContent}</span>}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <input type="text" value={message} onChange={e => setMessage(e.target.value)} onKeyPress={e => e.key === 'Enter' ? sendMessage() : null} style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #ccc', marginRight: '10px' }} placeholder="메시지를 입력하세요..." />
        <button onClick={sendMessage} disabled={isSending} style={{ padding: '10px 20px', backgroundColor: isSending ? '#ccc' : '#007BFF', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>전송</button>
      </div>
    </div>
  );
};

export default ChatPage;
