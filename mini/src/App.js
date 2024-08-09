import React from 'react';
import './App.css';
import MainPage from './pages/MainPage';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';

function App() {
  return (
    <div className="mobile-container">

<Router>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
   
      </Routes>
    </Router>

    </div>
    

  );
}

export default App;
