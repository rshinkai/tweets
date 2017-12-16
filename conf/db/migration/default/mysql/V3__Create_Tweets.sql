CREATE TABLE `tweets` (
  `id`        BIGINT                AUTO_INCREMENT,
  `user_id`   BIGINT       NOT NULL,
  `content`   VARCHAR(140) NOT NULL,
  `create_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
)
  ENGINE = InnoDB;

-- user_idで検索するためインデックスを割り当てておく
CREATE INDEX `tweets_user_id_idx` ON `users` (`id`);