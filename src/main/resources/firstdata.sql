CREATE TABLE IF NOT EXISTS User (
  user_id     VARCHAR(21) NOT NULL,
  name       VARCHAR(32) DEFAULT NULL,
  password   VARCHAR(32) DEFAULT NULL,
  gender     INTEGER     DEFAULT NULL,
  birthday   DATETIME    DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  PRIMARY KEY (user_id)
);