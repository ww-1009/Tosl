package com.rangi.nanodet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;

public class homeActivity extends BaseActivity {
    Button mChange;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initNavBar(true,"个人中心",false);
        initView();
    }

    public void initView(){
        mChange = findViewById(R.id.change);
        logout = findViewById(R.id.logout);
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(homeActivity.this , ChangePwdActivity.class);
                //启动
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
