import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/forum-post/forum-post.reducer';

export const ForumList = () => {
  const dispatch = useAppDispatch();
  const forumPostList = useAppSelector(state => state.forumPost.entities);
  const loading = useAppSelector(state => state.forumPost.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  return (
    <div className="forum-list">
      <h2>Forum Posts</h2>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div>
          {forumPostList.map(post => (
            <div key={post.id} className="forum-post-item">
              <h3>
                <Link to={`/forum/${post.id}`}>{post.title}</Link>
              </h3>
              <p>{post.content.substring(0, 100)}...</p>
              <small>
                Posted by: {post.user?.login} | Comments: {post.comments?.length || 0}
              </small>
            </div>
          ))}
        </div>
      )}
      <Link to="/forum/new" className="btn btn-primary">
        Create New Post
      </Link>
    </div>
  );
};

export default ForumList;
