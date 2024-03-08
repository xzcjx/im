CREATE TABLE `im_session`
(
    `id`         int                                                           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`    int                                                           NOT NULL COMMENT '用户id',
    `friend_id`  int                                                           NOT NULL COMMENT '朋友id',
    `session`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'uuid，会话id',
    `status`     tinyint                                                       NOT NULL COMMENT '0为不显示，1为显示，2为删除',
    `created_at` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_user_friend`(`user_id` ASC, `friend_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;