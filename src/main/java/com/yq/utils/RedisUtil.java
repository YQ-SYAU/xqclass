package com.yq.utils;

import com.yq.entity.dto.BType;

/**
 * 存储数据在redis中时，设置key的名称
 */
public class RedisUtil {
    public static String getKey(BType bType, Object bId) {
        return bType + ":" + bId;
    }
    public static String getReportKey(BType bType) {
        BType type = BType.LIKED_COURSE_REPORT;
        switch (bType) {
            case LIKED_COURSE:
                type = BType.LIKED_COURSE_REPORT;
                break;
            case ATTENTION_AUTH:
                type = BType.ATTENTION_AUTH_REPORT;
                break;
            case COLLECT_COURSE:
                type = BType.COLLECT_COURSE_REPORT;
                break;
        }
        return type + "";
    }
}
