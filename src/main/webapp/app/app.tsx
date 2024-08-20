import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import Header from 'app/modules/forum/Header';
import ForumList from 'app/modules/forum/ForumList';
import ForumPost from 'app/modules/forum/ForumPost';
import CreatePost from 'app/modules/forum/CreatePost';

export const App = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, []);

  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

  return (
    <Router>
      <div className="app-container">
        <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
        <ErrorBoundary>
          <Header />
        </ErrorBoundary>
        <div className="container mt-3">
          <ErrorBoundary>
            <Routes>
              <Route path="/" element={<ForumList />} />
              <Route path="/forum/:id" element={<ForumPost />} />
              <Route path="/forum/new" element={isAuthenticated ? <CreatePost /> : <ForumList />} />
            </Routes>
          </ErrorBoundary>
        </div>
      </div>
    </Router>
  );
};

export default App;
