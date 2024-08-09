import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState(null);

  useEffect(() => {
    // 로그인 상태를 확인하여 닉네임을 설정
    const storedNickname = localStorage.getItem('nickname');
    if (storedNickname) {
      setNickname(storedNickname);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('nickname');
    setNickname(null);
    navigate('/login');
  };

  const handleChatButtonClick = () => {
    if (nickname) {
      navigate('/chat');
    } else {
      navigate('/login');
    }
  };

  return (
    <div style={styles.container}>
      <Header />
      <main style={styles.main}>
        <h2>환영합니다 {nickname ? nickname : '손님'}!</h2>
        {nickname ? (
          <div style={styles.userSection}>
            <p style={styles.welcomeMessage}>반갑습니다, {nickname}님!</p>
            <button style={styles.logoutButton} onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        ) : (
          <div style={styles.authButtons}>
            <button style={styles.button} onClick={() => navigate('/login')}>
              로그인해라
            </button>
            <button style={styles.button} onClick={() => navigate('/signup')}>
              계정없나?
            </button>
          </div>
        )}
        <button style={styles.chatButton} onClick={handleChatButtonClick}>
         기깔나는 채팅
        </button>
      </main>
      <Footer />
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
    color: '#333',
  },
  main: {
    flex: '1',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
    backgroundColor: '#f0f8ff', // 전체 배경 색상
    borderRadius: '8px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',

  },
  authButtons: {
    marginBottom: '20px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  button: {
    margin: '10px',
    padding: '10px 20px',
    fontSize: '16px',
    cursor: 'pointer',
    backgroundColor: '#007BFF',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    transition: 'background-color 0.3s',
  },
  chatButton: {
    padding: '15px 30px',
    fontSize: '18px',
    backgroundColor: '#28a745',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    transition: 'background-color 0.3s',
  },
  userSection: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    marginBottom: '20px',
  },
  welcomeMessage: {
    fontSize: '18px',
    marginBottom: '10px',
  },
  logoutButton: {
    padding: '10px 20px',
    fontSize: '16px',
    cursor: 'pointer',
    backgroundColor: '#dc3545',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    transition: 'background-color 0.3s',
  },
};

export default MainPage;
