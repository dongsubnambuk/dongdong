import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../CSS/Signup.css';


function Signup(){
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [nickname, setNickname] = useState("");

    const [error, setError] = useState("");
    const navigate = useNavigate();




      const validatePasswords = (password, confirmPassword) => {
        if (password !== confirmPassword) {
            setError("비밀번호가 일치하지 않습니다.");
        } else {
            setError("");
        }
    };



    //회원가입 fetch
    const handleSignup = async (event) => {
        event.preventDefault();

        const response = await fetch('http://chatex.p-e.kr/api/register', { 
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                email: email,
                password: password,
                nickname: nickname,
            }),
        });

        const result = await response.json(); // 응답이 JSON 형식일 경우 이를 JavaScript 객체로 변환
      
        if (response.status === 200) { // 응답 status가 200 OK 일 경우
          
            console.log(result);
            console.log("회원가입 성공");
            alert("회원가입 성공");
            navigate('/'); 
        } else {
            console.log("회원가입 실패");
            alert("회원가입 실패: " + result.message);
        }
    };

    return(
        <>
   
            <div className="signup-inner">
            <div className="form-group">
                <label htmlFor="email">이메일</label>
                <input
                    type="text"
                    id="email"
                    value={email}
                    className="signup-email"
                    placeholder="아이디를 입력해주세요"
                    onChange={(e) => setEmail(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="password">비밀번호</label>
                <input
                    type="password"
                    id="password"
                    value={password}
                    className="signup-password"
                    placeholder="비밀번호를 입력해주세요"
                    onChange={(e) => {
                        setPassword(e.target.value);
                        validatePasswords(e.target.value, confirmPassword);
                    }}
                />
            </div>

            <div className="form-group">
                <label htmlFor="confirm-password">비밀번호 확인</label>
                <input
                    type="password"
                    id="confirm-password"
                    value={confirmPassword}
                    placeholder="비밀번호를 다시 입력해 주세요"
                    onChange={(e) => {
                        setConfirmPassword(e.target.value);
                        validatePasswords(password, e.target.value);
                    }}
                />
                    {error && <div className="error-message">{error}</div>} {/* 에러 메시지 표시 */}
            </div>

            <div className="form-group">
                <label htmlFor="username">닉네임</label>
                <input
                    type="text"
                    id="nickname"
                    value={nickname}
                    placeholder="닉네임을 입력해주세요."
                    onChange={(e) => setNickname(e.target.value)}
                />
             
            </div>


            <button className="signup-btn" onClick={handleSignup}>회원가입</button>
        </div>
     
   
        </>
    );
}

export default Signup;