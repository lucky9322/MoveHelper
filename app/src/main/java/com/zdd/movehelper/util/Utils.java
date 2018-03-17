package com.zdd.movehelper.util;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class Utils {
    // 检查是否有五子连起来
    public static boolean isWin(int x, int y) {
        if (isHFive(x, y, 5) || isVFive(x, y, 5) || isLTFive(x, y, 5)
                || isRTFive(x, y, 5))
            return true;
        return false;

    }

    // 横向是否五子连珠
    public static boolean isHFive(int x, int y, int mode) {
        int count = 1;
        for (int i = x; i < 8; i++) {
            if (Constant.ground[y][x] == Constant.ground[y][i + 1]) {
                count++;
            } else {
                break;
            }
        }
        for (int j = x; j > 0; j--) {
            if (Constant.ground[y][x] == Constant.ground[y][j - 1]) {
                count++;
            } else {
                break;
            }
        }
        if (count >= mode) {
            return true;
        }
        return false;
    }

    // 纵向是否五子连珠
    public static boolean isVFive(int x, int y, int mode) {
        int count = 1;
        for (int i = y; i < 8; i++) {
            if (Constant.ground[y][x] == Constant.ground[i + 1][x]) {
                count++;
            } else {
                break;
            }
        }
        for (int j = y; j > 0; j--) {
            if (Constant.ground[y][x] == Constant.ground[j - 1][x]) {
                count++;
            } else {
                break;
            }
        }
        if (count >= mode) {
            return true;
        }
        return false;
    }

    // 左上斜线五子连珠
    public static  boolean isLTFive(int x, int y, int mode) {
        int count = 1;
        for (int i = 1; Math.min(x, y) - i >= 0; i++) {
            if (Constant.ground[y][x] == Constant.ground[y - i][x - i]) {
                count++;
            } else {
                break;
            }
        }
        for (int j = 1; Math.max(x, y) + j < 9; j++) {
            if (Constant.ground[y][x] == Constant.ground[y + j][x + j]) {
                count++;
            } else {
                break;
            }
        }
        if (count >= mode) {
            return true;
        }
        return false;
    }

    // 右上斜线五子连珠
    public static boolean isRTFive(int x, int y, int mode) {
        int count = 1;
        for (int i = 1; x + i < 9 && y - i >= 0; i++) {
            if (Constant.ground[y][x] == Constant.ground[y - i][x + i]) {
                count++;
            } else {
                break;
            }
        }
        for (int j = 1; x - j >= 0 && y + j < 9; j++) {
            if (Constant.ground[y][x] == Constant.ground[y + j][x - j]) {
                count++;
            } else {
                break;
            }
        }
        if (count >= mode) {
            return true;
        }
        return false;
    }

    public static void initGroup(){
        int length =  Constant.ground.length;
        for (int i = 0; i < length; i++) {
            for(int j = 0;j < length;j++){
                Constant.ground[i][j] = 0;
            }
        }

    }
}
