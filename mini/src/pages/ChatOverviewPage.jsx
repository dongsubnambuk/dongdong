import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ChatOverviewPage = () => {
  const [rooms, setRooms] = useState([]);
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();
  const creatorId = localStorage.getItem('UID');
  const [draggingRoomId, setDraggingRoomId] = useState(null);
  const [startX, setStartX] = useState(null);
  const [currentX, setCurrentX] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [wsInstance, setWsInstance] = useState(null);
  const [attemptCount, setAttemptCount] = useState(0);

  const maxAttempts = 5;

  useEffect(() => {
    fetchRooms();
    fetchUsers();
    connectWebSocket();

    return () => {
      if (wsInstance) {
        wsInstance.close();
      }
    };
  }, [attemptCount]);

  const connectWebSocket = () => {
    const ws = new WebSocket(`ws://chatex.p-e.kr:12000/ws/message?userId=${creatorId}`);

    ws.onopen = () => {
      console.log('WebSocket 연결 성공');
      setWsInstance(ws);
    };

    ws.onerror = (error) => {
      console.error('WebSocket 오류:', error);
      if (attemptCount < maxAttempts) {
        setTimeout(() => setAttemptCount(attemptCount + 1), 1000); // 1초 후 재시도
      }
    };

    ws.onmessage = (event) => {
      const newMessage = JSON.parse(event.data);
      console.log('새 메시지 수신:', newMessage);
      updateChatRoom(newMessage);
    };

    ws.onclose = () => {
      console.log('WebSocket 연결이 종료되었습니다.');
      if (attemptCount < maxAttempts) {
        setTimeout(() => setAttemptCount(attemptCount + 1), 1000); // 1초 후 재시도
      }
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
          unreadCount: room.unreadCount || 0,  // unreadCount 초기값 설정
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
    setRooms((prevRooms) => {
      return prevRooms.map((room) => {
        if (room.id === newMessage.roomId) {
          const isCurrentUser = newMessage.userId === creatorId;
          return {
            ...room,
            lastMessage: newMessage.messageContent,
            lastMessageSender: isCurrentUser ? '나' : '상대방',
            unreadCount: isCurrentUser ? room.unreadCount : room.unreadCount + 1,
          };
        }
        return room;
      });
    });
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

  const handleTouchStart = (e, roomId) => {
    setStartX(e.touches[0].clientX);
    setDraggingRoomId(roomId);
    setIsDragging(false);
  };

  const handleTouchMove = (e) => {
    if (startX !== null) {
      setCurrentX(e.touches[0].clientX);
      if (Math.abs(startX - e.touches[0].clientX) > 10) {
        setIsDragging(true);
      }
    }
  };

  const handleTouchEnd = () => {
    if (isDragging) {
      setIsDragging(false);
      if (currentX - startX < -50) {
        setCurrentX(startX - 70);
      } else {
        setCurrentX(startX);
      }
    }
    setStartX(null);
    setDraggingRoomId(null);
  };

  const handleDelete = (roomId) => {
    axios.delete(`http://chatex.p-e.kr/api/chat/${roomId}`)
      .then(() => fetchRooms())
      .catch((error) => {
        console.error('Failed to delete chat room:', error.response ? error.response.data : error.message);
        alert('Failed to delete chat room. Please try again later.');
      });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>채팅방 목록</h2>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {rooms.length > 0 ? (
          rooms.map((room) => {
            if (!room.id) return null;
            const translateX = draggingRoomId === room.id ? Math.min(0, currentX - startX) : 0;
            return (
              <li 
                key={`room-${room.id}`}
                style={{ 
                  padding: '10px', 
                  margin: '8px 0', 
                  backgroundColor: '#f0f0f0', 
                  borderRadius: '5px', 
                  cursor: 'pointer',
                  transition: 'transform 0.3s ease',
                  transform: `translateX(${translateX}px)`,
                  display: 'flex',
                  flexDirection: 'column', // 채팅방 이름과 메시지를 세로로 정렬
                  justifyContent: 'center',
                  position: 'relative'
                }}
                onTouchStart={(e) => handleTouchStart(e, room.id)}
                onTouchMove={handleTouchMove}
                onTouchEnd={handleTouchEnd}
                onClick={() => {
                  if (!isDragging) {
                    navigate(`/chat/${room.id}`);
                    setRooms(prevRooms => 
                      prevRooms.map(r => 
                        r.id === room.id ? { ...r, unreadCount: 0 } : r
                      )
                    );
                  }
                }}
              >
                <div style={{ fontWeight: 'bold', marginBottom: '5px' }}>{room.chatName}</div>
                <div style={{ color: '#888', fontSize: '14px' }}>
                  {room.lastMessageSender && (
                    <span>
                      {room.lastMessageSender}: 
                    </span>
                  )}
                  {room.lastMessage || '메시지 없음'}
                </div>
                {room.unreadCount > 0 && (
                  <div style={{ backgroundColor: 'red', color: 'white', borderRadius: '50%', padding: '5px', minWidth: '20px', textAlign: 'center', alignSelf: 'flex-end', marginTop: '5px' }}>
                    {room.unreadCount}
                  </div>
                )}
                {/* 삭제 버튼 */}
                <button 
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDelete(room.id);
                  }}
                  style={{ 
                    position: 'absolute',
                    right: '-80px',
                    padding: '5px 10px', 
                    backgroundColor: '#ff4d4d', 
                    color: 'white', 
                    border: 'none', 
                    borderRadius: '5px',
                    cursor: 'pointer',
                    opacity: translateX < -50 ? 1 : 0, 
                    transition: 'opacity 0.3s ease'
                  }}
                >
                  삭제
                </button>
              </li>
            );
          })
        ) : (
          <p>현재 표시할 채팅방이 없습니다.</p>
        )}
      </ul>

      <h2>유저 목록</h2>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {users.map((user) => (
          <li 
            key={`user-${user.uniqueId}`}
            style={{ 
              padding: '10px', 
              margin: '8px 0', 
              backgroundColor: '#f0f0f0', 
              borderRadius: '5px', 
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}
          >
            <span>{user.nickname || user.name}</span>
            <button 
              onClick={() => createChatRoom(user.uniqueId, user.nickname || user.name)}
              style={{ padding: '5px 10px', marginLeft: '10px', backgroundColor: '#007BFF', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
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
