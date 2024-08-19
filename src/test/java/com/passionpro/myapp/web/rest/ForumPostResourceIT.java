package com.passionpro.myapp.web.rest;

import static com.passionpro.myapp.domain.ForumPostAsserts.*;
import static com.passionpro.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passionpro.myapp.IntegrationTest;
import com.passionpro.myapp.domain.ForumPost;
import com.passionpro.myapp.repository.ForumPostRepository;
import com.passionpro.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ForumPostResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ForumPostResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/forum-posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ForumPostRepository forumPostRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restForumPostMockMvc;

    private ForumPost forumPost;

    private ForumPost insertedForumPost;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ForumPost createEntity(EntityManager em) {
        ForumPost forumPost = new ForumPost()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .created_at(DEFAULT_CREATED_AT)
            .created_by(DEFAULT_CREATED_BY);
        return forumPost;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ForumPost createUpdatedEntity(EntityManager em) {
        ForumPost forumPost = new ForumPost()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .created_at(UPDATED_CREATED_AT)
            .created_by(UPDATED_CREATED_BY);
        return forumPost;
    }

    @BeforeEach
    public void initTest() {
        forumPost = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedForumPost != null) {
            forumPostRepository.delete(insertedForumPost);
            insertedForumPost = null;
        }
    }

    @Test
    @Transactional
    void createForumPost() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ForumPost
        var returnedForumPost = om.readValue(
            restForumPostMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forumPost)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ForumPost.class
        );

        // Validate the ForumPost in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertForumPostUpdatableFieldsEquals(returnedForumPost, getPersistedForumPost(returnedForumPost));

        insertedForumPost = returnedForumPost;
    }

    @Test
    @Transactional
    void createForumPostWithExistingId() throws Exception {
        // Create the ForumPost with an existing ID
        forumPost.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restForumPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forumPost)))
            .andExpect(status().isBadRequest());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        forumPost.setName(null);

        // Create the ForumPost, which fails.

        restForumPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forumPost)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllForumPosts() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        // Get all the forumPostList
        restForumPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(forumPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].created_at").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].created_by").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllForumPostsWithEagerRelationshipsIsEnabled() throws Exception {
        when(forumPostRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restForumPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(forumPostRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllForumPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(forumPostRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restForumPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(forumPostRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getForumPost() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        // Get the forumPost
        restForumPostMockMvc
            .perform(get(ENTITY_API_URL_ID, forumPost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(forumPost.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.created_at").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.created_by").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingForumPost() throws Exception {
        // Get the forumPost
        restForumPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingForumPost() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forumPost
        ForumPost updatedForumPost = forumPostRepository.findById(forumPost.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedForumPost are not directly saved in db
        em.detach(updatedForumPost);
        updatedForumPost.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created_at(UPDATED_CREATED_AT).created_by(UPDATED_CREATED_BY);

        restForumPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedForumPost.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedForumPost))
            )
            .andExpect(status().isOk());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedForumPostToMatchAllProperties(updatedForumPost);
    }

    @Test
    @Transactional
    void putNonExistingForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, forumPost.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forumPost))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(forumPost))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(forumPost)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateForumPostWithPatch() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forumPost using partial update
        ForumPost partialUpdatedForumPost = new ForumPost();
        partialUpdatedForumPost.setId(forumPost.getId());

        partialUpdatedForumPost.name(UPDATED_NAME).created_at(UPDATED_CREATED_AT);

        restForumPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedForumPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedForumPost))
            )
            .andExpect(status().isOk());

        // Validate the ForumPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertForumPostUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedForumPost, forumPost),
            getPersistedForumPost(forumPost)
        );
    }

    @Test
    @Transactional
    void fullUpdateForumPostWithPatch() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the forumPost using partial update
        ForumPost partialUpdatedForumPost = new ForumPost();
        partialUpdatedForumPost.setId(forumPost.getId());

        partialUpdatedForumPost
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .created_at(UPDATED_CREATED_AT)
            .created_by(UPDATED_CREATED_BY);

        restForumPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedForumPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedForumPost))
            )
            .andExpect(status().isOk());

        // Validate the ForumPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertForumPostUpdatableFieldsEquals(partialUpdatedForumPost, getPersistedForumPost(partialUpdatedForumPost));
    }

    @Test
    @Transactional
    void patchNonExistingForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, forumPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(forumPost))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(forumPost))
            )
            .andExpect(status().isBadRequest());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamForumPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        forumPost.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restForumPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(forumPost)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ForumPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteForumPost() throws Exception {
        // Initialize the database
        insertedForumPost = forumPostRepository.saveAndFlush(forumPost);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the forumPost
        restForumPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, forumPost.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return forumPostRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ForumPost getPersistedForumPost(ForumPost forumPost) {
        return forumPostRepository.findById(forumPost.getId()).orElseThrow();
    }

    protected void assertPersistedForumPostToMatchAllProperties(ForumPost expectedForumPost) {
        assertForumPostAllPropertiesEquals(expectedForumPost, getPersistedForumPost(expectedForumPost));
    }

    protected void assertPersistedForumPostToMatchUpdatableProperties(ForumPost expectedForumPost) {
        assertForumPostAllUpdatablePropertiesEquals(expectedForumPost, getPersistedForumPost(expectedForumPost));
    }
}
