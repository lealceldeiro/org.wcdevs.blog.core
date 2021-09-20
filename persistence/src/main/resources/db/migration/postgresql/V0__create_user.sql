CREATE TABLE "user" (
  id            BIGINT NOT NULL UNIQUE CONSTRAINT "PK_USER",
  username      VARCHAR(30) NOT NULL UNIQUE,
  email         VARCHAR(30) NOT NULL UNIQUE,
  password      VARCHAR(1000) NOT NULL,
  name          VARCHAR(50),
  last_name     VARCHAR(100),
  sign_up_date  DATE NOT NULL
);
