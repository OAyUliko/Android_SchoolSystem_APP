package com.example.android_student_system.student.ui.score;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

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
    private String[] type = new String[]{"本学期成绩", "全部成绩"};
    private ArrayList<String> course_name = new ArrayList<>();
    private ArrayList<String> course_year;
    private ArrayList<Integer> course_credit = new ArrayList<>();
    private ArrayList<Integer> course_score = new ArrayList<>();
    private ArrayList<Integer> course_type = new ArrayList<>();
    private ArrayList<String> course_point = new ArrayList<>();

    //private String[] score= new String[]{};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //galleryViewModel =
        ViewModelProviders.of(this).get(scoreViewModel.class);
        root = inflater.inflate(R.layout.fragment_score, container, false);
        //num=course_name.size();
        TextView F1 = root.findViewById(R.id.fortea1);
        TextView F2 = root.findViewById(R.id.fortea2);
        F1.setText(" ");
        F2.setText(" ");
        init();
        listView = root.findViewById(R.id.score_lv);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        return root;
    }

    //    public void onViewCreated(View view,Bundle savedInstanceState)
//    {
//        super.onViewCreated(view,savedInstanceState);
//        listView=root.findViewById(R.id.score_lv);
//        MyAdapter adapter=new MyAdapter();
//        listView.setAdapter(adapter);
//    }
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
            container.invalidate();//nvalidate是在主线程调用刷新界面
            textView.setOnClickListener(new scoreFragment.OnClickListenerImpl());
        }
    }

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
                    try {
                        GetThisTermScore();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    //所有成绩
                        GetAllTermScore();
            }
        }

    }

    //查询本学年的成绩
    private void GetThisTermScore() throws IOException {
        //这里为了省事，用学号直接查找,直接默认是2019 2学期
        cursor = DB.rawQuery("SELECT course.CourseName,course.Credit,score_软工11701.Score,course.CourseType FROM course,score_软工11701 WHERE course.CourseId = score_软工11701.CourseId AND score_软工11701.StudentNo = 201707771 AND TermYear=2019 AND TermId=2;", null);
        if (cursor.getCount() != 0) {
            course_year = new ArrayList<>();
            String term = "2019-2";
            for (int a = 0; a < 6; a++)
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

    private void GetAllTermScore(){
        //查询全部的成绩
        cursor = DB.rawQuery("SELECT course.CourseName,course.Credit,score_软工11701.Score,course.CourseType,TermYear ,TermId FROM course,score_软工11701 WHERE course.CourseId = score_软工11701.CourseId AND score_软工11701.StudentNo = 201707771 ;", null);
        String year;
        String term;
        if (cursor.getCount() != 0) {
            course_year = new ArrayList<>();
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
    }
}