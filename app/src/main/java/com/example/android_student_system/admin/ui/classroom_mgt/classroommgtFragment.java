package com.example.android_student_system.admin.ui.classroom_mgt;

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
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;
import com.example.android_student_system.student.ui.evaluate.evaluateViewModel;
import com.example.android_student_system.teacher.ui.score.givescore;
import com.example.android_student_system.teacher.ui.score.givescore2;
import com.example.android_student_system.teacher.ui.score.scoreFragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class classroommgtFragment extends Fragment {
    private evaluateViewModel toolsViewModel;
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private View root;

    private ListView LV;
    private Button BAdd;
    private Button BDelete;
    private Button BUpdate;
    private Button BScore;
    private Button BClean;
    private EditText EE1;
    private EditText EE2;


    private ArrayList<String> classroomname = new ArrayList<>();
    private ArrayList<Integer> classroomsize = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.fragment_classroom_mgt, container, false);

        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());

        BAdd = root.findViewById(R.id.BAdd);
        BDelete = root.findViewById(R.id.BDelete);
        BUpdate = root.findViewById(R.id.BUpdate);
        BScore = root.findViewById(R.id.BScore);
        BScore = root.findViewById(R.id.BScore);
        BClean = root.findViewById(R.id.BClean);
        EE1 = root.findViewById(R.id.EE1);
        EE2 = root.findViewById(R.id.EE2);
        LV = root.findViewById(R.id.course);
        MyAdapter adapter = new MyAdapter();
        LV.setAdapter(adapter);

        BAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = EE1.getText().toString();
                int size = Integer.valueOf(EE2.getText().toString());
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getWritableDatabase();//获得可读写的对象
                //只是删除了登陆表中的信息，不是删除了学生表的
                DB.execSQL("INSERT INTO classroom(ClassRoomName,ClassRoomSize) values(?,?);", new Object[]{place, size});
                Toast.makeText(getContext(), "教室信息添加成功！", Toast.LENGTH_SHORT).show();
            }
        });

        BClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EE1.setText("");
                EE2.setText("");
            }
        });

        BScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GetAllRoom();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MyAdapter adapter = new MyAdapter();
                LV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                LV.setOnItemClickListener(new MyOnItemClickListener());
            }
        });

        //cursor.close();
        //dbHelper.closeDatabase();

        return root;
    }

    private void GetAllRoom() throws IOException {
        classroomname.clear();//记得要先清空，不然再次点击查找会重复加载item
        classroomsize.clear();
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        dbHelper.openDatabase();//连接数据库
        //查询全部的教室
        cursor = DB.rawQuery("SELECT classroom.ClassRoomName,classroom.ClassRoomSize FROM classroom ;", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            classroomname.add(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
            classroomsize.add(cursor.getInt(cursor.getColumnIndex("ClassRoomSize")));
            while (cursor.moveToNext()) {
                classroomname.add(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
                classroomsize.add(cursor.getInt(cursor.getColumnIndex("ClassRoomSize")));
            }

        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
    }

    //重写simpleadapterd的getview方法

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return classroomname.size();
        }

        @Override
        public Object getItem(int position) {
            return classroomname.get(position);//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.list_item_course3, null);//加载Item布局
            TextView name = view.findViewById(R.id.tv_name3);
            TextView size = view.findViewById(R.id.tv_score3);

            name.setText("地址：" + classroomname.get(position) + "");
            size.setText("容纳人数：" + classroomsize.get(position) + "");
            return view;
        }


    }

    //这是对某个教室进行点击，要么修改要么删除
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int p = position;
            EE1.setText(classroomname.get(p));
            EE2.setText(classroomsize.get(p) + "");
            BDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dbHelper.openDatabase();//连接数据库
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DB = myHelper.getWritableDatabase();//获得可读写的对象
                    //只是删除了登陆表中的信息，不是删除了学生表的
                    DB.execSQL("DELETE FROM classroom WHERE ClassRoomName=? ;", new Object[]{classroomname.get(p)});
                    Toast.makeText(getContext(), classroomname.get(p) + "教室删除成功！", Toast.LENGTH_SHORT).show();
                }
            });


            BUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String place = EE1.getText().toString();
                    int size = Integer.valueOf(EE2.getText().toString());//获得更改后的新人数
                    //在DB里修改
                    try {
                        dbHelper.openDatabase();//连接数据库
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DB = myHelper.getWritableDatabase();//获得可读写的对象
                    DB.execSQL("UPDATE classroom SET ClassRoomSize=? WHERE ClassRoomName=? ;", new Object[]{size, place});
                    Toast.makeText(getContext(), "信息修改成功！", Toast.LENGTH_SHORT).show();
                }

            });


        }
    }


}