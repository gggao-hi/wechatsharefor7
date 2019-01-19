package com.ggg.wechatsend;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button send = findViewById(R.id.btn_send);
        textView = findViewById(R.id.textView);
        getCode();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        "send" +
                        ".jpg");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo2);
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    callSystemShare(file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wx88888888";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
    }

    public void callSystemShare(String imgUrl) {
        Intent intent = new Intent();
        /*
        *  //微信手动发朋友圈的界面
        intent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareScreenToTimeLineUI"));
                */
        ArrayList<Uri> uris = new ArrayList<>();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getImageContentUri(this, new File(imgUrl));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } else {
            uri = Uri.fromFile(new File(imgUrl));
        }
        uris.add(uri);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_STREAM, uris);
        intent.setDataAndType(uri, "image/*");
        getShareActivities(intent);
        Intent chooserIntent = Intent.createChooser(intent, "");
        startActivity(chooserIntent);
    }

    private void getShareActivities(Intent sharingIntent) {

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);
        for (ResolveInfo resolveInfo : activityList) {
            Log.d("xxx", String.format("%s--%d", resolveInfo.activityInfo.name, resolveInfo.priority));
        }

    }

    private Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }


    private void getCode() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo info : apps) {
            if (info.packageName.equals("com.tencent.mm")) {
                try {
                    PackageInfo packageInfo = pm.getPackageInfo("com.tencent.mm", 0);
                    textView.setText(String.format(Locale.CHINA, "wechat:versionName::%s;;versionCode::%d",
                            packageInfo.versionName, packageInfo.versionCode));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
