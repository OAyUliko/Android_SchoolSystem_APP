package com.example.android_student_system.teacher.ui.score;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;
import java.util.ArrayList;

public class givescore2 extends AppCompatActivity {
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private Intent intent;
    private TextView TV;
    private Button button;
    private ListView listView;
    public static int TAG=0;


    private ArrayList<String> StudentClass = new ArrayList<>();
    private ArrayList<Integer> StudentNo = new ArrayList<>();
    private ArrayList<String> StudentName = new ArrayList<>();
    private ArrayList<Integer> StudentScore = new ArrayList<>();
    ArrayList<Integer> S = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_givescore2);

        listView = findViewById(R.id.student_name);
        button = findViewById(R.id.SubmitButton);

        intent = getIntent();//获得选择课程页面选择的课程名

        TV = findViewById(R.id.score_name);
        TV.setText(intent.getStringExtra("CourseName") + " 课程的成绩如下");
        try {
            GetInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    private void GetInfo() throws IOException {
        dbHelper = new DBManager(getBaseContext());
        myHelper = new MyHelper(getBaseContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("SELECT student.No , student.Name, Class , Score FROM score_软工11701 , student ,course WHERE course.CourseName= ? AND score_软工11701.StudentNo= student.No AND score_软工11701.CourseId=course.CourseId", new String[]{intent.getStringExtra("CourseName")});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            getContent();
            while (cursor.moveToNext()) {
                getContent();
            }
        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
    }

    private void getContent() {
        StudentNo.add(cursor.getInt(cursor.getColumnIndex("No")));
        StudentName.add(cursor.getString(cursor.getColumnIndex("Name")));
        StudentClass.add(cursor.getString(cursor.getColumnIndex("Class")));
        StudentScore.add(cursor.getInt(cursor.getColumnIndex("Score")));
    }



    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return StudentNo.size();//返回Item的总数
        }

        @Override
        public Object getItem(int position) {
            return StudentNo.get(position);//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getBaseContext(), R.layout.list_item_singlecourse2, null);//加载Item布局
            TextView tv = view.findViewById(R.id.StudentName2);
            TextView tv2 = view.findViewById(R.id.StudentNo2);
            TextView tv3 = view.findViewById(R.id.StudentClass2);
            TextView editText = view.findViewById(R.id.score2);

            tv.setText("学生姓名：" + StudentName.get(position));
            tv2.setText("学号：" + StudentNo.get(position));
            tv3.setText("班级：" + StudentClass.get(position));
            editText.setText("分数："+StudentScore.get(position));
            return view;
        }



    }
}
