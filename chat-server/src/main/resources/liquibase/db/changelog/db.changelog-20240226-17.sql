CREATE TABLE `im_user`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `email`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户手机号',
    `username`      char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '用户昵称',
    `account`       char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '开始是自动生成，可以自定义，但不能重复',
    `password_hash` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户hash过后的密码',
    `status`        tinyint(4) NOT NULL DEFAULT 1 COMMENT '账号状态，0为封禁，1为正常',
    `delete_time`   timestamp NULL DEFAULT NULL COMMENT '删除时间',
    `create_at`     timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_phone_del`(`email`, `delete_time`) USING BTREE,
    UNIQUE INDEX `unique_account_del`(`account`, `delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;