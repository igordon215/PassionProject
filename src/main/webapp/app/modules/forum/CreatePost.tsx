import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch } from 'app/config/store';
import { createEntity } from 'app/entities/forum-post/forum-post.reducer';

export const CreatePost = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const handleSubmit = e => {
    e.preventDefault();
    dispatch(createEntity({ name, description }));
    navigate('/');
  };

  return (
    <div className="create-post">
      <h2>Create New Post</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Name</label>
          <input type="text" className="form-control" id="name" value={name} onChange={e => setName(e.target.value)} required />
        </div>
        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea className="form-control" id="description" value={description} onChange={e => setDescription(e.target.value)} required />
        </div>
        <button type="submit" className="btn btn-primary">
          Submit
        </button>
      </form>
    </div>
  );
};

export default CreatePost;
