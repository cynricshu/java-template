CREATE SCHEMA IF NOT EXISTS `k8s_console`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `app` (
  `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `name`        varchar(60) NOT NULL,
  `create_time` TIMESTAMP   NOT NULL
  COMMENT '数据更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;