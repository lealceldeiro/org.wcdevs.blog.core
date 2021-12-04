CREATE TABLE IF NOT EXISTS post
(
  uuid         uuid         NOT NULL UNIQUE
    CONSTRAINT pk_user PRIMARY KEY,
  title        VARCHAR(250) NOT NULL UNIQUE,
  slug         VARCHAR(200) NOT NULL UNIQUE,
  body         TEXT         NOT NULL,
  excerpt      VARCHAR(300) NOT NULL UNIQUE,
  published_on TIMESTAMP    NOT NULL,
  updated_on   TIMESTAMP    NOT NULL
);
