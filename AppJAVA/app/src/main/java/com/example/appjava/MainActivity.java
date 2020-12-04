package com.example.appjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
//    1201-2
    public static final String EXTRA_MESSAGE = "com.example.appjava.MESSAGE";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
    }

//    1201-1 Called when the user taps the Send button
//    您可能会看到一条错误，因为 Android Studio 无法解析用作方法参数的 View 类。
//    要清除错误，请点击 View 声明，将光标置于其上，然后按 Alt+Enter（或在 Mac 上按 Option+Enter）进行快速修复。
//    如果出现一个菜单，请选择 Import class。
    public void sendMessage(View view) {
        // Do something in response to button
        Log.i(TAG,"hello panda");
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
//        将启动一个由 Intent 指定的 DisplayMessageActivity 实例
        startActivity(intent);
    }
//    弹出Toast
    public void toastMessage(String msg) {
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }
}