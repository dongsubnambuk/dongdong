import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
    const navigate = useNavigate();

  return (
    <div style={styles.container}>
      <Header />
      <main style={styles.main}>
        <h2>뭘보노 눌러삐라</h2>
        <div style={styles.authButtons}>
          <button style={styles.button} onClick={() => navigate('/login')}>로그인해라</button>
          <button style={styles.button} onClick={() => navigate('/signup')}>계정없나?</button>
        </div>
        <button style={styles.chatButton} onClick={() => navigate('/chat')}>내랑 연락할사람</button>
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
  },
  main: {
    flex: '1',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
  },
  authButtons: {
    marginBottom: '20px',
   
  },
  button: {
    margin: '10px',
    padding: '10px 20px',
    fontSize: '16px',
    cursor:'pointer'
  },
  chatButton: {
    padding: '15px 30px',
    fontSize: '18px',
    backgroundColor: '#61dafb',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
  },
};

export default MainPage;
