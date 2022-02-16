package com.rangi.nanodet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rangi.nanodet.yzm.Code;

public class ForgetPwdActivity extends BaseActivity {

    //验证码部分
    String realCode;
    ImageView mshowCode;
    SQLiteDatabase db;
    //用户信息
    MyApp mMyData;
    RadioGroup rdg;
    int index;
    String sql;

    //控件

    EditText mLogin_name,mPhone,mNew_pwd1,mNew_pwd2,med_phoneCodes;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        initNavBar(true,"找回密码",false);

        //初始化控件
        mLogin_name = findViewById(R.id.Login_name);
        mPhone = findViewById(R.id.phone);
        mNew_pwd1 = findViewById(R.id.new_pwd1);
        mNew_pwd2 = findViewById(R.id.new_pwd2);
        med_phoneCodes = findViewById(R.id.ed_phoneCodes);
        mshowCode = findViewById(R.id.showCode);
        mButton = findViewById(R.id.sure);
        rdg = findViewById(R.id.radioGroup);

//        //将验证码用图片的形式显示出来
        mshowCode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        //声明全局变量
        mMyData = (MyApp) getApplication();
        //获取数据库对象
        DatabaseHelper dbHelper = new DatabaseHelper(this, "User_db",null,1);
        db = dbHelper.getWritableDatabase();
        //获取数据库数据
        getData();
    }

    //点击确定按钮事件
    public void change_sure(View view) {
        //获取用户输入的名字，电话，两个密码
        String Login_name = mLogin_name.getText().toString();;
        String phone = mPhone.getText().toString();;
        String new_pwd1 = mNew_pwd1.getText().toString();;
        String new_pwd2 = mNew_pwd2.getText().toString();;
        String phoneCodes = med_phoneCodes.getText().toString().toString().toLowerCase();
        String name = mMyData.getName();
        String number = mMyData.getNumber();

        //for循环找到用户选的角色
        for (int i = 0; i < rdg.getChildCount(); i++) {
            RadioButton rd = (RadioButton) rdg.getChildAt(i);
            if (rd.isChecked()) {
                index = i;
                System.out.println("你选择的是"+rd.getId()+":"+rd.getText());
                break;
            }
        }
        //判断输入信息是否为空
        if(Login_name.equals("") || phone.equals("")|| new_pwd1.equals("")|| new_pwd2.equals("")||TextUtils.isEmpty(phoneCodes)){
            Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"填写信息不能为空",Toast.LENGTH_SHORT).show();
        }else {
            System.out.println("输入账号="+Login_name+":"+phone+":"+new_pwd1+":"+new_pwd2+":"+index);
            if(!(phoneCodes.equals(realCode))){
                Toast.makeText(this, "验证码不正确，请重新输入！", Toast.LENGTH_SHORT).show();

            }else if(new_pwd1.equals(new_pwd2)){

                //更新数据
                if(index == 0){
                    sql = "select * from consumer where username = '"+Login_name+"' and number = '"+phone+"'";
                    if(ctrInfo(Login_name,phone)){
                        String sql = "update consumer set pwd = '"+new_pwd1+ "' where username = '" + Login_name + "'";
                        db.execSQL(sql);
                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"用户名或手机号错误",Toast.LENGTH_SHORT).show();
                    }
                }else if(index == 1){
                    sql = "select * from designer where username = '"+Login_name+"' and number = '"+phone+"'";
                    if(ctrInfo(Login_name,phone)){
                        String sql1 = "update designer set pwd = '"+new_pwd1+ "' where username = '" + Login_name + "'";
                        db.execSQL(sql1);
                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"用户名或手机号错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }else{

                Toast.makeText(com.rangi.nanodet.ForgetPwdActivity.this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //数据库信息比对
    public boolean ctrInfo(String name,String number){
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount() == 0){
            return false;
        }else {
            //存储用户信息到全局变量
            mMyData = (MyApp) getApplication();
            mMyData.setName(name);
            mMyData.setNumber(number);
            mMyData.setIndex(index);
            return true;
        }
    }

    public void sel_consumer(){
        Cursor cursor = db.rawQuery("select * from consumer",null);
        System.out.println("查询个人消费者信息：");
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            System.out.println(id+"-"+name+"-"+pwd+"-"+number);
        }

    }
    public void sel_designer(){
        Cursor cursor = db.rawQuery("select * from designer",null);
        System.out.println("查询形象设计师信息：");
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            System.out.println(id+"-"+name+"-"+pwd+"-"+number);
        }

    }
    public void getData(){
        sel_consumer();
        sel_designer();
        //sel_company();
    }

    public void code_onClick(View view) {
        switch (view.getId()) {
            case R.id.showCode:    //改变随机验证码的生成
                mshowCode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
        }
    }
}