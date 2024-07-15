import React from 'react';
import { Link } from 'react-router-dom';
import './MainPage.css';

const MainPage = () => {
  return (
      <div className="container">
        <div className="logo">Sensory People</div>
        <h1>Project : Trello</h1>
        <p>Please sign up and log in</p>
        <div className="buttons">
          <Link to="/login" className="button signin">Log In</Link>
          <Link to="/signup" className="button signup">Sign Up</Link>
        </div>
        <div className="features">
          <div className="feature">
            <h3>ㅎㅅㅎ</h3>
            <p>고맙다GPT야</p>
          </div>
          <div className="feature">
            <h3>ㅜ0ㅜ</h3>
            <p>그립다 백엔드</p>
          </div>
          <div className="feature">
            <h3>테스트</h3>
            <p>테스트중입니다</p>
          </div>
        </div>
      </div>
  );
};

export default MainPage;
