package com.example.android_student_system.student.ui.about_class;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;

public class about_classFragment extends Fragment {
    private about_classViewModel slideshowViewModel;
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private int Day1_NotSingle;
    private int Day2_NotSingle;
    private int Day3_Single;
    private int Day4_Single;
    private String CourseName;
    private String TeacherName;
    private String CLassRoomName;
    private int Time1;
    private int Time2;
    private int Time3;
    private int Time4;
    private int StartWeek;
    private int EndWeek;
    private int ChooseWeek;
    private GridLayout gridLayout;
    private View root;
    private Cursor cursor;
    private int mGridMinWidth;
    private int mGridMinHeight;
    private GridLayout.LayoutParams params;
    private LinearLayout container;
    private String[] weeks = new String[]{"第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周", "第9周", "第10周", "第11周", "第12周", "第13周", "第14周", "第15周", "第16周", "第17周", "第18周", "第19周", "第20周"};
    private String[] days = new String[]{"  ", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] time = new String[]{"第一节课", "第二节课", "第三节课", "第四节课", "第五节课"};
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(about_classViewModel.class);
        root = inflater.inflate(R.layout.fragment_about_class, container, false);
        init();
        getScreenWidth();
        return root;
    }

    //把顶部的周数加到LinearLayout中的TextView
    public void init() {
        container = root.findViewById(R.id.linlay);
        gridLayout = (GridLayout) root.findViewById(R.id.grid);
        //设置相关参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);
        layoutParams.gravity = Gravity.CENTER;
        //把文字添加进去
        for (int i = 0; i < weeks.length; i++) {

            textView = new TextView(getContext());

            textView.setText(weeks[i]);
            textView.setId(i);
            textView.setLayoutParams(layoutParams);
            container.addView(textView);
            container.invalidate();//nvalidate是在主线程调用刷新界面
            textView.setOnClickListener(new OnClickListenerImpl());
        }
        //textView.performClick();
    }

    private class OnClickListenerImpl implements View.OnClickListener {
        public void onClick(View v) {

            ChooseWeek=v.getId()+1;
            if ((v.getId()) % 2 == 0)//奇数周
            {
                try {
                    gridLayout.removeAllViews();
                    setTitle();
                    getClassesforNotSingle();
                    getClassesforSingle();
                    System.out.println(v.getId());

                } catch (IOException e) {
                    System.out.println("数据库处理异常");
                }
            } else//偶数周
            {
                try {
                    gridLayout.removeAllViews();
                    setTitle();
                    getClassesforNotSingle();
                    System.out.println(v.getId());
                } catch (IOException e) {
                    System.out.println("数据库处理异常");
                }
            }
        }
    }

    //获得屏幕宽度，计算表格一格的平均宽度
    private int getScreenWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mGridMinWidth = metrics.widthPixels / 8;
        mGridMinHeight = metrics.heightPixels / 11 / 2;
        return mGridMinWidth;
    }

    //获得表头
    public void setTitle() {

        for (int i = 0; i < days.length; ++i) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight;

            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(days[i]);
            tvTitle.setGravity(Gravity.CENTER);
            //tvTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
            tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_head));
            gridLayout.addView(tvTitle, params);

        }
        for (int i = 0; i < 5; ++i) {
            gridLayout = (GridLayout) root.findViewById(R.id.grid);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            //rowSpec和columnSpec分别设置了一个按钮的位置是第几行和第几列。
            // 设置行列下标， 所占行列  ，比重
            // 对应： layout_row  , layout_rowSpan , layout_rowWeight
            params.rowSpec = GridLayout.spec(i + 1, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(0, 1);
            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(time[i]);
            tvTitle.setTextSize(10);
            tvTitle.setGravity(Gravity.CENTER);
            //tvTitle.setTextColor(Color.parseColor("#000"));
            tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_head));
            gridLayout.addView(tvTitle, params);
        }

    }

    //通过查询数据库获得每周的基础课程
    public void getClassesforNotSingle() throws IOException {
        //对剩余两个变量初始化为0，不然默认为上次操作之后3和4的值，影响课表
        Day3_Single=0;
        Day4_Single=0;

        //获取数据库对象
        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        //基础课程：所有的不分单双周的课+可能有一周必有一节的分单双周的课（它可能有两节，一节不分单双周，一节分）
        cursor = DB.rawQuery("SELECT Day1_NotSingle,Time1,Day2_NotSingle,Time2,course.CourseName,teacher.Name ,classroom.ClassRoomName,course.StartWeek,course.EndWeek FROM course,coursetime,teacher,classroom WHERE (course.CourseId = coursetime.CourseId AND course.TeacherNo = teacher.Nofortea  AND course.ClassRoomId= classroom.ClassRoomId AND SingleWeek=0 ) OR  (course.CourseId = coursetime.CourseId AND course.TeacherNo = teacher.Nofortea  AND course.ClassRoomId= classroom.ClassRoomId AND Day1_NotSingle NOTNULL AND SingleWeek=1 ) ", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Day1_NotSingle = cursor.getInt(cursor.getColumnIndex("Day1_NotSingle"));
            Time1 = cursor.getInt(cursor.getColumnIndex("Time1"));
            Day2_NotSingle = cursor.getInt(cursor.getColumnIndex("Day2_NotSingle"));
            Time2 = cursor.getInt(cursor.getColumnIndex("Time2"));
            getContent();
            while (cursor.moveToNext()) {
                Day1_NotSingle = cursor.getInt(cursor.getColumnIndex("Day1_NotSingle"));
                Time1 = cursor.getInt(cursor.getColumnIndex("Time1"));
                Day2_NotSingle = cursor.getInt(cursor.getColumnIndex("Day2_NotSingle"));
                Time2 = cursor.getInt(cursor.getColumnIndex("Time2"));
                getContent();
            }

        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
    }

    //通过查询数据库获得每周的课程
    public void getClassesforSingle() throws IOException {
        //获取数据库对象
        //对剩余两个变量初始化为0，不然默认为上次操作之后1和2的值，影响课表
        Day1_NotSingle=0;
        Day2_NotSingle=0;

        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        //查询有单双周的课：每周至少有一节单双周的课+可能有一节不分单双周/可能一周最多两节单双周的课
        cursor = DB.rawQuery("SELECT Day3_Single,Time3,Day4_Single,Time4,course.CourseName,teacher.Name ,classroom.ClassRoomName ,course.StartWeek ,course.EndWeek" +
                " FROM course,coursetime,teacher,classroom WHERE course.CourseId = coursetime.CourseId AND course.TeacherNo = teacher.Nofortea  AND course.ClassRoomId= classroom.ClassRoomId AND SingleWeek=1", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Day3_Single = cursor.getInt(cursor.getColumnIndex("Day3_Single"));
            Time3 = cursor.getInt(cursor.getColumnIndex("Time3"));
            Day4_Single = cursor.getInt(cursor.getColumnIndex("Day4_Single"));
            Time4 = cursor.getInt(cursor.getColumnIndex("Time4"));
            getContent();
            while (cursor.moveToNext()) {
                Day3_Single = cursor.getInt(cursor.getColumnIndex("Day3_Single"));
                Time3 = cursor.getInt(cursor.getColumnIndex("Time3"));
                Day4_Single = cursor.getInt(cursor.getColumnIndex("Day4_Single"));
                Time4 = cursor.getInt(cursor.getColumnIndex("Time4"));
                getContent();
            }

        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
    }


    //获得表中的内容
    public void getContent() {
        params = new GridLayout.LayoutParams();

        CourseName = cursor.getString(cursor.getColumnIndex("CourseName"));
        TeacherName = cursor.getString(cursor.getColumnIndex("Name"));
        CLassRoomName = cursor.getString(cursor.getColumnIndex("ClassRoomName"));
        StartWeek = cursor.getInt(cursor.getColumnIndex("StartWeek"));
        EndWeek = cursor.getInt(cursor.getColumnIndex("EndWeek"));

        //首先应该判断选择的周数 是否 在开课周数范围里面 ，如果不在，直接跳出
        if(ChooseWeek>=StartWeek&&ChooseWeek<=EndWeek)
        {
        if (Day1_NotSingle != 0) {
            params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            params.rowSpec = GridLayout.spec(Time1, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(Day1_NotSingle, 1);
            setContent();
        }
        if (Day2_NotSingle != 0) {
            params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            params.rowSpec = GridLayout.spec(Time2, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(Day2_NotSingle, 1);
            setContent();
        }
        if (Day3_Single != 0) {
            params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            params.rowSpec = GridLayout.spec(Time3, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(Day3_Single, 1);
            setContent();
        }
        if (Day4_Single != 0) {
            params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            params.rowSpec = GridLayout.spec(Time4, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(Day4_Single, 1);
            setContent();
        }



        }
    }

    public void setContent() {
        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(CourseName + "\n" + TeacherName + "\n" + CLassRoomName);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(Color.parseColor("#009688"));
        tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_tv));
        gridLayout.addView(tvTitle, params);
    }
}