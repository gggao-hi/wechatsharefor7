package com.ggg.wechatsend;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ggg
 * @version 1.0
 * @date 2019/1/19 15:52
 * @description
 */
public class EmptyActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Uri> uris = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        Intent intent = new Intent();
        //微信手动发朋友圈的界面
        intent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareScreenToTimeLineUI"));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uris.get(0), "image/*");
        Intent chooserIntent = Intent.createChooser(intent, "");
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfos != null && resolveInfos.size() == 1) {
            startActivity(chooserIntent);
        } else {
            intent = new Intent();
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uris.get(0), "image/*");
            chooserIntent = Intent.createChooser(intent, "");
            startActivity(chooserIntent);
        }
    }

}
