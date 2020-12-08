package com.example.appjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
// 注意 implements View.OnClickListener ！！！
public class LinearLayoutActivity extends AppCompatActivity implements View.OnClickListener {

    private PandaService.DownloadBinder downloadBinder;
    //重写了onServiceConnected() 方法和onServiceDisconnected() 方法，
    // 这两个方法分别会在活动与服务成功绑定以及活动与服务的连接断开的时候调用
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (PandaService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout);
        Button startService = (Button) findViewById(R.id.start_service);
        Button stopService = (Button) findViewById(R.id.stop_service);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        // 解绑、绑定服务
        Button bindService = (Button) findViewById(R.id.bind_service);
        Button unbindService = (Button) findViewById(R.id.unbind_service);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);

        Button startIntentService = (Button) findViewById(R.id.start_intent_service);
        startIntentService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Intent startIntent = new Intent(this, PandaService.class);
                startService(startIntent); // 启动服务
                break;
            case R.id.stop_service:
                Intent stopIntent = new Intent(this, PandaService.class);
                stopService(stopIntent); // 停止服务
                break;

            case R.id.bind_service:
                Intent bindIntent = new Intent(this, PandaService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
                break;
            case R.id.unbind_service:
                unbindService(connection); // 解绑服务
                break;

            case R.id.start_intent_service:
                // 打印主线程的id
                Log.d("===", "Thread id is " + Thread.currentThread().
                        getId());
                Intent intentService = new Intent(this, PandaIntentService.class);
                startService(intentService);
                break;
            default:
                break;
        }
    }


}