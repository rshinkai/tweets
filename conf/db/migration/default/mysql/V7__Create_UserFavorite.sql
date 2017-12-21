CREATE TABLE `user_favorites` (
  `id`        BIGINT        AUTO_INCREMENT,
  `user_id`   BIGINT        NOT NULL,
  `tweet_id`  BIGINT        NOT NULL,
  `create_at` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`tweet_id`) REFERENCES tweets(`id`),
  UNIQUE(`user_id`, `tweet_id`)
) ENGINE=InnoDB;