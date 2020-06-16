package com.example.android_student_system.student.ui.person_information;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;

public class MyDialog extends Dialog {
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private String dialogName;
    private TextView tv;
    private EditText et;
    private Button btnOK;
    private Button btncancel;
    private int getno;


    public MyDialog(Context context,int getno) {
        super(context);
        this.getno=getno;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题
        setContentView(R.layout.mydialog);//引入自定义的对话框布局
        et = findViewById(R.id.edittext);
        btnOK = findViewById(R.id.ok);
        btncancel = findViewById(R.id.cancel);

        //更改DB 中的密码
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person_informationFragment person=new person_informationFragment();
                String newpass =et.getText()+"";
                dbHelper = new DBManager(getContext());
                myHelper = new MyHelper(getContext());
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getWritableDatabase();//获得可读写的对象
                //这里是是execSQL 不是rawQuery！！！
                DB.execSQL("UPDATE stulogin SET Password = ? WHERE No= ?", new Object[]{newpass, getno});
                dbHelper.closeDatabase();
                dismiss();
                Toast.makeText(getContext(), "更改成功", Toast.LENGTH_SHORT).show();

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }
}
