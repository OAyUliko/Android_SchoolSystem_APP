package com.example.android_student_system.student.ui.evaluate;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.android_student_system.R;
import com.example.android_student_system.StuMainActivity;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;
import java.util.ArrayList;

public class evaluate extends AppCompatActivity {
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Intent intent;
    private TextView TV;
    private RadioGroup One;
    private RadioGroup Two;
    private RadioGroup Three;
    private RadioGroup Four;
    private RadioGroup Five;
    private ArrayList<Integer> eachgrade;
    public static ArrayList<Integer> IsAllCourse=new ArrayList<>();
    private int grade=0;
    private String CourseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_evaluate);

        One=findViewById(R.id.one);
        Two=findViewById(R.id.two);
        Three=findViewById(R.id.three);
        Four=findViewById(R.id.four);
        Five=findViewById(R.id.five);
        intent = getIntent();//获得选择课程页面选择的课程名

        TV=findViewById(R.id.name);
        CourseName=intent.getStringExtra("CourseName");
        TV.setText("请对 "+intent.getStringExtra("CourseName")+" 课程做出评价进行提交");

    }
    public void submit(View view) throws IOException {
        grade=0;
        eachgrade =new ArrayList<>();
        final RadioButton rb=findViewById(Five.getCheckedRadioButtonId());
        //eachgrade.add((int)Five.getTag());
        eachgrade.add(One.getCheckedRadioButtonId()%20);
        eachgrade.add(Two.getCheckedRadioButtonId()%20-4);
        eachgrade.add(Three.getCheckedRadioButtonId()%20-8);
        eachgrade.add(Four.getCheckedRadioButtonId()%20-12);
        eachgrade.add(Integer.parseInt(rb.getTag().toString()));

        //eachgrade.add(Five.getCheckedRadioButtonId()%17+1);
        for(int a=0;a<eachgrade.size();a++)
        {
            grade+=eachgrade.get(a);
        }
        Log.v("luyao",grade+"");
        submitScoretoDB(grade);
        Toast.makeText(this,"成功提交!",Toast.LENGTH_SHORT).show();

        Intent intent2=new Intent(this, StuMainActivity.class);
        startActivity(intent2);
        finish();
    }

    public void submitScoretoDB(int grade) throws IOException {
        dbHelper = new DBManager(this);
        myHelper = new MyHelper(this);
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getWritableDatabase();//获得可读写的对象
        //这里是是execSQL 不是rawQuery！！！
        DB.execSQL("UPDATE course SET Score_pingjiao = ? WHERE CourseName= ?", new Object[]{ grade,CourseName });
        dbHelper.closeDatabase();
    }

}
