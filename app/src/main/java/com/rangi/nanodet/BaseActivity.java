package com.rangi.nanodet;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {
    private TextView mTvtitler;
    private ImageView mIvback;
    private ImageView mIvuser;

    @SuppressLint("WrongConstant")
    public void initNavBar(boolean back, String title, boolean user){
        Log.d("log", title+back+user);

        mIvback = findViewById(R.id.cc);
        mTvtitler = findViewById(R.id.tv_title);
        mIvuser = findViewById(R.id.iv_change);
        mIvback.setVisibility(back?View.VISIBLE:View.GONE);
        mTvtitler.setText(title);
        mIvuser.setVisibility(user?View.VISIBLE:View.GONE);
        mIvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回
                onBackPressed();
            }
        });
        //个人用户
        mIvuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, homeActivity.class);
                startActivity(intent);
            }
        });

    }
}
