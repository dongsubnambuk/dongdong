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
  const [isSending, setIsSending] = useState(false);
  const messagesEndRef = useRef(null);
  const [inviteUserId, setInviteUserId] = useState(''); 
  const [currentTab, setCurrentTab] = useState('chat'); 
  const [userList, setUserList] = useState([]); 

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
        updatedMessages.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
        return updatedMessages;
      });

      updateLastMessage(chatRoomId, newMessage.messageContent, newMessage.userId);
      scrollToBottom();
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

  useEffect(() => {
    if (currentTab === 'users') {
      fetchUserList();
    }
  }, [currentTab, chatRoomId]);

  const updateMessage = async (chatRoomId) => {
    try {
      const response = await axios.get(`http://chatex.p-e.kr:12000/api/message/sender/${chatRoomId}/read-all`);
      if (Array.isArray(response.data)) {
        const sortedMessages = response.data.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
        setMessages(sortedMessages);
      }
      scrollToBottom();
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
      setIsSending(true);
      const messageData = {
        userId: localUserId,
        messageContent: message,
        chatRoomId: chatRoomId,
      };

      try {
        const response = await axios.post(`http://chatex.p-e.kr:12000/api/message/receiver`, messageData);

        if (ws && ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(response.data));
          const newMessage = { ...response.data, sendTime: new Date().toISOString() };
          setMessages(prevMessages => {
            const updatedMessages = [...prevMessages, newMessage];
            return updatedMessages.sort((a, b) => new Date(a.sendTime) - new Date(b.sendTime));
          });
          setMessage('');
          scrollToBottom();
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
        setIsSending(false);
      }
    }
  };

  const scrollToBottom = () => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const inviteUser = async () => {
    if (inviteUserId.trim() !== '') {
      try {
        await axios.post(`http://chatex.p-e.kr/api/chat/${chatRoomId}/addUser/${inviteUserId}`);
        alert('사용자를 초대했습니다!');
        setInviteUserId('');
        fetchUserList(); // 유저 리스트를 업데이트하여 초대된 사용자가 보이도록 함
      } catch (error) {
        console.error('Failed to invite user:', error);
        alert('사용자 초대에 실패했습니다.');
      }
    } else {
      alert('초대할 사용자의 ID를 입력해주세요.');
    }
  };

  const removeUser = async (uniqueId) => {
    try {
      await axios.post(`http://chatex.p-e.kr/api/chat/${chatRoomId}/remove-user/${uniqueId}`);
      alert('사용자가 제거되었습니다.');
      fetchUserList(); // 유저 리스트를 업데이트하여 제거된 사용자가 보이지 않도록 함
    } catch (error) {
      console.error('Failed to remove user:', error);
      alert('사용자 제거에 실패했습니다.');
    }
  };

  const fetchUserList = async () => {
    try {
      const response = await axios.get(`http://chatex.p-e.kr/api/chat/${chatRoomId}/users`);
      setUserList(response.data);
    } catch (error) {
      console.error('Failed to fetch user list:', error);
      alert('유저 목록을 불러오는 데 실패했습니다.');
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto', backgroundColor: '#ffffff', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)', height: '80vh' }}>
      <h2 style={{ marginBottom: '20px', textAlign: 'center', fontSize: '24px', fontWeight: 'bold', color: '#333' }}>채팅방: {chatRoomId}</h2>

      {/* 탭 메뉴 */}
      <div style={{ display: 'flex', justifyContent: 'space-around', marginBottom: '20px' }}>
        <button onClick={() => setCurrentTab('chat')} style={{ padding: '10px 20px', backgroundColor: currentTab === 'chat' ? '#007BFF' : '#ccc', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>채팅</button>
        <button onClick={() => setCurrentTab('users')} style={{ padding: '10px 20px', backgroundColor: currentTab === 'users' ? '#007BFF' : '#ccc', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>유저 목록</button>
      </div>

      {currentTab === 'chat' ? (
        <>
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
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: '20px' }}>
            <input type="text" value={message} onChange={e => setMessage(e.target.value)} onKeyPress={e => e.key === 'Enter' ? sendMessage() : null} style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #ccc', marginRight: '10px' }} placeholder="메시지를 입력하세요..." />
            <button onClick={sendMessage} disabled={isSending} style={{ padding: '10px 20px', backgroundColor: isSending ? '#ccc' : '#007BFF', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>전송</button>
          </div>
        </>
      ) : (
        <div style={{ backgroundColor: '#f9f9f9', padding: '15px', borderRadius: '8px', maxHeight: 'calc(100% - 160px)', overflowY: 'auto' }}>
          {Array.isArray(userList) && userList.map((user, index) => (
            <div key={index} style={{ marginBottom: '10px', padding: '10px', borderRadius: '8px', backgroundColor: '#ffffff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <b style={{ fontWeight: 'bold', color: '#007BFF' }}>{user.nickname}</b>
                <span> ({user.uniqueId})</span>
              </div>
              <button onClick={() => removeUser(user.uniqueId)} style={{ padding: '5px 10px', backgroundColor: '#FF6347', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
                제거
              </button>
            </div>
          ))}

          {/* 사용자 초대 입력 필드 및 버튼 */}
          <div style={{ display: 'flex', alignItems: 'center', marginTop: '20px' }}>
            <input type="text" value={inviteUserId} onChange={e => setInviteUserId(e.target.value)} style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #ccc', marginRight: '10px' }} placeholder="초대할 사용자 ID를 입력하세요..." />
            <button onClick={inviteUser} style={{ padding: '10px 20px', backgroundColor: '#28a745', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>초대</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatPage;
