import React from 'react';
import { Link } from 'react-router-dom';
import { useAppSelector } from 'app/config/store';

export const Header = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <header className="forum-header">
      <nav className="navbar navbar-expand-sm navbar-light bg-light">
        <div className="container">
          <Link to="/" className="navbar-brand">
            Forum
          </Link>
          <div className="navbar-nav ml-auto">
            {account.login ? (
              <>
                <span className="nav-item nav-link">Welcome, {account.login}</span>
                <Link to="/logout" className="nav-item nav-link">
                  Logout
                </Link>
              </>
            ) : (
              <Link to="/login" className="nav-item nav-link">
                Login
              </Link>
            )}
          </div>
        </div>
      </nav>
    </header>
  );
};

export default Header;
