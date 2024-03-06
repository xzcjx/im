CREATE TABLE `im_friend_relationship_info`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `user_id`    int(11) NOT NULL COMMENT '用户id',
    `friend_id`  int(11) NOT NULL COMMENT '好友id',
    `read`       tinyint(4) NOT NULL COMMENT '是否已读',
    `status`     tinyint(4) NOT NULL COMMENT '关系的状态：0表示未处理，1表示同意，2表示拒接，3表示忽略，4表示发起好友申请,等待验证',
    `ps`         varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '添加好友时附言',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted_at` timestamp NULL DEFAULT NULL COMMENT '逻辑删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;