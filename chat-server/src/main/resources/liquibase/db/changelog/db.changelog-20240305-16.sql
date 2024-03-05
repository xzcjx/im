CREATE TABLE `im_friend_relationship`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `user_id`    int(11) NOT NULL COMMENT '用户id',
    `friend_id`  int(11) NOT NULL COMMENT '好友id',
    `note`       char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '好友备注',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted_at` timestamp NULL DEFAULT NULL COMMENT '逻辑删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_relationship`(`user_id`, `friend_id`, `deleted_at`) USING BTREE COMMENT '由于是逻辑删除，把删除时间加入进来，解决唯一性问题'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;