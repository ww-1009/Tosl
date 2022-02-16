package com.rangi.nanodet;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwdActivity extends BaseActivity {
    SQLiteDatabase db;
    //用户信息
    MyApp mMyData;
    //控件
    EditText mEdOldPwd,mEdNewPwd1,mEdNewPwd2;
    Button mChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        initNavBar(true,"修改密码",false);
        //声明全局变量
        mMyData = (MyApp) getApplication();
        //获取数据库对象
        DatabaseHelper dbHelper = new DatabaseHelper(this, "User_db",null,1);
        db = dbHelper.getWritableDatabase();
        initView();
    }
    public void initView(){
        mEdOldPwd = findViewById(R.id.ed_old_pwd);
        mEdNewPwd1 = findViewById(R.id.ed_new_pwd1);
        mEdNewPwd2 = findViewById(R.id.ed_new_pwd2);
        mChangeButton = findViewById(R.id.change_sure);
    }

    public void change_sure(View view) {
        String old_pwd = mEdOldPwd.getText().toString();
        String new_pwd1 = mEdNewPwd1.getText().toString();
        String new_pwd2 = mEdNewPwd2.getText().toString();
        String name = mMyData.getName();
        int index = mMyData.getIndex();
        if(old_pwd.equals("") || new_pwd1.equals("")|| new_pwd2.equals("")){
            Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
        }else if(!(new_pwd2.equals(new_pwd1))){
            Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
        }else if(old_pwd.equals(new_pwd1)){
            Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"旧密码和新密码相同",Toast.LENGTH_SHORT).show();
        }else if(old_pwd.equals(mMyData.getPwd())){
            //更新数据
            if(index == 0){
                String sql = "update consumer set pwd = '"+new_pwd1 + "' where username = '" + name + "'";
                db.execSQL(sql);
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"密码修改成功，请重新登录",Toast.LENGTH_SHORT).show();
            }else if(index == 1){
                String sql = "update designer set pwd = '"+new_pwd1 + "' where username = '" + name + "'";
                db.execSQL(sql);
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"密码修改成功，请重新登录",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(com.rangi.nanodet.ChangePwdActivity.this,"原有密码输入错误",Toast.LENGTH_SHORT).show();
        }
    }
}