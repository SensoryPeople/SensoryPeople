import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './MainPage';
import SignUpForm from './components/SignUpForm';
import LoginForm from './components/LoginForm';
import BoardList from './components/BoardList';
import KanbanBoard from './components/KanbanBoard';
import BoardMember from './components/BoardMember';
import CardComments from './components/CardComments'; // CardComments 컴포넌트 추가
import './App.css';

const App = () => {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/signup" element={<SignUpForm />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/boardlist" element={<BoardList />} />
          <Route path="/boards/:boardId/kanban" element={<KanbanBoard />} />
          <Route path="/boards/:boardId/members" element={<BoardMember />} /> {/* 동적 경로 설정 */}
          <Route path="/cardcomments" element={<CardComments />} /> {/* CardComments 컴포넌트 라우팅 추가 */}
        </Routes>
      </Router>
  );
};

export default App;