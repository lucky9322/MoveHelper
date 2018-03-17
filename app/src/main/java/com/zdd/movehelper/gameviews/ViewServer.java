package com.zdd.movehelper.gameviews;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zdd.movehelper.R;
import com.zdd.movehelper.ServerActivity;
import com.zdd.movehelper.util.AssetsLoad;
import com.zdd.movehelper.util.Constant;
import com.zdd.movehelper.util.StringDealer;
import com.zdd.movehelper.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class ViewServer extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private static final String TAG = "MyGameViewServer";


    private boolean isExit = true;
    private boolean isMyTurn = false;

    //    蓝牙组件
    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;

    private SurfaceHolder sfh;
    private Canvas canvas;
    private Thread th = new Thread(this);
    private boolean isStop = false;
    private Paint paint;
    private Resources res;
    private Bitmap whiteMap;
    private Bitmap blackMap;
    private Bitmap woodBackground;
    //    0 没有胜利，1 我放胜利，2 对方胜利
    private int whoWin = 0;

    public ViewServer(Context context) {
        super(context);
        isExit = false;
        sfh = this.getHolder();
        sfh.addCallback(this);
        setFocusable(true);
        res = getResources();
        whiteMap = BitmapFactory.decodeResource(res, R.drawable.human);
        blackMap = BitmapFactory.decodeResource(res, R.drawable.ai);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        woodBackground = BitmapFactory.decodeResource(res, R.drawable.wood_background);

        initBluetooth();
    }

    private void initBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        AcceptThread serverThread = new AcceptThread();
        serverThread.start();
    }

    @Override
    public void run() {

        while (!isExit) {
            draw();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        // TODO: 2018/3/17 java.lang.NullPointerException: Attempt to invoke virtual method 'boolean android.graphics.Bitmap.isRecycled()' on a null object reference lucky
        try {
            canvas = sfh.lockCanvas();
//        背景图
            canvas.drawBitmap(woodBackground, null, new RectF(0, 0, Constant.SCREENWIDTH, Constant.SCREENHEIGHT), null);
//        九宫格 横线
            for (int i = 0; i < 9; i++) {
                canvas.drawLine(2 * Constant.RECT_R, 2 * Constant.RECT_R * i + 2 * Constant.RECT_R,
                        9 * 2 * Constant.RECT_R, 2 * Constant.RECT_R * i + 2 * Constant.RECT_R, paint);
            }
            for (int j = 0; j < 9; j++) {
                canvas.drawLine(2 * Constant.RECT_R + 2 * Constant.RECT_R * j, 2 * Constant.RECT_R,
                        2 * Constant.RECT_R + 2 * Constant.RECT_R * j, 9 * 2 * Constant.RECT_R, paint);
            }
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (Constant.ground[y][x] != 0) {
                        drawMyBitmap(x, y);
                    }
                }
            }

            if (whoWin == 0) { //没有人获胜 继续判断该谁落子了
                if (isMyTurn) {
                    canvas.drawBitmap(AssetsLoad.picMyTurn, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);
                } else {
                    canvas.drawBitmap(AssetsLoad.picEnemyTurn, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);
                }
            } else {//如果有人获胜，继续判断是那方获胜
                if (whoWin == 1) {
                    canvas.drawBitmap(AssetsLoad.picIWin, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);
                } else {
                    canvas.drawBitmap(AssetsLoad.picEnemyWin, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != canvas) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }

    //以某点为中心，画图片上去
    private void drawMyBitmap(int x, int y) {
        if (Constant.ground[y][x] == Constant.WHITE_CHESS)
            canvas.drawBitmap(whiteMap, ((x + 1) * 2 * Constant.RECT_R) - Constant.CHESS_R, ((y + 1) * 2 * Constant.RECT_R) - Constant.CHESS_R, null);
        else {
            canvas.drawBitmap(blackMap, ((x + 1) * 2 * Constant.RECT_R) - Constant.CHESS_R, ((y + 1) * 2 * Constant.RECT_R) - Constant.CHESS_R, null);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        th.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isExit = true;
        Utils.initGroup();
        try {
            serverSocket.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isStop = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isStop && isMyTurn) {
                for (int y = 0; y < 9; y++) {
                    for (int x = 0; x < 9; x++) {
                        if (Constant.ground[x][y] == 0 && isInCircle(event.getX(), event.getY(), x, y)) {
                            Constant.ground[y][x] = Constant.WHITE_CHESS;
                            AssetsLoad.playSound(getContext(), AssetsLoad.putSoundId);
                            if (Utils.isWin(x, y)) {
                                Message msg4 = ServerActivity.handler.obtainMessage(Constant.SERVER_WIN);
                                ServerActivity.handler.sendMessage(msg4);
                                whoWin = 1;
                                new MWriteThread(new int[]{y, x, Constant.ENEMYWIN}).start();
                            } else {
                                new MWriteThread(new int[]{y, x,
                                        Constant.ENEMYNOTWIN}).start();
                            }
                            isMyTurn = false;
                            draw();
                            return super.onTouchEvent(event);
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    // 判断是否与某点最近
    private boolean isInCircle(float touch_x, float touch_y, int x, int y) {

        return ((touch_x - ((x + 1) * 2 * Constant.RECT_R))
                * (touch_x - ((x + 1) * 2 * Constant.RECT_R)) + (touch_y - ((y + 1) * 2 * Constant.RECT_R))
                * (touch_y - ((y + 1) * 2 * Constant.RECT_R))) < Constant.RECT_R
                * Constant.RECT_R;
    }

    //服务器 端的线程  等待连接
    private class AcceptThread extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("fiveChess", Constant.uuid);
                Log.i(TAG, "run: 等待连接");
                Message msg1 = new Message();
                msg1.what = Constant.SERVER_CONNECTING;
                ServerActivity.handler.sendMessage(msg1);
                socket = serverSocket.accept();
                Message msg2 = ServerActivity.handler.obtainMessage(Constant.SERVER_CONNECT_OK);
                ServerActivity.handler.sendMessage(msg2);
                Log.i(TAG, "run: 已经连接上le");
                new MReadThread().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 读取操作线程
    private class MReadThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while ((bytes = mmInStream.read(buffer)) > 0) {
                    byte[] bufData = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        bufData[i] = buffer[i];
                    }
                    String s = new String(bufData);
                    int[] data = StringDealer.getDataFromString(s);
                    if (data[0] != -1) {
                        Constant.ground[data[0]][data[1]] = Constant.BLACK_CHESS;
                        AssetsLoad.playSound(getContext(), AssetsLoad.putSoundId);
                        if (data[2] == Constant.ENEMYWIN) {
                            isStop = true;

                            whoWin = 2;
                            Message msg3 = ServerActivity.handler.obtainMessage(Constant.SERVER_FAIL);
                            ServerActivity.handler.sendMessage(msg3);
                        } else {
                            isMyTurn = true;
                        }
                    }

                    Log.i(TAG, "run: " + s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MWriteThread extends Thread {
        private String data;

        public MWriteThread(int[] buf) {
            if (buf == null) {
                data = "-1,-1,-1";
            }
            data = StringDealer.getStringFromData(buf);
        }

        @Override
        public void run() {
            OutputStream mmOutStream = null;

            try {
                mmOutStream = socket.getOutputStream();
                mmOutStream.write(data.getBytes());
                Log.i(TAG, "run: 已经发送");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
