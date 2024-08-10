import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ChatOverviewPage = () => {
  const navigate = useNavigate();
  const [chats, setChats] = useState([]); // 초기값을 빈 배열로 설정
  const [createdChats, setCreatedChats] = useState([]); // 생성된 방 상태 관리
  const [users, setUsers] = useState([]);
  const [newChatName, setNewChatName] = useState('');
  const [newChatDescription, setNewChatDescription] = useState('');
  const [activeTab, setActiveTab] = useState('chats'); // 초기 탭 상태
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리

  const email = localStorage.getItem("email");
  const nickname = localStorage.getItem("nickname");
  const userId = localStorage.getItem("UID");

  useEffect(() => {
    // 모든 채팅방 조회 API 호출
    fetch('http://chatex.p-e.kr:11000/api/chatroom/all')
      .then(response => response.json())
      .then(data => {
        if (Array.isArray(data)) {
          setChats(data); // 데이터가 배열이면 설정
        } else {
          console.error('채팅방 목록의 형식이 올바르지 않습니다:', data);
          setChats([]); // 데이터가 배열이 아니면 빈 배열로 설정
        }
      })
      .catch(error => {
        console.error('채팅방 목록을 불러오는 중 오류 발생:', error);
        setChats([]); // 오류 발생 시 빈 배열로 설정
      });

    // 사용자 목록 조회 API 호출
    fetch('http://chatex.p-e.kr:10000/api/users')
      .then(response => response.json())
      .then(data => setUsers(data))
      .catch(error => console.error('사용자 목록을 불러오는 중 오류 발생:', error));
  }, []);

  const handleCreateChat = () => {
    const loggedInUser = {
      userId: userId,  
      nickname: nickname,
      email: email
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
      setCreatedChats(prevCreatedChats => [...prevCreatedChats, data]); // 생성된 방에 추가
      setNewChatName('');
      setNewChatDescription('');
      setIsModalOpen(false);  // 모달 닫기
      navigate(`/chat/${data.chatRoomId}`); // 생성된 방으로 이동
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
      userId: userId,  
      nickname: nickname,
      email: email
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
      setCreatedChats(prevCreatedChats => [...prevCreatedChats, data]); // 생성된 방에 추가
      navigate(`/chat/${data.chatRoomId}`); // 생성된 방으로 이동
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
          style={activeTab === 'created' ? styles.activeTab : styles.tab} 
          onClick={() => setActiveTab('created')}
        >
          생성 된 방
        </button>
        <button 
          style={activeTab === 'users' ? styles.activeTab : styles.tab} 
          onClick={() => setActiveTab('users')}
        >
          회원가입된 유저
        </button>
      </div>

      {activeTab === 'created' ? (
        <>
          <ul style={styles.chatList}>
            {createdChats.map((chat, index) => (
              <li key={`${chat.id}-${index}`} style={styles.chatItem} onClick={() => openChat(chat.id)}>
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
        </>
      ) : activeTab === 'chats' ? (
        <>
          <ul style={styles.chatList}>
            {chats.map((chat, index) => (
              <li key={`${chat.id}-${index}`} style={styles.chatItem} onClick={() => openChat(chat.id)}>
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
          <button onClick={() => setIsModalOpen(true)} style={styles.addButton}>+</button>

          {/* 모달 렌더링 부분 추가 */}
          {isModalOpen && (
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
                <button onClick={() => setIsModalOpen(false)} style={styles.cancelButton}>취소</button>
              </div>
            </div>
          )}
        </>
      ) : activeTab === 'users' ? (
        <ul style={styles.userList}>
          {users.map((user, index) => (
            <li key={`${user.userId}-${index}`} style={styles.userItem}>
              <p>{user.nickname}</p>
              <button style={styles.chatButton} onClick={() => startChatWithUser(user)}>
                채팅하기
              </button>
            </li>
          ))}
        </ul>
      ) : null}
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
    borderTop: '1px solid #ddd',
    borderRight: '1px solid #ddd',
    borderLeft: '1px solid #ddd',
    borderBottom: '1px solid #ddd',
    borderRadius: '8px 8px 0 0',
    cursor: 'pointer',
    textAlign: 'center',
  },
  activeTab: {
    flex: 1,
    padding: '10px',
    backgroundColor: '#61dafb',
    borderTop: '1px solid #ddd',
    borderRight: '1px solid #ddd',
    borderLeft: '1px solid #ddd',
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
    zIndex: 1000, // 모달이 다른 요소들 위에 나타나도록 설정
  },
  modalContent: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '8px',
    display: 'flex',
    flexDirection: 'column',
    zIndex: 1001, // 모달 콘텐츠가 항상 위에 표시되도록 설정
  },
};

export default ChatOverviewPage;
