package com.example.appjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        // 获取启动此activity的Intent并提取字符串
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //捕获TextView的布局并将字符串设置为其文本
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }

    // https://stackoverflow.com/a/24880547
    // You could add android:textAllCaps="false" to the button.
    public void finishActivity(View view) {
        finish();
    }
    //调用系统的浏览器来打开网页
    public void implicit_intents2(View view) {
        //这里我们首先指定了Intent的action 是Intent.ACTION_VIEW ，这是一个Android系统内置的动作，其常量值为android.intent.action.VIEW
        //再调用Intent的setData() 方法将这个Uri 对象传递进去。
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //然后通过Uri.parse() 方法，将一个网址字符串解析成一个Uri 对象
        intent.setData(Uri.parse("https://www.baidu.com"));
        startActivity(intent);
    }
}