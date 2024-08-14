import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const UserProfilePage = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem("UID");
  const nickname = localStorage.getItem("nickname");
  const email = localStorage.getItem("email");

  useEffect(() => {
    fetch(`http://chatex.p-e.kr:11000/api/chat/user/${id}`)
      .then(response => response.json())
      .then(data => setUser(data))
      .catch(error => console.error('사용자 정보를 불러오는 중 오류 발생:', error));
  }, [id]);

  const startChatWithUser = (user) => {
    const loggedInUser = {
      userId: userId,
      nickname: nickname,
      email: email
    };

    const newChat = {
      chatName: `${loggedInUser.nickname} & ${user.nickname}`,
      description: `Chat between ${loggedInUser.nickname} and ${user.nickname}`,
      creatorInfo: loggedInUser
    };

    fetch('http://chatex.p-e.kr:11000/api/chat/create-chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newChat)
    })
    .then(response => response.json())
    .then(data => {
      navigate(`/chat/${data.chatRoomId}`);
    })
    .catch(error => console.error('채팅방 생성 중 오류 발생:', error));
  };

  if (!user) {
    return <p>사용자 정보를 불러오는 중...</p>;
  }

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>{user.nickname}의 프로필</h2>
      <p><strong>Email:</strong> {user.email}</p>
      <p><strong>가입일:</strong> {user.createdAt}</p>
      <button style={styles.chatButton} onClick={() => startChatWithUser(user)}>
        채팅하기
      </button>
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
  chatButton: {
    padding: '10px 20px',
    borderRadius: '8px',
    backgroundColor: '#61dafb',
    border: 'none',
    cursor: 'pointer',
  },
};

export default UserProfilePage;
