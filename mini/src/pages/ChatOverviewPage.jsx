import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ChatOverviewPage = () => {
  const navigate = useNavigate();
  const [chats, setChats] = useState([]);
  const [users, setUsers] = useState([]);
  const [newChatName, setNewChatName] = useState('');
  const [newChatDescription, setNewChatDescription] = useState('');
  const [activeTab, setActiveTab] = useState('chats'); // 탭 상태 관리

  useEffect(() => {
    // 모든 채팅방 조회 API 호출
    fetch('/api/chatroom/all')
      .then(response => response.json())
      .then(data => setChats(data))
      .catch(error => console.error('채팅방 목록을 불러오는 중 오류 발생:', error));

    // 사용자 목록 조회 API 호출
    fetch('http://chatex.p-e.kr:10000/api/users')
      .then(response => response.json())
      .then(data => setUsers(data))
      .catch(error => console.error('사용자 목록을 불러오는 중 오류 발생:', error));
  }, []);

  const handleCreateChat = () => {
    const loggedInUser = {
      userId: '로그인된 유저의 해시코드',  // 실제 로그인된 유저 정보를 여기에 적용해야 합니다.
      nickname: '로그인된 유저의 닉네임',
      email: '로그인된 유저의 이메일'
    };

    const newChat = {
      chatName: newChatName,
      description: newChatDescription,
      creatorInfo: loggedInUser
    };

    fetch('http://chatex.p-e.kr:11000/api/chatroom', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newChat)
    })
    .then(response => response.json())
    .then(data => {
      setChats(prevChats => [...prevChats, data]);
      setNewChatName('');
      setNewChatDescription('');
    })
    .catch(error => console.error('채팅방 생성 중 오류 발생:', error));
  };

  const openChat = (chatId) => {
    // 채팅방을 열면 unreadCount를 0으로 초기화
    setChats(prevChats => prevChats.map(chat => 
      chat.id === chatId ? { ...chat, unreadCount: 0 } : chat
    ));
    navigate(`/chat/${chatId}`);
  };

  const startChatWithUser = (user) => {
    const loggedInUser = {
      userId: '로그인된 유저의 해시코드',  // 실제 로그인된 유저 정보를 여기에 적용해야 합니다.
      nickname: '로그인된 유저의 닉네임',
      email: '로그인된 유저의 이메일'
    };

    const newChat = {
      chatName: `${loggedInUser.nickname} & ${user.nickname}`,
      description: `Chat between ${loggedInUser.nickname} and ${user.nickname}`,
      creatorInfo: loggedInUser
    };

    fetch('http://chatex.p-e.kr:11000/api/chatroom', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newChat)
    })
    .then(response => response.json())
    .then(data => {
      setChats(prevChats => [...prevChats, data]);
      navigate(`/chat/${data.id}`);
    })
    .catch(error => console.error('채팅방 생성 중 오류 발생:', error));
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>채팅</h2>
      <div style={styles.tabContainer}>
        <button 
          style={activeTab === 'chats' ? styles.activeTab : styles.tab} 
          onClick={() => setActiveTab('chats')}
        >
          채팅방
        </button>
        <button 
          style={activeTab === 'users' ? styles.activeTab : styles.tab} 
          onClick={() => setActiveTab('users')}
        >
          회원가입된 유저
        </button>
      </div>

      {activeTab === 'chats' ? (
        <>
          <ul style={styles.chatList}>
            {chats.map((chat) => (
              <li key={chat.id} style={styles.chatItem} onClick={() => openChat(chat.id)}>
                <div style={styles.chatInfo}>
                  <p style={styles.participants}>{chat.chatName}</p>
                  <p style={styles.lastMessage}>{chat.lastMessage}</p>
                </div>
                {chat.unreadCount > 0 && (
                  <div style={styles.unreadCount}>{chat.unreadCount}</div>
                )}
              </li>
            ))}
          </ul>
          <button onClick={() => setActiveTab('create')} style={styles.addButton}>+</button>
          {activeTab === 'create' && (
            <div style={styles.modalOverlay}>
              <div style={styles.modalContent}>
                <input 
                  type="text"
                  value={newChatName}
                  onChange={(e) => setNewChatName(e.target.value)}
                  placeholder="채팅방 이름"
                  style={styles.input}
                />
                <input 
                  type="text"
                  value={newChatDescription}
                  onChange={(e) => setNewChatDescription(e.target.value)}
                  placeholder="채팅방 설명"
                  style={styles.input}
                />
                <button onClick={handleCreateChat} style={styles.createButton}>채팅방 생성</button>
                <button onClick={() => setActiveTab('chats')} style={styles.cancelButton}>취소</button>
              </div>
            </div>
          )}
        </>
      ) : (
        <ul style={styles.userList}>
          {users.map(user => (
            <li key={user.userId} style={styles.userItem}>
              <p>{user.nickname}</p>
              <button style={styles.chatButton} onClick={() => startChatWithUser(user)}>
                채팅하기
              </button>
            </li>
          ))}
        </ul>
      )}
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
  tabContainer: {
    display: 'flex',
    marginBottom: '20px',
  },
  tab: {
    flex: 1,
    padding: '10px',
    backgroundColor: '#ffffff',
    border: '1px solid #ddd',
    borderRadius: '8px 8px 0 0',
    cursor: 'pointer',
    textAlign: 'center',
  },
  activeTab: {
    flex: 1,
    padding: '10px',
    backgroundColor: '#61dafb',
    border: '1px solid #ddd',
    borderBottom: 'none',
    borderRadius: '8px 8px 0 0',
    cursor: 'pointer',
    textAlign: 'center',
    fontWeight: 'bold',
  },
  newChatContainer: {
    display: 'flex',
    marginBottom: '20px',
  },
  input: {
    flex: 1,
    padding: '10px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    marginRight: '10px',
  },
  createButton: {
    padding: '10px 20px',
    borderRadius: '8px',
    backgroundColor: '#61dafb',
    border: 'none',
    cursor: 'pointer',
  },
  cancelButton: {
    padding: '10px 20px',
    borderRadius: '8px',
    backgroundColor: '#ccc',
    border: 'none',
    cursor: 'pointer',
    marginTop: '10px'
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
  userList: {
    listStyleType: 'none',
    padding: '0',
    margin: '0',
    flex: 1,
    overflowY: 'auto', // 사용자 목록 스크롤 가능하게 설정
  },
  userItem: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '15px',
    backgroundColor: '#ffffff', // 각 사용자 아이템의 배경 색상
    borderRadius: '8px',
    marginBottom: '10px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)', // 약간의 그림자 추가
  },
  chatButton: {
    padding: '10px 20px',
    borderRadius: '8px',
    backgroundColor: '#61dafb',
    border: 'none',
    cursor: 'pointer',
  },
  addButton: {
    padding: '10px',
    borderRadius: '50%',
    backgroundColor: '#61dafb',
    border: 'none',
    cursor: 'pointer',
    position: 'fixed',
    bottom: '20px',
    right: '20px',
    fontSize: '24px',
    color: 'white',
    width: '50px',
    height: '50px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  modalOverlay: {
    position: 'fixed',
    top: '0',
    left: '0',
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  modalContent: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '8px',
    display: 'flex',
    flexDirection: 'column',
  },
};

export default ChatOverviewPage;
