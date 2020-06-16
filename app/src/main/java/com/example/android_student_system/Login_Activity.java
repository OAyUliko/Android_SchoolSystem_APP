package com.example.android_student_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;


public class Login_Activity extends AppCompatActivity {
    private String state = "";//标记是选择了学生,老师还是管理员
    private int load_state = 0;//账号查询状态，为1表示：正确登录
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private EditText No;
    private EditText Password;
    private RadioGroup rg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login__page);

        No = findViewById(R.id.No);
        Password = findViewById(R.id.Password);
        rg = findViewById(R.id.load_radiogroup);

        //获取数据库对象
        dbHelper = new DBManager(this);
        myHelper = new MyHelper(this);
        ReleaseDataBaseActivity releaseDataBaseActivity=new ReleaseDataBaseActivity(this);
        releaseDataBaseActivity.OpenDataBase();

    }

    //【登录】按钮跳转方法
    public void GotoMain(View view) throws IOException {
        dbHelper.openDatabase();
        GetState();//判断选择的登录身份
        Load_success();//判断选择的登录身份
    }

    public int Load_success() {
        int no = Integer.parseInt(No.getText().toString());
        String password = Password.getText().toString();
        String Password = "";
        if (state.equals("student")) {
            DB = myHelper.getReadableDatabase();//获得可读写的对象
            Cursor cursor = DB.rawQuery("select * from stulogin where No = ?", new String[]{no + ""});
            //判断有无此账号
            if (cursor.getCount() == 0)
                Toast.makeText(getApplicationContext(), "学号错误！", Toast.LENGTH_SHORT).show();
            else {
                cursor.moveToFirst();
                Password = cursor.getString(cursor.getColumnIndex("Password"));
                //判断账号的密码是否正确
                if (Password.equals(password)) {
                    load_state = 1;
                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                    //获得相关的数据作为【个人信息】
                    Cursor cursor2 = DB.rawQuery("SELECT Name,Sex,Id,Major_Name, Class from student, major where student.No= ? and student.Major_Id= major.Major_Id;", new String[]{no + ""});
                    cursor2.moveToFirst();
                    Intent intent = new Intent(this, StuMainActivity.class);//获得数据，数据传输给下个Activity
                    intent.putExtra("student_no", no);
                    intent.putExtra("student_name", cursor2.getString(0));
                    intent.putExtra("student_sex", cursor2.getString(1));
                    intent.putExtra("student_id", cursor2.getString(2));
                    intent.putExtra("student_majorname", cursor2.getString(3));
                    intent.putExtra("student_classname", cursor2.getString(4));
                    startActivity(intent);
                    cursor.close();
                    cursor2.close();
                    dbHelper.closeDatabase();
                } else
                    Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();
            }
        } else if (state.equals("admin")) {
            DB = myHelper.getReadableDatabase();//获得可读写的对象
            Cursor cursor = DB.rawQuery("select * from admin where Name = ?", new String[]{no + ""});
            //判断有无此账号
            if (cursor.getCount() == 0)
                Toast.makeText(getApplicationContext(), "工号错误！", Toast.LENGTH_SHORT).show();
            else {
                cursor.moveToFirst();
                Password = cursor.getString(cursor.getColumnIndex("Password"));
            }
            //判断账号的密码是否正确
            if (Password.equals(password)) {
                load_state = 1;
                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                //获得相关的数据作为【个人信息】
                Intent intent = new Intent(this, AdmMainActivity.class);//获得数据，数据传输给下个Activity
                intent.putExtra("admin_no", no);
                startActivity(intent);
                cursor.close();
                dbHelper.closeDatabase();
            } else
                Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();

        } else if (state.equals("teacher")) {
            DB = myHelper.getReadableDatabase();//获得可读写的对象
            Cursor cursor = DB.rawQuery("select * from tealogin where No = ?", new String[]{no + ""});
            //判断有无此账号
            if (cursor.getCount() == 0)
                Toast.makeText(getApplicationContext(), "工号错误！", Toast.LENGTH_SHORT).show();
            else {
                cursor.moveToFirst();
                Password = cursor.getString(cursor.getColumnIndex("Password"));
                //判断账号的密码是否正确
                if (Password.equals(password)) {
                    load_state = 1;
                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                    //获得相关的数据作为【个人信息】
                    Cursor cursor2 = DB.rawQuery("SELECT Name,Id,Major_Name, Sex from teacher, major where teacher.Nofortea= ? and teacher.Major_Id= major.Major_Id;", new String[]{no + ""});
                    cursor2.moveToFirst();
                    Intent intent = new Intent(this, TeaMainActivity.class);//获得数据，数据传输给下个Activity
                    intent.putExtra("teacher_no", no);
                    intent.putExtra("teacher_name", cursor2.getString(0));
                    intent.putExtra("teacher_sex", cursor2.getString(3));
                    intent.putExtra("teacher_id", cursor2.getString(1));
                    intent.putExtra("teacher_majorname", cursor2.getString(2));
                    startActivity(intent);
                    cursor.close();
                    cursor2.close();
                    dbHelper.closeDatabase();
                } else
                    Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();
            }
        }
        return load_state;
    }


    //获得选中的登录身份
    public void GetState() {
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.radiobutton_student:
                state = "student";
                break;
            case R.id.radiobutton_admin:
                state = "admin";
                break;
            case R.id.radiobutton_teacher:
                state = "teacher";
                break;
            default:
                Toast.makeText(getApplicationContext(), "请选择登录身份", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
