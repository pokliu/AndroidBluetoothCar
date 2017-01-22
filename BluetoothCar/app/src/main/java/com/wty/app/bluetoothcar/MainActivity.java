package com.wty.app.bluetoothcar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wty.app.bluetoothcar.bluetooth.BluetoothChatService;
import com.wty.app.bluetoothcar.bluetooth.DeviceListActivity;
import com.wty.app.bluetoothcar.utils.PreferenceUtil;

import com.wty.app.bluetoothcar.pokau.VerticalSeekBar;

import java.security.Timestamp;

import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.DEVICE_NAME;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_DEVICE_NAME;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_READ;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_STATE_CHANGE;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_TOAST;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_WRITE;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.TOAST;

public class MainActivity extends AppCompatActivity {

    private ImageButton btngo,btnstop,btnleft,btnright,btnback,settings;
    private VerticalSeekBar seekBar;
    TextView tv_setting;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings=(ImageButton)findViewById(R.id.settings);
        //control Button
        btngo = (ImageButton) findViewById(R.id.btngo);
        btnleft = (ImageButton) findViewById(R.id.btnleft);
        btnright = (ImageButton) findViewById(R.id.btnright);
        btnstop = (ImageButton) findViewById(R.id.btnstop);
        btnback = (ImageButton) findViewById(R.id.btnback);
        tv_setting = (TextView) findViewById(R.id.tv_setting);

        seekBar = (VerticalSeekBar) findViewById(R.id.seekBar);

        float startSpeed = 200;

        seekBar.setProgress((int)((startSpeed/255)*100));

        sendMessage(String.valueOf(startSpeed));             //初始速度是200

        initListener();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{

            if(!mBluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
            }else{
                // Initialize the BluetoothChatService to perform bluetooth connections
                mChatService = new BluetoothChatService(this, mHandler);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null && mBluetoothAdapter.isEnabled()) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    if(mChatService == null){
                        mChatService = new BluetoothChatService(this, mHandler);
                        mChatService.start();
                    }
                    mChatService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bt_enabled_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * @Decription 初始化各个按钮效果
     **/
    private void initListener(){
        btngo.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        btngo.setBackgroundResource(R.mipmap.up_press);
                        sendMessage(PreferenceUtil.getInstance().getUpCode());
                        break;

                    case MotionEvent.ACTION_UP:
                        btngo.setBackgroundResource(R.mipmap.up);
                        sendMessage(PreferenceUtil.getInstance().getBlurCode());
                        break;
                }
                return true;
            }


        });
        btnleft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        btnleft.setBackgroundResource(R.mipmap.left_press);
                        sendMessage(PreferenceUtil.getInstance().getLeftCode());
                        break;
                    case MotionEvent.ACTION_UP:
                        btnleft.setBackgroundResource(R.mipmap.left);
                        sendMessage(PreferenceUtil.getInstance().getBlurCode());
                        break;
                }
                return true;
            }


        });
        btnright.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        btnright.setBackgroundResource(R.mipmap.right_press);
                        sendMessage(PreferenceUtil.getInstance().getRightCode());
                        break;

                    case MotionEvent.ACTION_UP:
                        btnright.setBackgroundResource(R.mipmap.right);
                        sendMessage(PreferenceUtil.getInstance().getBlurCode());
                        break;
                }
                return true;
            }


        });
        btnback.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        btnback.setBackgroundResource(R.mipmap.back_press);
                        sendMessage(PreferenceUtil.getInstance().getDownCode());
                        break;

                    case MotionEvent.ACTION_UP:
                        btnback.setBackgroundResource(R.mipmap.back);
                        sendMessage(PreferenceUtil.getInstance().getBlurCode());
                        break;
                }
                return true;
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PreferenceUtil.getInstance().getStopCode());
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, CodeSetttingActivity.class);
                startActivity(serverIntent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            int speed = 0;
            long lastTime = System.currentTimeMillis();
            Handler handler = new Handler();
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    lastTime = System.currentTimeMillis();
                    sendMessage(String.valueOf(speed));
                }
            };
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                speed = (int) (2.55 * progress);
                if(System.currentTimeMillis() - lastTime < 100){
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(runnable,100);
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onrequestDisallowInterceptTouchEvent(boolean enable) {

            }
        });
    }

    /**
     * Sends a message.
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService == null ||(mChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            Log.d("SendMsg", message);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), "正在连接该蓝牙设备", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    String result = "";
                    if(writeMessage.equals(PreferenceUtil.getInstance().getStopCode())){
                        result = "停车";
                    }else if(writeMessage.equals(PreferenceUtil.getInstance().getLeftCode())){
                        result = "左转";
                    }else if(writeMessage.equals(PreferenceUtil.getInstance().getRightCode())){
                        result = "右转";
                    }else if(writeMessage.equals(PreferenceUtil.getInstance().getUpCode())){
                        result = "前进";
                    }else if(writeMessage.equals(PreferenceUtil.getInstance().getDownCode())){
                        result = "后退";
                    }
                    Log.d("蓝牙小车:",result);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "连接上 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
