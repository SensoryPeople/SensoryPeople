import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SignUpForm.css';

const SignUpForm = () => {
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [userName, setUserName] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/users/signup', {
        userId,
        password,
        userName,
        email,
      });
      setMessage(response.data.message);
      navigate('/login'); // 회원가입 성공 후 로그인 페이지로 이동
    } catch (error) {
      if (error.response) {
        setMessage(error.response.data.message || '회원가입 중 오류가 발생했습니다.');
      } else {
        setMessage('회원가입 중 오류가 발생했습니다.');
      }
    }
  };

  return (
      <div className="signup-container">
        <h1>Sign up</h1>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="userId">ID</label>
            <input
                type="text"
                id="userId"
                name="userId"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                required
                pattern="^[a-z0-9]{4,10}$"
                title="사용자 ID는 알파벳 소문자와 숫자로 이루어진 4자에서 10자 사이여야 합니다."
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
                type="password"
                id="password"
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />
          </div>
          <div className="form-group">
            <label htmlFor="userName">UserName</label>
            <input
                type="text"
                id="userName"
                name="userName"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">E-mail</label>
            <input
                type="email"
                id="email"
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
            />
          </div>
          <button type="submit">Sign up</button>
        </form>
        {message && <p>{message}</p>}
      </div>
  );
};

export default SignUpForm;
