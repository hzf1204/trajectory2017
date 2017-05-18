package com.android.trail.com.adroid.huizhao.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.a20160316.trajectory.R;

/**
 * Created by Lenovo on 2016/12/1.
 */

public class JieShao1 extends Activity{
    private Button back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jie_shao_1);
//        StatusBarCompat.setStatusBarColor(this, Color.BLUE,255);
        back = (Button)findViewById(R.id.jshao1_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent();
                back.setClass(JieShao1.this,JieShaoActivity.class);
                startActivity(back);
                finish();
            }
        });
    }
}
