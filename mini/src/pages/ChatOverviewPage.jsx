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

      if (newMessage.chatRoomId) {
        if (newMessage.userId !== creatorId) {
          // 상대방이 메시지를 보낸 경우에만 unreadCount 증가
          updateChatRoom(newMessage);
        }
        addNewChatRoom(newMessage.chatRoom);
      } else {
        console.error('Received message with undefined chatRoomId:', newMessage);
      }
    };

    ws.onclose = () => {
      console.log('WebSocket 연결 종료');
    };
  };

  const fetchRooms = () => {
    axios.get(`http://chatex.p-e.kr/api/chat/${creatorId}/chat-rooms`)
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
    axios.get('http://chatex.p-e.kr/api/auth/users')
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
            unreadCount: newMessage.userId !== creatorId ? room.unreadCount + 1 : room.unreadCount, // 메시지를 보낸 사용자가 본인이 아닐 경우에만 unreadCount 증가
          };
        }
        return room;
      });
    });
  };

  const addNewChatRoom = (newRoom) => {
    if (!newRoom || !newRoom.chatRoomId) {
      console.error('Cannot add a chat room without chatRoomId:', newRoom);
      return;
    }

    setRooms(prevRooms => [...prevRooms, {
      id: newRoom.chatRoomId,
      chatName: newRoom.chatName,
      creatorId: newRoom.creatorId,
      lastMessage: '',
      lastMessageSender: '',
      unreadCount: 0
    }]);
  };

  const deleteChatRoom = (chatRoomId) => {
    axios.delete(`http://chatex.p-e.kr/api/chat/${chatRoomId}`)
      .then(() => {
        setRooms(prevRooms => prevRooms.filter(room => room.id !== chatRoomId));
        alert('채팅방이 삭제되었습니다.');
      })
      .catch(error => {
        console.error('채팅방 삭제에 실패했습니다.', error);
        alert('채팅방 삭제에 실패했습니다.');
      });
  };

  const handleRoomClick = (roomId) => {
    if (!roomId) {
      console.error('Cannot navigate to chat room without roomId');
      return;
    }

    navigate(`/chat/${roomId}`);
    setRooms(prevRooms =>
      prevRooms.map(room =>
        room.id === roomId ? { ...room, unreadCount: 0 } : room
      )
    );
  };

  const createChatRoom = (selectedUserUniqueId, selectedUserNickname) => {
    const existingRoom = rooms.find(room => room.chatName === selectedUserNickname);
    
    if (existingRoom) {
      alert('이미 채팅방이 존재합니다.');
      navigate(`/chat/${existingRoom.id}`);
      return;
    }

    axios.post('http://chatex.p-e.kr/api/chat/create-chat', {
        chatName: selectedUserNickname,
        creatorId: creatorId,
        selectedId: selectedUserUniqueId
      })
      .then(response => {
        const newRoomId = response.data.id;

        if (!newRoomId) {
          alert('채팅방 ID를 생성하는 데 실패했습니다.');
          return;
        }

        navigate(`/chat/${newRoomId}`);
        if (wsInstance) {
          wsInstance.close();
        }
        const newWs = new WebSocket(`ws://chatex.p-e.kr:12000/ws/message?userId=${creatorId}&chatRoomId=${newRoomId}`);
        setWsInstance(newWs);

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
                    right: '60px',  // 숫자와 삭제 버튼 간의 거리 조정
                  }}
                >
                  {room.unreadCount}
                </div>
              )}
              <button
                style={{
                  position: 'absolute',
                  top: '10px',
                  right: '10px',
                  backgroundColor: '#FF6347',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  padding: '5px 10px',
                  cursor: 'pointer',
                }}
                onClick={(e) => {
                  e.stopPropagation(); // 부모 요소로의 이벤트 전파 방지
                  if (window.confirm('정말로 이 채팅방을 삭제하시겠습니까?')) {
                    deleteChatRoom(room.id);
                  }
                }}
              >
                삭제
              </button>
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
