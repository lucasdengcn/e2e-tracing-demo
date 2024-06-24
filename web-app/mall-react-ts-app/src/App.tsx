import { useState } from 'react';
import { Outlet, Link } from "react-router-dom";
import React from 'react';
import reactLogo from './assets/react.svg';
import viteLogo from '/vite.svg';
import './App.css';

function App() {

  return (
    <>
      <h1>Web Tracing Demo</h1>
      <div>
        <Outlet />
      </div>
      <div>
        <p><Link to={`/`}>Home</Link></p>
        <p><Link to={`checkout`} traceable="true">Checkout</Link></p>
        <p><Link to={`payment/1`} traceable="true">Payment</Link></p>
        <p><Link to={`scientists`} traceable="true">Scientists</Link></p>
      </div>
    </>
  );
}

export default App;
