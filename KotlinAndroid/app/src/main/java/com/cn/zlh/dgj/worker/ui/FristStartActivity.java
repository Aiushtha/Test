package com.cn.zlh.dgj.worker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cn.zlh.dgj.worker.BuildConfig;

public class FristStartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            startActivity(new Intent(this,Class.forName(BuildConfig.DefaultFristActivity)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
