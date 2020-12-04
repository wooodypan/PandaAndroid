package com.example.appjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

}