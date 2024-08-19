import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './forum-post.reducer';

export const ForumPostDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const forumPostEntity = useAppSelector(state => state.forumPost.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="forumPostDetailsHeading">Forum Post</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{forumPostEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{forumPostEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{forumPostEntity.description}</dd>
          <dt>
            <span id="created_at">Created At</span>
          </dt>
          <dd>
            {forumPostEntity.created_at ? <TextFormat value={forumPostEntity.created_at} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="created_by">Created By</span>
          </dt>
          <dd>{forumPostEntity.created_by}</dd>
          <dt>User</dt>
          <dd>{forumPostEntity.user ? forumPostEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/forum-post" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/forum-post/${forumPostEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ForumPostDetail;
