import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch } from 'app/config/store';
import { createEntity } from 'app/entities/forum-post/forum-post.reducer';

export const CreatePost = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const handleSubmit = e => {
    e.preventDefault();
    dispatch(createEntity({ title, content }));
    navigate('/');
  };

  return (
    <div className="create-post">
      <h2>Create New Post</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title</label>
          <input type="text" className="form-control" id="title" value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div className="form-group">
          <label htmlFor="content">Content</label>
          <textarea className="form-control" id="content" value={content} onChange={e => setContent(e.target.value)} required />
        </div>
        <button type="submit" className="btn btn-primary">
          Submit
        </button>
      </form>
    </div>
  );
};

export default CreatePost;
