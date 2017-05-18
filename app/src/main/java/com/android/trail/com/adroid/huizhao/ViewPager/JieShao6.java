package com.android.trail.com.adroid.huizhao.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.a20160316.trajectory.R;

public class JieShao6 extends AppCompatActivity {

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_shao6);
//        StatusBarCompat.setStatusBarColor(this, Color.BLUE,255);
        back = (Button)findViewById(R.id.jshao6_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent();
                back.setClass(JieShao6.this,JieShaoActivity.class);
                startActivity(back);
                finish();
            }
        });
    }
}
