import React, { useState } from 'react';
import '../CSS/Logintest.css'; // 스타일을 위한 CSS 파일

const Logintest = () => {
  const [activeLogin, setActiveLogin] = useState('user'); // 초기값은 'user'로 설정

  const handleLoginClick = (type) => {
    setActiveLogin(type); // 클릭한 로그인 타입에 따라 상태를 변경
  };

  return (
    <div className="login-container">
      <div className="login-selection">
        <div
          className={`login-item ${activeLogin === 'user' ? 'active' : ''}`}
          onClick={() => handleLoginClick('user')}
        >
          유저 로그인
        </div>
        <div
          className={`login-item ${activeLogin === 'admin' ? 'active' : ''}`}
          onClick={() => handleLoginClick('admin')}
        >
          관리자 로그인
        </div>
        <div className={`active-indicator ${activeLogin}`}></div>
      </div>

      {activeLogin === 'user' ? (
        <div className="login-form">
          <h3>유저 로그인</h3>
          <input type="text" placeholder="아이디" />
          <input type="password" placeholder="비밀번호" />
          <button>로그인</button>
        </div>
      ) : (
        <div className="login-form">
          <h3>관리자 로그인</h3>
          <input type="password" placeholder="관리자 코드" />
          <button>로그인</button>
        </div>
      )}
    </div>
  );
};

export default Logintest;
