import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ForumPost from './forum-post';
import ForumPostDetail from './forum-post-detail';
import ForumPostUpdate from './forum-post-update';
import ForumPostDeleteDialog from './forum-post-delete-dialog';

const ForumPostRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ForumPost />} />
    <Route path="new" element={<ForumPostUpdate />} />
    <Route path=":id">
      <Route index element={<ForumPostDetail />} />
      <Route path="edit" element={<ForumPostUpdate />} />
      <Route path="delete" element={<ForumPostDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ForumPostRoutes;
