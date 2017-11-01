package me.militch.quick.h5panel;

import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;

import me.militch.quick.h5panel.util.LogUtil;

public final class H5Panel implements QbSdk.PreInitCallback {
    public static void init(Context context){
        QbSdk.initX5Environment(context,new H5Panel());
    }

    @Override
    public void onCoreInitFinished() {
        LogUtil.d(getClass(),"X5内核初始化完成");
    }

    @Override
    public void onViewInitFinished(boolean b) {
        LogUtil.d(getClass(),"X5内核-加载完成?%s",b);
    }
}
