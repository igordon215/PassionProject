import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from 'app/entities/forum-post/forum-post.reducer';
import { getEntities as getComments } from 'app/entities/comment/comment.reducer';

export const ForumPost = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<{ id: string }>();
  const forumPostEntity = useAppSelector(state => state.forumPost.entity);
  const comments = useAppSelector(state => state.comment.entities);
  const loading = useAppSelector(state => state.forumPost.loading);

  useEffect(() => {
    dispatch(getEntity(id));
    dispatch(getComments({}));
  }, []);

  return (
    <div className="forum-post">
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div>
          <h2>{forumPostEntity.title}</h2>
          <p>{forumPostEntity.content}</p>
          <small>Posted by: {forumPostEntity.user?.login}</small>
          <h3>Comments</h3>
          {comments
            .filter(comment => comment.forumPost?.id === forumPostEntity.id)
            .map(comment => (
              <div key={comment.id} className="comment">
                <p>{comment.content}</p>
                <small>Comment by: {comment.user?.login}</small>
              </div>
            ))}
        </div>
      )}
    </div>
  );
};

export default ForumPost;
