import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ForumPost e2e test', () => {
  const forumPostPageUrl = '/forum-post';
  const forumPostPageUrlPattern = new RegExp('/forum-post(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const forumPostSample = { name: 'bah absolute' };

  let forumPost;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/forum-posts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/forum-posts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/forum-posts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (forumPost) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/forum-posts/${forumPost.id}`,
      }).then(() => {
        forumPost = undefined;
      });
    }
  });

  it('ForumPosts menu should load ForumPosts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('forum-post');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ForumPost').should('exist');
    cy.url().should('match', forumPostPageUrlPattern);
  });

  describe('ForumPost page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(forumPostPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ForumPost page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/forum-post/new$'));
        cy.getEntityCreateUpdateHeading('ForumPost');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', forumPostPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/forum-posts',
          body: forumPostSample,
        }).then(({ body }) => {
          forumPost = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/forum-posts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/forum-posts?page=0&size=20>; rel="last",<http://localhost/api/forum-posts?page=0&size=20>; rel="first"',
              },
              body: [forumPost],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(forumPostPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ForumPost page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('forumPost');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', forumPostPageUrlPattern);
      });

      it('edit button click should load edit ForumPost page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ForumPost');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', forumPostPageUrlPattern);
      });

      it('edit button click should load edit ForumPost page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ForumPost');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', forumPostPageUrlPattern);
      });

      it('last delete button click should delete instance of ForumPost', () => {
        cy.intercept('GET', '/api/forum-posts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('forumPost').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', forumPostPageUrlPattern);

        forumPost = undefined;
      });
    });
  });

  describe('new ForumPost page', () => {
    beforeEach(() => {
      cy.visit(`${forumPostPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ForumPost');
    });

    it('should create an instance of ForumPost', () => {
      cy.get(`[data-cy="name"]`).type('pitiful beatify who');
      cy.get(`[data-cy="name"]`).should('have.value', 'pitiful beatify who');

      cy.get(`[data-cy="description"]`).type('dribble softly');
      cy.get(`[data-cy="description"]`).should('have.value', 'dribble softly');

      cy.get(`[data-cy="created_at"]`).type('2024-08-19T19:02');
      cy.get(`[data-cy="created_at"]`).blur();
      cy.get(`[data-cy="created_at"]`).should('have.value', '2024-08-19T19:02');

      cy.get(`[data-cy="created_by"]`).type('incidentally wrong');
      cy.get(`[data-cy="created_by"]`).should('have.value', 'incidentally wrong');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        forumPost = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', forumPostPageUrlPattern);
    });
  });
});
