import React from 'react';
import { useNavigate } from 'react-router-dom';

const NotFoundPage = () => {
  const navigate = useNavigate();

  const goBack = () => {
    navigate(-1);  // 이전 페이지로 이동
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>404 - 페이지를 찾을 수 없습니다</h2>
      <p>죄송합니다, 요청하신 페이지를 찾을 수 없습니다.</p>
      <button onClick={goBack} style={styles.button}>돌아가기</button>
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    textAlign: 'center',
  },
  header: {
    fontSize: '24px',
    fontWeight: 'bold',
    marginBottom: '20px',
  },
  button: {
    padding: '10px 20px',
    backgroundColor: '#61dafb',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
  },
};

export default NotFoundPage;
