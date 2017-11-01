package me.militch.quick.h5panel.conf.impl;

import android.net.Uri;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import me.militch.quick.h5panel.event.H5PanelEventListener;

public class WebChromeClientImpl extends WebChromeClient {
    private H5PanelEventListener listener;
    public WebChromeClientImpl(H5PanelEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if(listener != null){
            listener.onLoading(newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if(listener != null){
            listener.onReceivedTitle(title);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
        geolocationPermissionsCallback.invoke(s, true, false);
        super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        if(listener!=null){
            listener.onOpenFile(true,valueCallback,null);
        }
        return true;
    }
    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if(listener != null){
            listener.onOpenFile(false,null,uploadMsg);
        }
    }
}
