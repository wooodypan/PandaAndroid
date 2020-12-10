package com.example.appjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {
    private static String[] PERMISSIONS_STORAGE = {
            //下载只需要写权限
//            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DownloadService.DownloadBinder downloadBinder;
    //001 首先创建了一个ServiceConnection 的匿名类，
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //002 然后在onServiceConnected() 方法中获取到 DownloadBinder的实例，有了这个实例，我们就可以在活动中调用服务提供的各种方法了
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //003 在这里我们对各个按钮都进行了初始化操作并设置了点击事件
        Button startDownload = (Button) findViewById(R.id.start_download);
        Button pauseDownload = (Button) findViewById(R.id.pause_download);
        Button cancelDownload = (Button) findViewById(R.id.cancel_download);
        startDownload.setOnClickListener(this);
        pauseDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);

        //004 然后分别调用了startService() 和bindService() 方法来启动和绑定服务。
        // 这一点至关重要，因为启动服务可以保证DownloadService一直在后台运行，绑定服务则可以让MainActivity和DownloadService进行通信，因此两个方法调用都必不可少。
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
        //005 申请WRITE_EXTERNAL_STORAGE 的运行时权限，因为下载文件是要下载到SD卡的Download目录下的，如果没有这个权限的话，我们整个程序都无法正常工作。
        if (ContextCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DownloadActivity.this, PERMISSIONS_STORAGE, 1);
        }
    }

    @Override
    public void onClick(View v) {
        //对点击事件进行判断
        if (downloadBinder == null) {
            return;
        }
        switch (v.getId()) {
            // 点击了开始按钮
            case R.id.start_download:
//                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                String url = "https://p.agolddata.com/l/111-000.ts";
                downloadBinder.startDownload(url);
                break;
            // 点击了暂停按钮
            case R.id.pause_download:
                downloadBinder.pauseDownload();
                break;
            // 点击了取消按钮
            case R.id.cancel_download:
                downloadBinder.cancelDownload();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        //注意，如果活动被销毁了，那么一定要记得对服务进行解绑，不然就有可能会造成内存泄漏。
        // 这里我们在onDestroy() 方法中完成了解绑操作。
        super.onDestroy();
        unbindService(connection);
    }
}