import React from 'react';
import './App.css';
import MainPage from './pages/MainPage';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import ChatOverviewPage from './pages/ChatOverviewPage';
import ChatPage from './pages/ChatPage';
import Logintest from './pages/Logintest';
function App() {
  return (
    <div className="mobile-container">
      <Router>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Logintest />} />
          <Route path="/chat" element={<ChatOverviewPage />} />
          <Route path="/chat/:id" element={<ChatPage />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
