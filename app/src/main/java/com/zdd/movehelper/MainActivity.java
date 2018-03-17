package com.zdd.movehelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zdd.movehelper.util.AssetsLoad;
import com.zdd.movehelper.util.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.listview_devices)
    ListView listviewDevices;
    @BindView(R.id.button_create_room)
    Button buttonCreateRoom;
    @BindView(R.id.button_search_room)
    Button buttonSearchRoom;


    private ArrayAdapter listAdapter;
    private ArrayList<String> listData;//listview数据
    private ArrayList<String> listAddressData;//设备物理地址

    private BluetoothAdapter adapter;
    private BluetoothDevice device;

    private boolean flag = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "开始搜索");
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "onReceive: 搜索结束");
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "device device" + device);
                    if (null != device) {
                        Log.i(TAG, "device name " + device.getName());
                    }
                    if (null != device && device.getName() != null) {
                        listData.add(device.getName());
                        listAddressData.add(device.getAddress());
                        // TODO: 2018/3/17 需要保证刷新在UI线程 lucky
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Toast.makeText(MainActivity.this, "蓝牙已开启，请重新尝试创建和连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constant.SCREENWIDTH = dm.widthPixels;
        Constant.SCREENHEIGHT = dm.heightPixels;// 高度

        // TODO Auto-generated method stub
        //初始化参数
        Constant.RECT_R = Constant.SCREENWIDTH / 20;
        Constant.CHESS_R = Constant.SCREENWIDTH * 3 / 80;
        AssetsLoad.load(getApplicationContext());


        adapter = BluetoothAdapter.getDefaultAdapter();
        listAddressData = new ArrayList<String>();
        listData = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listData);
        listviewDevices.setAdapter(listAdapter);
        listviewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Constant.address = listAddressData.get(position);
                if (device != null && Constant.address != null) {

                    Intent intent = new Intent();
                    intent.putExtra("viewType", 2);
                    // intent.putExtra("viewType", 3);
                    Constant.serverOrClient = false;
                    intent.setClass(getApplicationContext(),
                            TestClientActivity.class);
                    // intent.setClass(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });

//        注册广播
        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filterFound.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filterFound.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filterFound.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filterFound);

    }


    @OnClick({R.id.button_create_room, R.id.button_search_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_create_room:
                if (adapter.isEnabled()) {
                    Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoveryIntent, 1);
                    flag = true;
                } else {
                    adapter.enable();
                    Toast.makeText(this, "正在为您开启蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_search_room:
                // TODO Auto-generated method stub
                if (!adapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "正在为您开启蓝牙", Toast.LENGTH_SHORT).show();
                    // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
                    // 那么将会收到RESULT_OK的结果，
                    // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
                    /*Intent mIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(mIntent, 1);*/
                    // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
                    adapter.enable();
                    // mBluetoothAdapter.disable();//关闭蓝牙
                } else if (adapter.isEnabled()) {
                    initData();
                    adapter.startDiscovery();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        initData();
        super.onRestart();
    }

    private void initData() {
        listAddressData.removeAll(listAddressData);
        listData.removeAll(listData);
        Constant.address = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.i(TAG, "onActivityResult: requestCode " + requestCode);
            if (resultCode == 300) {
                Log.i(TAG, "onActivityResult: 服务端已打开");
                Intent intent = new Intent();
                intent.putExtra("viewType", 2);
                intent.setClass(getApplicationContext(), TestServerActivity.class);
                Constant.serverOrClient = true;
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
