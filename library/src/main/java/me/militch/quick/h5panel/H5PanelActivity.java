package me.militch.quick.h5panel;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public abstract class H5PanelActivity extends AppCompatActivity{
    private H5PanelFragment h5PanelFragment;
    protected abstract @LayoutRes int layout();
    protected abstract @IdRes int contentViewId();
    protected abstract void h5PanelAction(H5PanelFragment h5Panel);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        h5PanelFragment = H5PanelFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(contentViewId(),h5PanelFragment);
        ft.commit();
    }
    @Override
    public void onBackPressed() {
        if (h5PanelFragment.canGoBack()) {
            h5PanelFragment.goBack();
        } else {
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        h5PanelAction(h5PanelFragment);
    }
}
