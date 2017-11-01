package me.militch.quick.h5panel.example;

import android.app.Application;
import me.militch.quick.h5panel.H5Panel;

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        H5Panel.init(this);
    }
}
