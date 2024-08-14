import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const ChatRoomManagementPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [chatRoom, setChatRoom] = useState(null);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetch(`http://chatex.p-e.kr:11000/api/chat/${id}`)
      .then(response => response.json())
      .then(data => setChatRoom(data))
      .catch(error => console.error('채팅방 정보를 불러오는 중 오류 발생:', error));

    fetch('http://chatex.p-e.kr:11000/api/chat/user/all')
      .then(response => response.json())
      .then(data => setUsers(data))
      .catch(error => console.error('사용자 목록을 불러오는 중 오류 발생:', error));
  }, [id]);

  const handleDeleteChatRoom = () => {
    fetch(`http://chatex.p-e.kr:11000/api/chat/${id}`, {
      method: 'DELETE',
    })
    .then(() => {
      alert('채팅방이 삭제되었습니다.');
      navigate('/chat-overview');
    })
    .catch(error => console.error('채팅방 삭제 중 오류 발생:', error));
  };

  const handleAddUser = (userId) => {
    fetch(`http://chatex.p-e.kr:11000/api/chat/${id}/addUser/${userId}`, {
      method: 'POST',
    })
    .then(() => {
      alert('사용자가 채팅방에 추가되었습니다.');
      setChatRoom(prev => ({
        ...prev,
        participants: [...prev.participants, userId],
      }));
    })
    .catch(error => console.error('사용자 추가 중 오류 발생:', error));
  };

  const handleRemoveUser = (userId) => {
    fetch(`http://chatex.p-e.kr:11000/api/chat/${id}/removeUser/${userId}`, {
      method: 'DELETE',
    })
    .then(() => {
      alert('사용자가 채팅방에서 제거되었습니다.');
      setChatRoom(prev => ({
        ...prev,
        participants: prev.participants.filter(uid => uid !== userId),
      }));
    })
    .catch(error => console.error('사용자 제거 중 오류 발생:', error));
  };

  if (!chatRoom) {
    return <p>채팅방 정보를 불러오는 중...</p>;
  }

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>{chatRoom.chatName} 관리</h2>
      <button onClick={handleDeleteChatRoom} style={styles.deleteButton}>채팅방 삭제</button>

      <h3>참여자 관리</h3>
      <ul style={styles.participantList}>
        {chatRoom.participants.map((participant, index) => (
          <li key={`${participant}-${index}`} style={styles.participantItem}>
            <p>{participant.nickname}</p>
            <button onClick={() => handleRemoveUser(participant.uniqueId)} style={styles.removeButton}>
              제거
            </button>
          </li>
        ))}
      </ul>

      <h3>사용자 추가</h3>
      <ul style={styles.userList}>
        {users.filter(user => !chatRoom.participants.includes(user.uniqueId)).map((user, index) => (
          <li key={`${user.uniqueId}-${index}`} style={styles.userItem}>
            <p>{user.nickname}</p>
            <button onClick={() => handleAddUser(user.uniqueId)} style={styles.addButton}>
              추가
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
  },
  header: {
    fontSize: '24px',
    fontWeight: 'bold',
    marginBottom: '20px',
  },
  deleteButton: {
    padding: '10px 20px',
    backgroundColor: 'red',
    color: 'white',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
    marginBottom: '20px',
  },
  participantList: {
    listStyleType: 'none',
    padding: '0',
    marginBottom: '20px',
  },
  participantItem: {
    padding: '10px',
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    marginBottom: '10px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  removeButton: {
    padding: '5px 10px',
    backgroundColor: 'red',
    color: 'white',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
  },
  userList: {
    listStyleType: 'none',
    padding: '0',
    marginBottom: '20px',
  },
  userItem: {
    padding: '10px',
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    marginBottom: '10px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  addButton: {
    padding: '5px 10px',
    backgroundColor: '#61dafb',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
  },
};

export default ChatRoomManagementPage;
