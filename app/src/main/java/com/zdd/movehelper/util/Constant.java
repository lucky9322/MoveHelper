package com.zdd.movehelper.util;

import java.util.UUID;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class Constant {

    // 白棋
    public final static int WHITE_CHESS = 1;
    // 黑棋
    public final static int BLACK_CHESS = 2;

    // 对方还未获胜
    public final static int ENEMYNOTWIN = 0;
    // 对方获胜
    public final static int ENEMYWIN = 1;
    // 格子长和高度
    public  static int RECT_R = 26;
    // 棋子长和高度
    public  static int CHESS_R = 18;
    // 时候是服务器端
    public static boolean serverOrClient = true;

    //屏幕宽度
    public  static int SCREENWIDTH;
    //屏幕高度
    public  static int SCREENHEIGHT;
    /**
     * 目标设备的蓝牙物理地址
     * **/
    public static String address ;
    public static String LogTag = "五子棋";
    /**
     * 用于蓝牙之间通信的表示uuid
     * **/
    public final static UUID uuid = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * 棋盘
     * **/
    public static int[][] ground = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

    /**
     * 客户端正在连接主机
     * **/
    public final static int CLIENT_CONNECTING = 1;
    /**
     * 客户端连接主机成功
     * **/
    public final static int CLIENT_CONNECT_OK = 2;
    /**
     * 客户端胜利
     * **/
    public final static int CLIENT_WIN = 3;
    /**
     * 客户端连接异常
     * **/
    public final static int CLIENT_CONNECT_ERROR = 4;
    /**
     * 客户端失败
     **/
    public final static int CLIENT_FAIL = 5;


    /**
     * 服务器正在等待连接
     * **/
    public final static int SERVER_CONNECTING = 1;
    /**
     * 服务器连接客户端成功
     * **/
    public final static int SERVER_CONNECT_OK = 2;
    /**
     * 服务器连接客户端异常
     * **/
    public final static int SERVER_CONNECT_ERROR = 3;
    /**
     * 服务器胜利
     * **/
    public final static int SERVER_WIN = 4;
    /**
     * 服务器失败
     **/
    public final static int SERVER_FAIL = 5;

}
