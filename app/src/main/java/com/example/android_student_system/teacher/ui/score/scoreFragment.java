package com.example.android_student_system.teacher.ui.score;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;
import com.example.android_student_system.student.ui.evaluate.evaluate;
import com.example.android_student_system.student.ui.evaluate.evaluateFragment;
import com.example.android_student_system.student.ui.score.scoreViewModel;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class scoreFragment extends Fragment {

    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private int num;
    private MyAdapter adapter;

    private scoreViewModel galleryViewModel;
    private LinearLayout container;
    private View root;
    private TextView textView;
    private ListView listView;
    private ImageView  queren;
    private String[] type = new String[]{"本学期授课", "我任课过的课程"};
    private ArrayList<String> course_name = new ArrayList<>();
    private ArrayList<String> course_year;
    private ArrayList<Integer> course_credit = new ArrayList<>();
    private ArrayList<Integer> course_start = new ArrayList<>();
    private ArrayList<Integer> course_type = new ArrayList<>();
    private ArrayList<Integer> course_end = new ArrayList<>();

    //这个数组是对每个Item进行标记，提交过成绩就变成true，在item上表示为“已提交”
    //这一点没有做到！！！
    public static ArrayList<Boolean> IsClicked=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //galleryViewModel =
        ViewModelProviders.of(this).get(scoreViewModel.class);
        root = inflater.inflate(R.layout.fragment_score, container, false);
        //num=course_name.size();
        init();
        listView = root.findViewById(R.id.score_lv);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new MyOnItemClickListener());
        return root;
    }


    public void init() {
        container = root.findViewById(R.id.score_linlay);
        //设置相关参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(180, 10, 180, 10);
        layoutParams.gravity = Gravity.CENTER;
        //把文字添加进去
        for (int i = 0; i < type.length; i++) {

            textView = new TextView(getContext());
            textView.setText(type[i]);
            textView.setId(i);
            if (i == 0)
                textView.setEnabled(true);

            textView.setLayoutParams(layoutParams);
            container.addView(textView);
            container.invalidate();//invalidate是在主线程调用刷新界面
            textView.setOnClickListener(new OnClickListenerImpl());//这是对上方的"本学期授课", "所有课程"两者进行点击选择
        }
    }

    //出现"本学期授课或所有课程"
    private class OnClickListenerImpl implements View.OnClickListener {
        public void onClick(View v) {
            //获取数据库对象
            dbHelper = new DBManager(getContext());
            myHelper = new MyHelper(getContext());
            try {
                dbHelper.openDatabase();//连接数据库
            } catch (IOException e) {
                e.printStackTrace();
            }
            DB = myHelper.getReadableDatabase();//获得可读写的对象
            switch (v.getId()) {
                case 0:
                        GetThisTermTeach();
                    break;
                case 1:
                    //已完成的课程，这里就是成绩的显示
                        GetAllTermTeach();
            }
        }

    }

    //查询这名老师在这学期教授的课程，然后可以选择一门课程上传成绩
    private void GetThisTermTeach(){
        //这里为了省事，用工号直接查找,直接默认是2019 2学期
        cursor = DB.rawQuery("SELECT course.CourseName,course.Credit,course.CourseType ,course.StartWeek,course.EndWeek FROM course,teacher WHERE course.TeacherNo = teacher.Nofortea AND TermYear=2019 AND TermId=2 AND Nofortea=2357;", null);
        if (cursor.getCount() != 0) {
            course_year = new ArrayList<>();
            String term = "2019-2";
            for (int a = 0; a <2; a++)
                course_year.add(term);
            cursor.moveToFirst();
            commom();
            while (cursor.moveToNext()) {
                commom();
            }

        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();

        adapter.notifyDataSetChanged();
    }

    private void GetAllTermTeach(){
        //查询全部的成绩
        cursor = DB.rawQuery("SELECT course.CourseName,course.Credit,course.CourseType ,course.StartWeek,course.EndWeek ,TermYear, TermId FROM course,teacher WHERE course.TeacherNo = teacher.Nofortea  AND Nofortea=2357;", null);
        String year;
        String term;
        if (cursor.getCount() != 0) {
            course_year = new ArrayList<>();
            course_year.clear();

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
        int start = cursor.getInt(cursor.getColumnIndex("StartWeek"));
        int end = cursor.getInt(cursor.getColumnIndex("EndWeek"));
        course_start.add(start);
        course_end.add(end);
        course_type.add(cursor.getInt(cursor.getColumnIndex("CourseType")));
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
            View view = View.inflate(getContext(), R.layout.list_item_teach, null);//加载Item布局
            TextView name = view.findViewById(R.id.course_name);
            TextView credit = view.findViewById(R.id.course_credit);
            TextView start = view.findViewById(R.id.course_start);
            TextView end = view.findViewById(R.id.course_end);
            TextView year = view.findViewById(R.id.course_year);
            TextView style = view.findViewById(R.id.course_style);
            queren=view.findViewById(R.id.Queren);

            name.setText(course_name.get(position));
            credit.setText("课程学分：" + course_credit.get(position));
            start.setText("开始周数：" + course_start.get(position));
            end.setText("结课周数：" + course_end.get(position));
            year.setText("课程学年：" + course_year.get(position));
            String a=TurnTypeNumToWord(course_type.get(position));
            style.setText("课程类别：" + a + "");

            return view;
        }
    }

    //这是对具体某节课程进行点击
    //点击课程之后，进入详细给分页面
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            givescore give=new givescore();
            if(give.TAG==0)//表示没有提交过成绩
            {
                Intent intent = new Intent(getActivity(), givescore.class);
                intent.putExtra("CourseName", course_name.get(position));
                startActivity(intent);
            }
            if(give.TAG==1)//表示提交过成绩,再点击就查看成绩
            {
                Intent intent = new Intent(getActivity(), givescore2.class);
                intent.putExtra("CourseName", course_name.get(position));
                startActivity(intent);
            }



        }
    }


}