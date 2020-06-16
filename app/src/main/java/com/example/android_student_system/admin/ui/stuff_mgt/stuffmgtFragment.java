package com.example.android_student_system.admin.ui.stuff_mgt;

import android.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.admin.ui.student_mgt.studentmgtFragment;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;
import com.example.android_student_system.student.ui.evaluate.evaluateViewModel;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class stuffmgtFragment extends Fragment {
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
    private EditText E7;
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

    private ArrayList<String> CourseName = new ArrayList<>();
    private ArrayList<Integer> CourseEvaluate = new ArrayList<>();

    private ArrayList<String> MajorName = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.fragment_stuff_mgt, container, false);

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
        E7 = root.findViewById(R.id.E7);
        E9 = root.findViewById(R.id.E9);


        GetMajor();
        SetMajor();

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
                cursor = DB.rawQuery("SELECT teacher.Name,teacher.Nofortea,teacher.Sex,teacher.Age,major.Major_Name,major.Major_Id,teacher.Id,tealogin.Password FROM teacher,tealogin,major   WHERE teacher.Name =? OR teacher.Nofortea= ? AND tealogin.No = teacher.Nofortea AND teacher.Major_Id=major.Major_Id;", new String[]{name, no + ""});
                if (cursor.getCount() != 0) {
                    SetMajor();
                    cursor.moveToFirst();
                    E1.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    E2.setText(cursor.getInt(cursor.getColumnIndex("Nofortea")) + "");
                    E3.setText(cursor.getString(cursor.getColumnIndex("Sex")));
                    E4.setText(cursor.getInt(cursor.getColumnIndex("Age")) + "");

                    spinner.setSelection(cursor.getInt(cursor.getColumnIndex("Major_Id")) - 1);

                    E7.setText(cursor.getString(cursor.getColumnIndex("Id")));
                    E9.setText(cursor.getString(cursor.getColumnIndex("Password")));
                } else
                    Toast.makeText(getContext(), "没有此教师，是不是输入错误啦", Toast.LENGTH_SHORT).show();
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
                DB.execSQL("INSERT INTO teacher(Name,Sex,Age,Major_Id,Id) values(?,?,?,?,?) ;", new Object[]{name, sex, age, major, id});
                SQLiteDatabase DB2 = myHelper.getReadableDatabase();//获得可读写的对象
                cursor = DB2.rawQuery("SELECT teacher.Nofortea FROM teacher WHERE teacher.Name =? AND teacher.Id= ? ;", new String[]{name, id});
                cursor.moveToFirst();
                int teacherno = cursor.getInt(cursor.getColumnIndex("Nofortea"));
                E2.setText(teacherno + "");
                DB.execSQL("INSERT INTO tealogin values(?,?);", new Object[]{teacherno, pass});
                Toast.makeText(getContext(), "教师信息添加成功！", Toast.LENGTH_SHORT).show();
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
                DB.execSQL("UPDATE teacher SET Name = ? ,Sex=?,Age=?,Id=?,Major_Id=? WHERE Nofortea=? ;", new Object[]{name, sex, age, id, major, no});
                DB.execSQL("UPDATE tealogin SET Password = ? WHERE No=? ;", new Object[]{pass, no});
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
                //只是删除了登陆表中的信息，不是删除了教师表的
                DB.execSQL("DELETE FROM tealogin WHERE No=? ;", new Object[]{no});
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
                No = Integer.valueOf(E2.getText().toString());
                getCourse();
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
        id = E7.getText() + "";
        pass = E9.getText() + "";
    }


    public void SetInformationNull() {
        E1.setText("");
        E2.setText("");
        E3.setText("");
        E4.setText("");
        E7.setText("");
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

        public void onNothingSelected(AdapterView<?> arg0) {
        }
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

    public void getCourse() {
        //查询全部的成绩
        no = Integer.valueOf(E2.getText().toString());
        cursor = DB.rawQuery("SELECT course.Score_pingjiao,CourseName FROM course ,teacher WHERE teacher.Nofortea = course.TeacherNo AND teacher.Nofortea=? AND course.Score_pingjiao IS NOT NULL", new String[]{no + ""});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            CourseName.add(cursor.getString(cursor.getColumnIndex("CourseName")));
            CourseEvaluate.add(cursor.getInt(cursor.getColumnIndex("Score_pingjiao")));
            while (cursor.moveToNext()) {
                CourseName.add(cursor.getString(cursor.getColumnIndex("CourseName")));
                CourseEvaluate.add(cursor.getInt(cursor.getColumnIndex("Score_pingjiao")));
            }
        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
        adapter.notifyDataSetChanged();
    }


    //重写simpleadapterd的getview方法
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CourseName.size();//返回Item的总数
        }

        @Override
        public Object getItem(int position) {
            return CourseName.get(position);//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.list_item_course3, null);//加载Item布局
            TextView tv = view.findViewById(R.id.tv_name3);
            tv.setText(CourseName.get(position));
            TextView tv2 = view.findViewById(R.id.tv_score3);
            tv2.setText(CourseEvaluate.get(position) + "");
            return view;
        }

    }

}
