CREATE TABLE IF NOT EXISTS post
(
  uuid         UUID         NOT NULL UNIQUE
    CONSTRAINT pk_user PRIMARY KEY,
  title        VARCHAR(200) NOT NULL UNIQUE,
  slug         VARCHAR(150) NOT NULL UNIQUE,
  body         TEXT         NOT NULL,
  published_on TIMESTAMP    NOT NULL,
  updated_on   TIMESTAMP    NOT NULL
);
