package com.zdd.movehelper.gameviews;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import com.zdd.movehelper.ClientActivity;
import com.zdd.movehelper.R;
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

public class ViewClient extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String TAG = "MyGameViewClient";

    private SurfaceHolder sfh;
    private Canvas canvas;
    private Thread th = new Thread(this);
    private boolean isStop = false;
    private Paint paint;
    private Resources res;
    private Bitmap whiteMap;
    private Bitmap blackMap;
    private Bitmap woodBackground;
    private int whoWin = 0;
    private boolean isExit = true;
    private boolean isMyTurn = true;

    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private BluetoothDevice device;

    public ViewClient(Context context) {
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

    /**
     * 初始化蓝牙涩北，开启客户端线程
     */
    private void initBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.cancelDiscovery();
        device = adapter.getRemoteDevice(Constant.address);

        ConnectThread clientThread = new ConnectThread();
        clientThread.start();
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
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isStop = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            if (!isStop && isMyTurn) {
                for (int y = 0; y < 9; y++) {
                    for (int x = 0; x < 9; x++) {
                        if (Constant.ground[y][x] == 0 && isInCircle(event.getX(), event.getY(), x, y)) {
                            Constant.ground[y][x] = Constant.BLACK_CHESS;
                            AssetsLoad.playSound(getContext(),
                                    AssetsLoad.putSoundId);
                            if (Utils.isWin(x, y)) {
                                Message msg3 = ClientActivity.handler.obtainMessage(Constant.CLIENT_WIN);
                                whoWin = 1;
                                ClientActivity.handler.sendMessage(msg3);
                                new MWriteThread(new int[]{y, x, Constant.ENEMYWIN}).start();
                            } else {
                                new MWriteThread(new int[]{y, x, Constant.ENEMYNOTWIN}).start();
                            }
                            isMyTurn = false;
                            draw();

                            return super.onTouchEvent(event);
                        }
                    }
                }
            }

        return super.onTouchEvent(event);
    }

    @Override
    public void run() {

        while (!isExit) {
            draw();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            canvas = sfh.lockCanvas();
            //canvas.drawBitmap(woodBackground,0,0,null);
            canvas.drawBitmap(woodBackground, null, new RectF(0, 0, Constant.SCREENWIDTH, Constant.SCREENHEIGHT), null);
            //横线
            for (int i = 0; i < 9; i++) {
                canvas.drawLine(2 * Constant.RECT_R, 2 * Constant.RECT_R * i + 2 * Constant.RECT_R, 9 * 2 * Constant.RECT_R, 2 * Constant.RECT_R * i + 2 * Constant.RECT_R, paint);
            }
            for (int j = 0; j < 9; j++) {
                canvas.drawLine(2 * Constant.RECT_R + 2 * Constant.RECT_R * j, 2 * Constant.RECT_R, 2 * Constant.RECT_R + 2 * Constant.RECT_R * j, 9 * 2 * Constant.RECT_R, paint);
            }
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (Constant.ground[y][x] != 0)
                        drawMyBitmap(x, y);
                    //这里加入判断是否有人获胜
                }
            }
            if (whoWin == 0) {

                if (isMyTurn)
                    canvas.drawBitmap(AssetsLoad.picMyTurn, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);
                else {
                    canvas.drawBitmap(AssetsLoad.picEnemyTurn, null, new RectF(
                            0, 10 * 2 * Constant.RECT_R, Constant.SCREENWIDTH,
                            Constant.SCREENHEIGHT), null);

                }
            } else {
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
            //	canvas.dra
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (canvas != null) {
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

    //判断是否与某点最近
    private boolean isInCircle(float touch_x, float touch_y, int x, int y) {
        return ((touch_x - ((x + 1) * 2 * Constant.RECT_R)) * (touch_x - ((x + 1) * 2 * Constant.RECT_R)) + (touch_y - ((y + 1) * 2 * Constant.RECT_R)) * (touch_y - ((y + 1) * 2 * Constant.RECT_R))) < Constant.RECT_R * Constant.RECT_R;
    }

    //    客户端的线程
    private class ConnectThread extends Thread {
        @Override
        public void run() {
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(Constant.uuid);
                if (null == ClientActivity.handler) {
                    Log.i(TAG, "run: ClientActivity.handler null null null");
                }
                Message msg1 = ClientActivity.handler.obtainMessage(Constant.CLIENT_CONNECTING);
                ClientActivity.handler.sendMessage(msg1);
                Log.i(TAG, "run: 正在创建客户端");
                socket.connect();
                Message msg2 = ClientActivity.handler.obtainMessage(Constant.CLIENT_CONNECT_OK);
                ClientActivity.handler.sendMessage(msg2);
                Log.i(TAG, "run: 已经连接上服务器");

                new MReadThread().start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //    读取操作线程
    private class MReadThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Log.v(Constant.LogTag, "receive" + s);
                        int[] data = StringDealer.getDataFromString(s);
                        if (data[0] != -1) {
                            Constant.ground[data[0]][data[1]] = Constant.WHITE_CHESS;
                            AssetsLoad.playSound(getContext(),
                                    AssetsLoad.putSoundId);
                            if (data[2] == Constant.ENEMYWIN) {
                                isStop = true;
                                Message msg4 = ClientActivity.handler.obtainMessage(Constant.CLIENT_FAIL);
                                ClientActivity.handler.sendMessage(msg4);
                                whoWin = 2;
                                //白棋获胜
                                //Toast.makeText(context, "白棋获胜", Toast.LENGTH_SHORT).show();
                            } else {
                                isMyTurn = true;
                            }
                        }
                        Log.v(Constant.LogTag, s);
                        System.out.println(s);
                        // MyGameView.ground =
                        // StringDealer.getGroupFromString(s);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    // 发送数据线程
    private class MWriteThread extends Thread {
        private String data;


        public MWriteThread(int[] buf) {
            super();
            if (buf == null) {
                data = "-1,-1,-1";
            }
            data = StringDealer.getStringFromData(buf);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            OutputStream mmOutStream = null;

            try {
                mmOutStream = socket.getOutputStream();
                mmOutStream.write(data.getBytes());
                Log.v(Constant.LogTag, data + "已经发送");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }
}
