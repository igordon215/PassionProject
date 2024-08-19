package com.passionpro.myapp.repository;

import com.passionpro.myapp.domain.ForumPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ForumPost entity.
 */
@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    @Query("select forumPost from ForumPost forumPost where forumPost.user.login = ?#{authentication.name}")
    List<ForumPost> findByUserIsCurrentUser();

    default Optional<ForumPost> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ForumPost> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ForumPost> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select forumPost from ForumPost forumPost left join fetch forumPost.user",
        countQuery = "select count(forumPost) from ForumPost forumPost"
    )
    Page<ForumPost> findAllWithToOneRelationships(Pageable pageable);

    @Query("select forumPost from ForumPost forumPost left join fetch forumPost.user")
    List<ForumPost> findAllWithToOneRelationships();

    @Query("select forumPost from ForumPost forumPost left join fetch forumPost.user where forumPost.id =:id")
    Optional<ForumPost> findOneWithToOneRelationships(@Param("id") Long id);
}
