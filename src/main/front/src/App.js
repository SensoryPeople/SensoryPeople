import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './MainPage';
import SignUpForm from './components/SignUpForm';
import LoginForm from './components/LoginForm';
import BoardList from './components/BoardList';
import './App.css';

const App = () => {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/signup" element={<SignUpForm />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/boardlist" element={<BoardList />} />
        </Routes>
      </Router>
  );
};

export default App;
