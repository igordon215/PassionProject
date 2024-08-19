package com.passionpro.myapp.domain;

import static com.passionpro.myapp.domain.ForumPostTestSamples.*;
import static com.passionpro.myapp.domain.PostTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.passionpro.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ForumPostTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ForumPost.class);
        ForumPost forumPost1 = getForumPostSample1();
        ForumPost forumPost2 = new ForumPost();
        assertThat(forumPost1).isNotEqualTo(forumPost2);

        forumPost2.setId(forumPost1.getId());
        assertThat(forumPost1).isEqualTo(forumPost2);

        forumPost2 = getForumPostSample2();
        assertThat(forumPost1).isNotEqualTo(forumPost2);
    }

    @Test
    void postTest() {
        ForumPost forumPost = getForumPostRandomSampleGenerator();
        Post postBack = getPostRandomSampleGenerator();

        forumPost.addPost(postBack);
        assertThat(forumPost.getPosts()).containsOnly(postBack);
        assertThat(postBack.getForumPost()).isEqualTo(forumPost);

        forumPost.removePost(postBack);
        assertThat(forumPost.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getForumPost()).isNull();

        forumPost.posts(new HashSet<>(Set.of(postBack)));
        assertThat(forumPost.getPosts()).containsOnly(postBack);
        assertThat(postBack.getForumPost()).isEqualTo(forumPost);

        forumPost.setPosts(new HashSet<>());
        assertThat(forumPost.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getForumPost()).isNull();
    }
}
