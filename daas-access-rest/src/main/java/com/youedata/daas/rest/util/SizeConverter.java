package com.youedata.daas.rest.util;

/**
 * 数据存储量单位换算
 */
public class SizeConverter {
    // 单位转换
    private static final String[] UNITS = new String[] {
        "B", "KB", "MB", "GB", "TB", "PB", "**"
    };
    private static final int LAST_IDX = UNITS.length-1;
    private static final String FORMAT_F_UNIT = "%1$-1.2f%2$s";

    public static String fromatStoredSize(double size) {
        int unitIdx = 0;
        while (1024 <= size) {
            unitIdx++;
            size /= 1024;
        }
        int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
        return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
    }
}
