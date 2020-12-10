> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [juejin.cn](https://juejin.cn/post/6844903633142677511)

前一段时间看了一下 android 8 中的一些新特性，在组内分享了一下，想写下来分享一下。 android 8 之前通知是无法进行分类的，这样用户就无法对自己感兴趣和不感兴趣的通知进行分类处理，导致用户使用某些应用的时候，对一些推荐推销的通知深受折磨。为了进一步优化管理通知，Google 在发布 android 8 时对通知做了修改优化，出现了通知渠道功能。

### 什么是通知渠道（Notification Channels）

这里尝试给通知渠道简单下一个定义，每一个通知都属于一个通知渠道，开发者可以在 APP 中自由创建多个渠道，需要注意的时，通知渠道一旦创建就无法修改。

### 创建通知渠道

应用程序中创建通知渠道（Notification Channel）的步骤：

1. 通过构造方法 NotificationChannel(channelId, channelName, importance) 创建一个 NotificationChannel 对象

2. 通过 createNotificationChannel ( ) 来注册 NotificationChannel 一个对象

```
NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
String channelId = "some_channel_id";
CharSequence channelName = "Some Channel";
int importance = NotificationManager.IMPORTANCE_LOW;
NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
notificationChannel.enableLights(true);
notificationChannel.setLightColor(Color.RED);
notificationChannel.enableVibration(true);
notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
notificationManager.createNotificationChannel(notificationChannel);
复制代码
```

创建通知渠道需要三个参数

*   channelId 通知渠道的 ID 可以是任意的字符串，全局唯一就可以
*   channelName 通知渠道的名称，这个是用户可见的，开发者需要认真规划的命名
*   importance 通知渠道的重要等级，有一下几个等级，不过这个用户都是可以手动修改的 ![](https://user-gold-cdn.xitu.io/2018/7/4/16463503c4fd1063?imageView2/0/w/1280/h/960/format/webp/ignore-error/1) 其次我们可以通过使用通知渠道提供给我们的一些公共方法来操纵该通知渠道:
*   getId()—检索给定通道的 ID
*   enablellights() - 如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
*   setLightColor() - 如果我们确定通道支持通知灯，则允许使用传递一个 int 值，该值定义通知灯使用的颜色
*   enablementVisuration()—在设备上显示时，说明来自此通道的通知是否应振动
*   getImportance()—检索给定通知通道的重要性值
*   setSound()—提供一个 Uri，用于在通知发布到此频道时播放声音
*   getSound()—检索分配给此通知的声音
*   setGroup()—设置通知分配到的组
*   getGroup()—检索通知分配到的组
*   setBypassDnd()—设置通知是否应绕过 “请勿打扰” 模式(中断_筛选器_优先级值)
*   canBypassDnd() - 检索通知是否可以绕过 “请勿打扰” 模式
*   getName()—检索指定频道的用户可见名称
*   setLockScreenVisibility() - 设置是否应在锁定屏幕上显示来自此通道的通知
*   getlockscreendisibility() - 检索来自此通道的通知是否将显示在锁定屏幕上
*   getAudioAttributes()—检索已分配给相应通知通道的声音的音频属性
*   canShowBadge()—检索来自此通道的通知是否能够在启动器应用程序中显示为徽章 下面我们写个 demo，创建两个通知渠道，升级和私信。

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "upgrade";
            String channelName = "升级";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
            channelId = "compose";
            channelName = "私信";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    //创建通知渠道
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendUpgradeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "upgrade")
                .setContentTitle("升级")
                .setContentText("程序员终于下班了。。")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();
        manager.notify(100, notification);
    }

    public void sendComposeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "compose")
                .setContentTitle("私信")
                .setContentText("有人私信向你提出问题")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))

                .build();
        manager.notify(101, notification);
    }
}
复制代码
```

![](https://user-gold-cdn.xitu.io/2018/7/4/164636784e37fe65?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

### 管理通知渠道

之前我们说过，通知渠道一旦创建，控制权就在用户手中，如果有一个重要通知渠道被用户手动关闭了，我们就要提醒用户去手动打开该渠道。

getNotificationChannel() 方法可以获取指定的通知渠道对象，

getNotificationChannels() 可以获取所有通知对象的集合，保存在一个 list 中

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("upgrade");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                Toast.makeText(this, "升级通知不能关闭，请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
复制代码
```

到此通知渠道的简单使用介绍完毕， 参考

[shoewann0402.github.io/2018/01/08/…](https://shoewann0402.github.io/2018/01/08/about-android-o-notification-channels/) [developer.android.com/about/versi…](https://developer.android.com/about/versions/oreo/android-8.0.html?hl=zh-cn)