package com.passionpro.myapp.web.rest;

import com.passionpro.myapp.domain.ForumPost;
import com.passionpro.myapp.repository.ForumPostRepository;
import com.passionpro.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.passionpro.myapp.domain.ForumPost}.
 */
@RestController
@RequestMapping("/api/forum-posts")
@Transactional
public class ForumPostResource {

    private static final Logger log = LoggerFactory.getLogger(ForumPostResource.class);

    private static final String ENTITY_NAME = "forumPost";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ForumPostRepository forumPostRepository;

    public ForumPostResource(ForumPostRepository forumPostRepository) {
        this.forumPostRepository = forumPostRepository;
    }

    /**
     * {@code POST  /forum-posts} : Create a new forumPost.
     *
     * @param forumPost the forumPost to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new forumPost, or with status {@code 400 (Bad Request)} if the forumPost has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ForumPost> createForumPost(@Valid @RequestBody ForumPost forumPost) throws URISyntaxException {
        log.debug("REST request to save ForumPost : {}", forumPost);
        if (forumPost.getId() != null) {
            throw new BadRequestAlertException("A new forumPost cannot already have an ID", ENTITY_NAME, "idexists");
        }
        forumPost = forumPostRepository.save(forumPost);
        return ResponseEntity.created(new URI("/api/forum-posts/" + forumPost.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, forumPost.getId().toString()))
            .body(forumPost);
    }

    /**
     * {@code PUT  /forum-posts/:id} : Updates an existing forumPost.
     *
     * @param id the id of the forumPost to save.
     * @param forumPost the forumPost to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated forumPost,
     * or with status {@code 400 (Bad Request)} if the forumPost is not valid,
     * or with status {@code 500 (Internal Server Error)} if the forumPost couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ForumPost> updateForumPost(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ForumPost forumPost
    ) throws URISyntaxException {
        log.debug("REST request to update ForumPost : {}, {}", id, forumPost);
        if (forumPost.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, forumPost.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!forumPostRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        forumPost = forumPostRepository.save(forumPost);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, forumPost.getId().toString()))
            .body(forumPost);
    }

    /**
     * {@code PATCH  /forum-posts/:id} : Partial updates given fields of an existing forumPost, field will ignore if it is null
     *
     * @param id the id of the forumPost to save.
     * @param forumPost the forumPost to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated forumPost,
     * or with status {@code 400 (Bad Request)} if the forumPost is not valid,
     * or with status {@code 404 (Not Found)} if the forumPost is not found,
     * or with status {@code 500 (Internal Server Error)} if the forumPost couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ForumPost> partialUpdateForumPost(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ForumPost forumPost
    ) throws URISyntaxException {
        log.debug("REST request to partial update ForumPost partially : {}, {}", id, forumPost);
        if (forumPost.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, forumPost.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!forumPostRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ForumPost> result = forumPostRepository
            .findById(forumPost.getId())
            .map(existingForumPost -> {
                if (forumPost.getName() != null) {
                    existingForumPost.setName(forumPost.getName());
                }
                if (forumPost.getDescription() != null) {
                    existingForumPost.setDescription(forumPost.getDescription());
                }
                if (forumPost.getCreated_at() != null) {
                    existingForumPost.setCreated_at(forumPost.getCreated_at());
                }
                if (forumPost.getCreated_by() != null) {
                    existingForumPost.setCreated_by(forumPost.getCreated_by());
                }

                return existingForumPost;
            })
            .map(forumPostRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, forumPost.getId().toString())
        );
    }

    /**
     * {@code GET  /forum-posts} : get all the forumPosts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of forumPosts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ForumPost>> getAllForumPosts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of ForumPosts");
        Page<ForumPost> page;
        if (eagerload) {
            page = forumPostRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = forumPostRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /forum-posts/:id} : get the "id" forumPost.
     *
     * @param id the id of the forumPost to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the forumPost, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ForumPost> getForumPost(@PathVariable("id") Long id) {
        log.debug("REST request to get ForumPost : {}", id);
        Optional<ForumPost> forumPost = forumPostRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(forumPost);
    }

    /**
     * {@code DELETE  /forum-posts/:id} : delete the "id" forumPost.
     *
     * @param id the id of the forumPost to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForumPost(@PathVariable("id") Long id) {
        log.debug("REST request to delete ForumPost : {}", id);
        forumPostRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
