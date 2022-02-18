package com.rangi.nanodet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.rangi.nanodet.person.personMsg;
import com.rangi.nanodet.organization.orgMsg;



public class LoginActivity extends BaseActivity {
    EditText etname,etpwd;
    Button loginb;
    SQLiteDatabase db;
    RadioGroup rdg;

    //标识符
    int index;
    String sql;
    MyApp mMyData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //控件初始化
        etname = findViewById(R.id.et_name);
        etpwd = findViewById(R.id.et_pwd);
        rdg = findViewById(R.id.radioGroup);
        //依靠DatabaseHelper带全部参数的构造函数创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this, "User_db",null,1);
        db = dbHelper.getWritableDatabase();
        //数据库执行插入命令
        ins();
        //跳转到注册页面
        TextView register_Text = (TextView) findViewById(R.id.register_Text);
        register_Text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent是一种运行时绑定（run-time binding）机制，它能在程序运行过程中连接两个不同的组件。
                //在存放资源代码的文件夹下下，
//                getConnection();
                Intent i = new Intent(LoginActivity.this , RegisterActivity.class);
                //启动
                startActivity(i);
            }
        });

        //跳转到找回密码页面
        TextView forget_password_Text = (TextView) findViewById(R.id.forget_password_Text);
        forget_password_Text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent是一种运行时绑定（run-time binding）机制，它能在程序运行过程中连接两个不同的组件。
                //在存放资源代码的文件夹下下，
                Intent i = new Intent(LoginActivity.this ,  ForgetPwdActivity.class);
                //启动
                startActivity(i);
            }
        });

    }

    public void LoginOnClick(View view){
        //获取用户输入账号
        String name = etname.getText().toString();
        String pwd = etpwd.getText().toString();
        //获取选择的角色
        for (int i = 0; i < rdg.getChildCount(); i++) {
            RadioButton rd = (RadioButton) rdg.getChildAt(i);
            if (rd.isChecked()) {
                index = i;
                System.out.println("你选择的是"+rd.getId()+":"+rd.getText());
                break;
            }
        }
        //判空
        if(name.equals("") || pwd.equals("")){
            Toast.makeText(this,"账号或密码为空",Toast.LENGTH_SHORT).show();
        }else {
            System.out.println("输入账号="+name+":"+pwd+":"+index);
            //判断账号的有效性
            if(index == 0){
                //个人
                sql = "select * from consumer where username = '"+name+"' and pwd = '"+pwd+"'";
                if(ctrInfo(name,pwd)){
                    Intent intent = new Intent(LoginActivity.this, personMsg.class);
                    startActivity(intent);
                    //跳转后销毁本页面
                    this.finish();
                }else {
                    System.out.println("查不到该个人账号");
                    Toast.makeText(this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                }

            }else if(index == 1){
                //机构
                sql = "select * from designer where username = '"+name+"' and pwd = '"+pwd+"'";
                if(ctrInfo(name,pwd)){
                    Intent intent = new Intent(LoginActivity.this, orgMsg.class);
                    startActivity(intent);
                    //跳转后销毁本页面
                    this.finish();
                }else {
                    System.out.println("查不到该机构账号");
                    Toast.makeText(this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //数据库信息比对
    public boolean ctrInfo(String name,String pwd){
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount() == 0){
            return false;
        }else {
            //存储用户信息到全局变量
            mMyData = (MyApp) getApplication();
            mMyData.setName(name);
            mMyData.setPwd(pwd);
            mMyData.setIndex(index);
            return true;
        }
    }
    public void sel_consumer(){
        Cursor cursor = db.rawQuery("select * from consumer",null);
        System.out.println("查询个人信息：");
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
        System.out.println("查询机构信息：");
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            System.out.println(id+"-"+name+"-"+pwd+"-"+number);
        }

    }

    //插入初始化数据
    public void ins(){
        Cursor cursor = db.rawQuery("select * from consumer",null);
        if(cursor.getCount() <= 0){
            System.out.println("检测到第一次使用，执行数据初始化");
            //个人账号
            db.execSQL("insert into consumer values(null,'ww','123','18101964502')");
            db.execSQL("insert into consumer values(null,'ww2','321','18101964502')");
            //机构账号
            db.execSQL("insert into designer values(null,'ww','123','18101964502')");
            db.execSQL("insert into designer values(null,'ww2','321','18101964502')");
            System.out.println("插入数据成功");
        }
        sel_consumer();
        sel_designer();
    }
}
