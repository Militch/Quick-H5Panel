package me.militch.quick.h5panel.example;

import me.militch.quick.h5panel.H5PanelActivity;
import me.militch.quick.h5panel.H5PanelFragment;

public class MainActivity extends H5PanelActivity{
    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected int contentViewId() {
        return R.id.main_content;
    }

    @Override
    protected void h5PanelAction(H5PanelFragment h5Panel) {
        h5Panel.loadUrl("http://218.70.11.118:1024/test");
    }
}
