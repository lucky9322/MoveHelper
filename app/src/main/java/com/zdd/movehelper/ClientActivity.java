package com.zdd.movehelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.zdd.movehelper.gameviews.ViewClient;
import com.zdd.movehelper.util.Constant;
import com.zdd.movehelper.widget.WaitingProgressDialog;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class ClientActivity extends AppCompatActivity {

    private WaitingProgressDialog progressDialog;
    public static Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    // 1代表要关闭activity
                    //2代表已经连接上服务器用户
                    case Constant.CLIENT_CONNECTING:
                        //finish();
                        progressDialog.show();
                        break;
                    case Constant.CLIENT_CONNECT_OK:
                        progressDialog.dismiss();
                        break;
                    case Constant.CLIENT_WIN:
                        Toast.makeText(ClientActivity.this, "我方获胜", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.CLIENT_FAIL:
                        Toast.makeText(ClientActivity.this, "我方失利", Toast.LENGTH_SHORT).show();
                        break;

                    case Constant.CLIENT_CONNECT_ERROR:
                        finish();
                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };

        setContentView(new ViewClient(this));

        // 创建ProgressDialog对象
        progressDialog = new WaitingProgressDialog(
                this) {

            @Override
            public void onBackPressed() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = Constant.CLIENT_CONNECT_ERROR;
                handler.sendMessage(msg);
                super.onBackPressed();
            }

        };
        progressDialog.setMessage("正在寻找主机");


    }
}
