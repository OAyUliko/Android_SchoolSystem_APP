package com.example.android_student_system.admin.ui.student_mgt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;
import com.example.android_student_system.student.ui.evaluate.evaluateViewModel;
import com.example.android_student_system.student.ui.score.scoreFragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class studentmgtFragment extends Fragment {
    private evaluateViewModel toolsViewModel;
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private View root;

    private Button B1;
    private Button BAdd;
    private Button BDelete;
    private Button BUpdate;
    private Button BScore;
    private Button BClean;
    private EditText ET1;
    private EditText ET2;
    private EditText E1;
    private TextView E2;
    private EditText E3;
    private EditText E4;
    private Spinner spinner;
    private EditText E6;
    private EditText E7;
    private TextView E8;
    private EditText E9;

    private String name;
    private String sex;
    private int major;
    private String classes;
    private String id;
    private String pass;
    private int age;
    private int SelectedId;
    private int no;
    int No;

    private ArrayList<String> course_name = new ArrayList<>();
    private ArrayList<String> course_year = new ArrayList<>();;
    private ArrayList<Integer> course_credit = new ArrayList<>();
    private ArrayList<Integer> course_score = new ArrayList<>();
    private ArrayList<Integer> course_type = new ArrayList<>();
    private ArrayList<String> course_point = new ArrayList<>();

    private ArrayList<String> MajorName = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.fragment_student_mgt, container, false);

        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());

        B1 = root.findViewById(R.id.B1);
        BAdd = root.findViewById(R.id.BAdd);
        BDelete = root.findViewById(R.id.BDelete);
        BUpdate = root.findViewById(R.id.BUpdate);
        BScore = root.findViewById(R.id.BScore);
        BScore = root.findViewById(R.id.BScore);
        BClean = root.findViewById(R.id.BClean);
        ET1 = root.findViewById(R.id.et1);
        ET2 = root.findViewById(R.id.et2);
        E1 = root.findViewById(R.id.E1);
        E2 = root.findViewById(R.id.E2);
        E3 = root.findViewById(R.id.E3);
        E4 = root.findViewById(R.id.E4);
        spinner = root.findViewById(R.id.spinner);
        E6 = root.findViewById(R.id.E6);
        E7 = root.findViewById(R.id.E7);
        E8 = root.findViewById(R.id.E8);
        E9 = root.findViewById(R.id.E9);


        GetMajor();
        SetMajor();
        //Fragment不是布局器，不具备渲染视图的能力，虽然可以管理布局器，但它管理的布局器最终要加载到一个ViewGroup对象内，
        // 由ViewGroup对象来渲染，而ViewGroup并不知道每一个子控件来源于哪里。
        //所以Fragment里面用不了onclick属性,只能用setOnClickListener来解决
        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = ET1.getText() + "";
                String no = ET2.getText().toString();
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getReadableDatabase();//获得可读写的对象
                cursor = DB.rawQuery("SELECT student.Name,student.No,student.Sex,student.Age,major.Major_Name,major.Major_Id,student.Class,student.Id,student.pingjiao2020,stulogin.Password FROM student,stulogin,major   WHERE student.Name =? OR student.No= ? AND student.No = stulogin.No AND student.Major_Id=major.Major_Id;", new String[]{name, no + ""});
                if (cursor.getCount() != 0) {
                    SetMajor();
                    cursor.moveToFirst();
                    E1.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    E2.setText(cursor.getInt(cursor.getColumnIndex("No")) + "");
                    E3.setText(cursor.getString(cursor.getColumnIndex("Sex")));
                    E4.setText(cursor.getInt(cursor.getColumnIndex("Age")) + "");

                    //int position = adapter.getPosition((cursor.getInt(cursor.getColumnIndex("Major_Id"))) + "");    //根据学院的id-1来设置spinner
                    //spinner.setSelection(position);
                    spinner.setSelection(cursor.getInt(cursor.getColumnIndex("Major_Id"))-1);

                    E6.setText(cursor.getString(cursor.getColumnIndex("Class")));
                    E7.setText(cursor.getString(cursor.getColumnIndex("Id")));
                    int PJ = cursor.getInt(cursor.getColumnIndex("pingjiao2020"));
                    String pj;
                    if (PJ == 0)
                        pj = "没有参加";
                    else
                        pj = "已参加";
                    E8.setText(pj + "");
                    E9.setText(cursor.getString(cursor.getColumnIndex("Password")));
                } else
                    Toast.makeText(getContext(), "没有此学生，是不是输入错误啦", Toast.LENGTH_SHORT).show();
            }
        });

        BAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetInformation();
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getWritableDatabase();//获得可读写的对象
                //只是删除了登陆表中的信息，不是删除了学生表的
                DB.execSQL("INSERT INTO student(Name,Sex,Age,Major_Id,Class,Id) values(?,?,?,?,?,?) ;", new Object[]{name, sex, age, major, classes, id});
                SQLiteDatabase DB2 = myHelper.getReadableDatabase();//获得可读写的对象
                cursor = DB2.rawQuery("SELECT student.No FROM student WHERE student.Name =? AND student.Id= ? ;", new String[]{name, id});
                cursor.moveToFirst();
                int studentno=cursor.getInt(cursor.getColumnIndex("No"));
                E2.setText(studentno+"");
                DB.execSQL("INSERT INTO stulogin values(?,?);", new Object[]{studentno, pass});
                Toast.makeText(getContext(), "学生信息添加成功！", Toast.LENGTH_SHORT).show();
            }
        });

        BUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetInformation();//获得修改后的信息
                no = Integer.valueOf(E2.getText().toString());
                //在DB里修改
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getWritableDatabase();//获得可读写的对象
                DB.execSQL("UPDATE student SET Name = ? ,Sex=?,Age=?,Class=?,Id=?,Major_Id=? WHERE No=? ;", new Object[]{name, sex, age, classes, id, major, no});
                DB.execSQL("UPDATE stulogin SET Password = ? WHERE No=? ;", new Object[]{pass, no});
                Toast.makeText(getContext(), "信息修改成功！", Toast.LENGTH_SHORT).show();
            }
        });

        BDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = Integer.valueOf(E2.getText().toString());
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getWritableDatabase();//获得可读写的对象
                //只是删除了登陆表中的信息，不是删除了学生表的
                DB.execSQL("DELETE FROM stulogin WHERE No=? ;", new Object[]{no});
                Toast.makeText(getContext(), "信息删除成功！", Toast.LENGTH_SHORT).show();
            }
        });

        BClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetInformationNull();
            }
        });

        BScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               No=Integer.valueOf(E2.getText().toString());
                //ListView listView = root.findViewById(R.id.showstudentscore);
                GetAllTermScore();
                CreateDialog();// 点击创建Dialog
            }
        });

        cursor.close();
        dbHelper.closeDatabase();

        return root;
    }

    public void GetInformation() {
        name = E1.getText() + "";
        sex = E3.getText() + "";
        age = Integer.valueOf(E4.getText().toString());
        major = SelectedId;
        classes = E6.getText() + "";
        id = E7.getText() + "";
        pass = E9.getText() + "";
    }

    public void SetInformationNull() {
        E1.setText("");
        E2.setText("");
        E3.setText("");
        E4.setText("");
        E6.setText("");
        E7.setText("");
        E8.setText("");
        E9.setText("");
    }

    private void SetMajor() {
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, MajorName);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            SelectedId = arg2 + 1;
        }

        public void onNothingSelected(AdapterView<?> arg0) { }
    }

    private void GetMajor() {
        try {
            dbHelper.openDatabase();//连接数据库
        } catch (IOException e) {
            e.printStackTrace();
        }
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("SELECT major.Major_Name FROM major;", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            MajorName.add(cursor.getString(cursor.getColumnIndex("Major_Name")));
            while (cursor.moveToNext()) {
                MajorName.add(cursor.getString(cursor.getColumnIndex("Major_Name")));
            }
        }
    }

    public void CreateDialog() {
        // 动态加载一个listview的布局文件进来
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View getlistview = inflater.inflate(R.layout.showstudentscore, null);
        MyAdapter adapter = new MyAdapter();


        // 给ListView绑定内容
        ListView listview = (ListView) getlistview.findViewById(R.id.X_listview);
        listview.setAdapter(adapter);
        // 给listview加入适配器

        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //listview.setOnItemClickListener(new ItemOnClick());

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());

        //设置加载的listview
        builder.setView(getlistview);
        builder.create().show();
    }

    private void GetAllTermScore(){
        //查询全部的成绩
        cursor = DB.rawQuery("SELECT course.CourseName,course.Credit,score_软工11701.Score,course.CourseType,TermYear ,TermId FROM course,score_软工11701 WHERE course.CourseId = score_软工11701.CourseId AND score_软工11701.StudentNo = ? ;", new String[]{No+""});
        String year;
        String term;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            year = (cursor.getInt(cursor.getColumnIndex("TermYear"))) + "";
            term = (cursor.getInt(cursor.getColumnIndex("TermId"))) + "";
            course_year.add(year + "-" + term);
            commom();
            while (cursor.moveToNext()) {
                year = (cursor.getInt(cursor.getColumnIndex("TermYear"))) + "";
                term = (cursor.getInt(cursor.getColumnIndex("TermId"))) + "";
                course_year.add(year + "-" + term);
                commom();
            }

        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
        adapter.notifyDataSetChanged();
    }

    private void commom() {
        course_name.add(cursor.getString(cursor.getColumnIndex("CourseName")));
        course_credit.add(cursor.getInt(cursor.getColumnIndex("Credit")));
        int score = cursor.getInt(cursor.getColumnIndex("Score"));
        course_score.add(score);
        course_type.add(cursor.getInt(cursor.getColumnIndex("CourseType")));
        DecimalFormat df = new DecimalFormat("0.0");//设置保留位数
        String a = df.format((float) (score - 50) / 10);//除法运算
        course_point.add(a);
    }

    //重写simpleadapterd的getview方法

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return course_name.size();
        }

        @Override
        public Object getItem(int position) {
            return course_name.get(position);//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.list_item_score, null);//加载Item布局
            TextView name = view.findViewById(R.id.course_name);
            TextView credit = view.findViewById(R.id.course_credit);
            TextView score = view.findViewById(R.id.course_score);
            TextView point = view.findViewById(R.id.course_point);
            TextView year = view.findViewById(R.id.course_year);
            TextView style = view.findViewById(R.id.course_style);

            name.setText(course_name.get(position));
            credit.setText("课程学分：" + course_credit.get(position));
            score.setText("课程成绩：" + course_score.get(position));
            point.setText("课程绩点：" + course_point.get(position));
            year.setText("课程学年：" + course_year.get(position));
            String a=TurnTypeNumToWord(course_type.get(position));
            style.setText("课程类别：" + a + "");
            return view;
        }

        private String TurnTypeNumToWord(int type) {
            String TYPE=null;
            switch (type) {
                case 1:
                    TYPE="必修";
                case 2:
                    TYPE="公选";
                case 3:
                    TYPE="实践";
            }
            return TYPE;
        }
    }


}