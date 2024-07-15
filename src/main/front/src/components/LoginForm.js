import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginForm.css';

const LoginForm = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/users/login', {
        userId: formData.username,
        password: formData.password,
      });
      const token = response.headers['authorization'].substring(7); // 응답 헤더에서 Authorization 헤더 추출
      sessionStorage.setItem('token', token);
      alert(`로그인 성공: ${response.data.message}`);
      // 로그인 성공 시 처리 (예: 토큰 저장 등)
      navigate('/boardlist');
    } catch (error) {
      if (error.response) {
        setMessage(error.response.data.message || '로그인 중 오류가 발생했습니다.');
      } else {
        setMessage('로그인 중 오류가 발생했습니다.');
      }
    }
  };

  return (
      <div className="login-container">
        <h1>Sensory People Log In</h1>
        <form onSubmit={handleSubmit}>
          <input
              type="text"
              id="username"
              name="username"
              placeholder="ID"
              value={formData.username}
              onChange={handleChange}
              required
          />
          <input
              type="password"
              id="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              required
          />
          <button type="submit">Log In</button>
        </form>
        {message && <p className="error-message">{message}</p>}
        <div className="signup">
          <a href="/signup">Sign Up</a>
        </div>
      </div>
  );
};

export default LoginForm;
