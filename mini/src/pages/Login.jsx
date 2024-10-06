import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../CSS/Login.css';


function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();



    const changeEmail = (e) => {
        setEmail(e.target.value);
    };

    const changePassword = (e) => {
        setPassword(e.target.value);
    };



    // login fetch 함수
    const handleLogin = async (event) => {
        event.preventDefault();
    
        try {
            const response = await fetch('http://3.37.122.192:8000/api/auth/login', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    username: email,
                    password: password,
                }),
            });
    
            const result = await response.json();
    
            if (response.status === 200) {
                console.log(result);
                // localStorage.setItem("token", result.token);
                console.log("로그인 성공");
                // navigate('/'); 
            } else {
                console.log("로그인 실패");
                alert("로그인 실패: " + result.message);
            }
        } catch (error) {
            console.error("Fetch error: ", error);
        }
    };

    return (
        <>
        

            <div className="login-inner">
                <input type="text" id="email" value={email} className="login-email" placeholder="아이디" onChange={changeEmail} />
                <input type="password" id="password" value={password} className="login-password" placeholder="비밀번호" onChange={changePassword} />

                <button className="login-btn" onClick={handleLogin}>로그인</button>
                <div className="login-options">
                    <label>
                        <input type="checkbox" /> 자동 로그인
                    </label>
                    <div>
                        <a href="/find-id">아이디 찾기</a> | <a href="/find-password">비밀번호 찾기</a>
                    </div>
                </div>
          
            </div>

          
        </>
    );
}

export default Login;
