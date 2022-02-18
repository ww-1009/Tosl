package com.rangi.nanodet;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {
    EditText addname,addpwd1,addpwd2,addnumber;

    RadioGroup rdg;
    int index;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        initNavBar(true,"添加账户",false);
        //控件初始化
        addname = findViewById(R.id.Add_Username);
        addpwd1 = findViewById(R.id.Add_Password1);
        addpwd2 = findViewById(R.id.Add_Password2);
        addnumber = findViewById(R.id.Add_Number);
        rdg = findViewById(R.id.radioGroup);
        //依靠DatabaseHelper带全部参数的构造函数创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(this, "User_db",null,1);
        db = dbHelper.getWritableDatabase();
    }

    public void AddUserOnclick(View view) {
        //获取用户输入账号
        String name = addname.getText().toString();
        String number = addnumber.getText().toString();
        String pwd1 = addpwd1.getText().toString();
        String pwd2 = addpwd2.getText().toString();
        //获取选择的角色
        for (int i = 0; i < rdg.getChildCount(); i++) {
            RadioButton rd = (RadioButton) rdg.getChildAt(i);
            if (rd.isChecked()) {
                index = i;
                break;
            }
        }
        // 判空
        if(name.equals("") || number.equals("")|| pwd1.equals("")|| pwd2.equals("")){
            Toast.makeText(this,"注册信息不能为空",Toast.LENGTH_SHORT).show();
        }else {
            if(!(number.length()==11)){
                Toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            }else if (pwd1.equals(pwd2)) {

                //录入数据库
                if(index == 0){
                    //个人
                    String sql = "select * from consumer where username = '"+name+"'";
                    Cursor cu = db.rawQuery(sql,null);
                    //检查用户名是否存在，保证用户唯一性
                    if(cu.getCount() > 0){
                        Toast.makeText(this,"个人账号已被占用",Toast.LENGTH_SHORT).show();

                    }else {
                        add_consumer(name,pwd1,number);
                        Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this , LoginActivity.class);
                        startActivity(i);
                        finish();

                    }

                }else if(index == 1){
                    //机构
                    String sql2 = "select * from designer where username = '"+name+"'";
                    Cursor cur = db.rawQuery(sql2,null);
                    //检查用户名是否存在，保证用户唯一性
                    if(cur.getCount() > 0){
                        Toast.makeText(this,"机构账号已被占用",Toast.LENGTH_SHORT).show();
                    }else {
                        add_designer(name,pwd1,number);
                        Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this , LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }else{
                Toast.makeText(this, "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //根据用户信息添加个人
    public void add_consumer(String name,String pwd,String number){
        String sql_consumer = "insert into consumer values(null,'"+name+"','"+pwd+"','"+number+"')";
        db.execSQL(sql_consumer);

    }
    //根据用户信息添加机构

    public void add_designer(String name,String pwd,String number){
        String sql_designer = "insert into designer values(null,'"+name+"','"+pwd+"','"+number+"')";
        db.execSQL(sql_designer);
    }

}