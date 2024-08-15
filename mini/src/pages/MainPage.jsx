import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");

    // 로그인한 경우에만 fetch 요청을 보냄
    if (token && email) {
      const handleget = async () => {
        const response = await fetch(`http://chatex.p-e.kr/api/auth/user?email=${email}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": token,
          }
        });
  
        const result = await response.json(); // 응답이 JSON 형식일 경우 이를 JavaScript 객체로 변환
  
        if (response.status === 200) { // 응답 status가 200 OK 일 경우
          setNickname(result.nickname);
        } else {
          console.log("사용자 정보 가져오기 실패");
          alert("사용자 정보 가져오기 실패: " + result.message);
        }
      };
  
      handleget();
    }
  }, []); // 빈 배열을 두어 컴포넌트가 마운트될 때만 실행되도록 함

  const handleLogout = () => {
    localStorage.removeItem('nickname');
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    setNickname(null);
    navigate('/');
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
