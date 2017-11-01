package me.militch.quick.h5panel.conf.impl;

import android.net.Uri;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import me.militch.quick.h5panel.event.H5PanelEventListener;

public class WebViewClientImpl extends WebViewClient{
    private H5PanelEventListener listener;
    public WebViewClientImpl(H5PanelEventListener listener) {
        this.listener = listener;
    }
    //解决HTTPS链接问题
    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        sslErrorHandler.proceed();
    }


    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if((scheme.equals("http")||scheme.equals("https"))&&listener!=null){
            if(url.startsWith("http://a.app.qq.com")){
                if(view.canGoBack()){
                    view.goBack();
                }
                listener.loadUrl(view,false,scheme,url);
                return true;
            }
            listener.loadUrl(view,true,scheme,url);
            return false;
        }else if(listener != null){
            if(view.canGoBack()){
                view.goBack();
            }
            listener.loadUrl(view,false,scheme,url);
        }
        return true;
    }
}
