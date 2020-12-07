package com.example.appjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
//        startActivity(intent);
        //从下一个Activity返回时，可以带回一些数据到这个Activity
        startActivityForResult(intent, 1);
    }
    // 隐式Intent的用法
    public void sendMessage2(View view) {
        Intent intent = new Intent("com.example.appjava.PPACTION");
        // android.intent.category.DEFAULT 是一种默认的category,要么不写，要么添加下面的Category跟AndroidManifest.xml对应
        intent.addCategory("com.example.appjava.PPCATEGORY");
        // Verify that the intent will resolve to an activity
        //防止闪退： https://developer.android.com/guide/components/intents-filters#ExampleSend
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("data_return");
                    Log.d("FirstActivity", returnedData);
                }
                break;
            default:
        }
    }

//    弹出Toast
    public void toastMessage(String msg) {
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        启动时调用，先创建好弹出菜单
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        点击菜单某个选项时调用
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add_item:
                Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this, "You clicked Remove", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

}