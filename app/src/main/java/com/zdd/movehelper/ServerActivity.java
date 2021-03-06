package com.zdd.movehelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.zdd.movehelper.gameviews.ViewServer;
import com.zdd.movehelper.util.Constant;
import com.zdd.movehelper.widget.WaitingProgressDialog;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class ServerActivity extends AppCompatActivity {
    private WaitingProgressDialog progressDialog;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    // 1代表要关闭activity
                    //2代表已经连接上服务器用户
                    case Constant.SERVER_CONNECTING:
                        //finish();
                        progressDialog.show();
                        break;
                    case Constant.SERVER_CONNECT_OK:
                        progressDialog.dismiss();
                        break;
                    case Constant.SERVER_WIN:
                        Toast.makeText(ServerActivity.this, "我方获胜", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.SERVER_FAIL:
                        Toast.makeText(ServerActivity.this, "我方失利", Toast.LENGTH_SHORT).show();
                        break;

                    case Constant.SERVER_CONNECT_ERROR:
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };

        setContentView(new ViewServer(this));

        progressDialog = new WaitingProgressDialog(
                this) {

            @Override
            public void onBackPressed() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = Constant.SERVER_CONNECT_ERROR;
                handler.sendMessage(msg);
                super.onBackPressed();
            }

        };
        progressDialog.setMessage("正在等待连接");
    }
}
