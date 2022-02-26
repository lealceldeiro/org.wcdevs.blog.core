CREATE TABLE IF NOT EXISTS post
(
  uuid         UUID         NOT NULL UNIQUE,
  title        VARCHAR(250) NOT NULL,
  slug         VARCHAR(200) NOT NULL UNIQUE,
  body         LONGVARCHAR  NOT NULL,
  excerpt      VARCHAR(300) NOT NULL,
  published_on TIMESTAMP    NOT NULL,
  updated_on   TIMESTAMP    NOT NULL,
  published_by VARCHAR(100) NOT NULL,
  updated_by   VARCHAR(100) NOT NULL,
  status       SMALLINT     NOT NULL,
  PRIMARY KEY (uuid)
);
