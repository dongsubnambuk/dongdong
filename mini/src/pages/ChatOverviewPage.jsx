import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ChatOverviewPage = () => {
  const [rooms, setRooms] = useState([]);
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();
  const creatorId = localStorage.getItem('UID');
  const [wsInstance, setWsInstance] = useState(null);

  useEffect(() => {
    fetchRooms();
    fetchUsers();
    connectWebSocket();

    return () => {
      if (wsInstance) {
        wsInstance.close();
      }
    };
  }, []);

  const connectWebSocket = () => {
    const ws = new WebSocket(`ws://chatex.p-e.kr:12000/ws/message?userId=${creatorId}`);

    ws.onopen = () => {
      console.log('WebSocket 연결 성공');
      setWsInstance(ws);
    };

    ws.onmessage = (event) => {
      const newMessage = JSON.parse(event.data);
      
      // 새로운 메시지인 경우
      if (newMessage.type === 'NEW_MESSAGE') {
        updateChatRoom(newMessage);
      }
      
      // 새로운 채팅방이 생성된 경우
      if (newMessage.type === 'NEW_CHAT_ROOM') {
        addNewChatRoom(newMessage.chatRoom);
      }
    };

    ws.onclose = () => {
      console.log('WebSocket 연결 종료');
    };
  };

  const fetchRooms = () => {
    axios.get('http://chatex.p-e.kr/api/chat/all-chat')
      .then(response => {
        const roomsData = response.data.map(room => ({
          id: room.chatRoomId,
          chatName: room.chatName,
          creatorId: room.creatorId,
          lastMessage: room.lastMessage,
          lastMessageSender: room.lastMessageSender,
          unreadCount: room.unreadCount || 0,
        }));
        setRooms(roomsData);
      })
      .catch(error => {
        console.error('채팅방 목록을 가져오는데 실패했습니다.', error);
      });
  };

  const fetchUsers = () => {
    axios.get('http://chatex.p-e.kr/api/chat/user/all')
      .then(response => {
        setUsers(response.data);
      })
      .catch(error => {
        console.error('유저 목록을 가져오는데 실패했습니다.', error);
      });
  };

  const updateChatRoom = (newMessage) => {
    setRooms(prevRooms => {
      return prevRooms.map(room => {
        if (room.id === newMessage.chatRoomId) {
          return {
            ...room,
            lastMessage: newMessage.messageContent,
            lastMessageSender: newMessage.userId === creatorId ? '나' : '상대방',
            unreadCount: room.unreadCount + 1, // 메시지가 올 때마다 읽지 않은 메시지 수 증가
          };
        }
        return room;
      });
    });
  };

  const addNewChatRoom = (newRoom) => {
    setRooms(prevRooms => [...prevRooms, {
      id: newRoom.chatRoomId,
      chatName: newRoom.chatName,
      creatorId: newRoom.creatorId,
      lastMessage: '',
      lastMessageSender: '',
      unreadCount: 0
    }]);
  };

  const handleRoomClick = (roomId) => {
    navigate(`/chat/${roomId}`);
    setRooms(prevRooms =>
      prevRooms.map(room =>
        room.id === roomId ? { ...room, unreadCount: 0 } : room // 방 클릭 시 읽지 않은 메시지 초기화
      )
    );
  };

  const createChatRoom = (selectedUserUniqueId, selectedUserNickname) => {
    axios.post('http://chatex.p-e.kr/api/chat/create-chat', {
        chatName: selectedUserNickname,
        creatorId: creatorId,
        selectedId: selectedUserUniqueId
      })
      .then(response => {
        const newRoom = {
          id: response.data.chatRoomId,
          chatName: selectedUserNickname,
          lastMessage: '',
          lastMessageSender: '',
          unreadCount: 0
        };
        setRooms(prevRooms => [...prevRooms, newRoom]);
        navigate(`/chat/${newRoom.id}`);
        fetchRooms();
      })
      .catch(error => {
        console.error('채팅방 생성에 실패했습니다.', error);
      });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>채팅방 목록</h2>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {rooms.length > 0 ? (
          rooms.map((room) => (
            <li
              key={room.id}
              style={{
                padding: '10px',
                margin: '8px 0',
                backgroundColor: '#f0f0f0',
                borderRadius: '5px',
                cursor: 'pointer',
                position: 'relative',
              }}
              onClick={() => handleRoomClick(room.id)}
            >
              <div style={{ fontWeight: 'bold' }}>{room.chatName}</div>
              <div style={{ color: '#888', fontSize: '14px' }}>
                {room.lastMessageSender && `${room.lastMessageSender}: `}
                {room.lastMessage || '메시지 없음'}
              </div>
              {room.unreadCount > 0 && (
                <div
                  style={{
                    backgroundColor: 'red',
                    color: 'white',
                    borderRadius: '50%',
                    padding: '5px',
                    minWidth: '20px',
                    textAlign: 'center',
                    position: 'absolute',
                    top: '10px',
                    right: '10px',
                  }}
                >
                  {room.unreadCount}
                </div>
              )}
            </li>
          ))
        ) : (
          <p>현재 표시할 채팅방이 없습니다.</p>
        )}
      </ul>

      <h2>유저 목록</h2>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {users.map((user) => (
          <li 
            key={user.uniqueId}
            style={{ 
              padding: '10px', 
              margin: '8px 0', 
              backgroundColor: '#f0f0f0', 
              borderRadius: '5px', 
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              cursor: 'pointer',
            }}
            onClick={() => createChatRoom(user.uniqueId, user.nickname || user.name)}
          >
            <span>{user.nickname || user.name}</span>
            <button 
              style={{ 
                padding: '5px 10px', 
                backgroundColor: '#007BFF', 
                color: 'white', 
                border: 'none', 
                borderRadius: '5px', 
                cursor: 'pointer',
              }}
            >
              채팅하기
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatOverviewPage;
