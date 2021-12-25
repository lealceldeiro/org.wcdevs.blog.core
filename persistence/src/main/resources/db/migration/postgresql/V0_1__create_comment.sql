CREATE TABLE IF NOT EXISTS comment
(
  uuid                uuid          NOT NULL UNIQUE
    CONSTRAINT pk_comment PRIMARY KEY,
  anchor              VARCHAR(250)  NOT NULL UNIQUE,
  body                VARCHAR(3000) NOT NULL,
  last_updated        TIMESTAMP     NOT NULL,
  published_by        VARCHAR(100)  NOT NULL,
  post_uuid           uuid          NOT NULL,
  parent_comment_uuid uuid,
  /*
   * see https://www.postgresql.org/docs/12/ddl-constraints.html#DDL-CONSTRAINTS-FK
   */
  FOREIGN KEY (post_uuid) REFERENCES post (uuid) ON DELETE CASCADE,
  FOREIGN KEY (parent_comment_uuid) REFERENCES comment (uuid) ON DELETE CASCADE
);
