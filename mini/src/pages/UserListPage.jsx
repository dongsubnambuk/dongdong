import React, { useState, useEffect } from 'react';

const UserListPage = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetch('http://chatex.p-e.kr:11000/api/chat/user/all')
      .then(response => response.json())
      .then(data => setUsers(data))
      .catch(error => console.error('사용자 목록을 불러오는 중 오류 발생:', error));
  }, []);

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>사용자 목록</h2>
      <ul style={styles.userList}>
        {users.map((user, index) => (
          <li key={`${user.uniqueId}-${index}`} style={styles.userItem}>
            <p>{user.nickname}</p>
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
  userList: {
    listStyleType: 'none',
    padding: '0',
    margin: '0',
  },
  userItem: {
    padding: '15px',
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    marginBottom: '10px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
  },
};

export default UserListPage;
