package me.militch.quick.h5panel.event;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

public class H5PanelEventListener {
    private Context context;
    private boolean isOther;
    private boolean isOpen;
    public H5PanelEventListener(Context context) {
        this.context = context;
    }
    public void loadUrl(WebView webView, boolean isWebScheme, String scheme , String url){
        if (!isWebScheme&&context != null) {
            isOther = true;
            Uri uri = Uri.parse(url);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //跳转指定协议打开外部应用
                context.startActivity(intent);
                isOpen = true;
            } catch (ActivityNotFoundException exception) {
                //这个时候可以理解为用户没有安装指定的应用
                isOpen = false;
            }
        } else {
            isOther = false;
            webView.loadUrl(url);
        }
    }
    public void onDownload(String url){
        if (isOther && !isOpen && context != null) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else if (!isOther && !isOpen && context != null) {
            //普通协议，调用系统浏览器下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
    public void onLoading(int v){

    }
   public void onReceivedTitle(String title){

    }
    public void onOpenFile(boolean is, ValueCallback<Uri[]> valueCallback, ValueCallback<Uri> callback2){

    }
}
