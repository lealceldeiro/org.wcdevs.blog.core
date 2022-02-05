CREATE TABLE IF NOT EXISTS comment
(
  uuid                uuid          NOT NULL UNIQUE,
  anchor              VARCHAR(250)  NOT NULL UNIQUE,
  body                VARCHAR(3000) NOT NULL,
  last_updated        TIMESTAMP     NOT NULL,
  published_by        VARCHAR(100)  NOT NULL,
  post_uuid           uuid          NOT NULL,
  parent_comment_uuid uuid,
  PRIMARY KEY(uuid),
  FOREIGN KEY (post_uuid) REFERENCES post (uuid) ON DELETE CASCADE,
  FOREIGN KEY (parent_comment_uuid) REFERENCES comment (uuid) ON DELETE CASCADE
);
