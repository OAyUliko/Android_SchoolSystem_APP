package com.example.android_student_system.admin.ui.course_mgt;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android_student_system.R;
import com.example.android_student_system.admin.ui.student_mgt.studentmgtFragment;
import com.example.android_student_system.admin.ui.stuff_mgt.stuffmgtFragment;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;
import com.example.android_student_system.student.ui.evaluate.evaluateViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class coursemgtFragment extends Fragment {
    private evaluateViewModel toolsViewModel;
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private View root;
    private int mGridMinWidth;
    private int mGridMinHeight;
    private GridLayout gridLayout;
    private GridLayout.LayoutParams params;
    private int Day1_NotSingle;
    private int Day2_NotSingle;
    private int Day3_Single;
    private int Day4_Single;
    private int StartWeek;
    private int EndWeek;
    private String CourseName;
    private String TeacherName;
    private String CLassRoomName;
    private int Time1;
    private int Time2;
    private int Time3;
    private int Time4;
    private static String Info;
    private static ArrayList<String> myArray = new ArrayList<>();
    private String[] days = new String[]{"  ", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] time = new String[]{"第一节课", "第二节课", "第三节课", "第四节课", "第五节课"};
    private int SelectedId;
    private int SelectedId2;
    private int SelectedId3;
    private int SelectedId4;

    private Button BAdd;
    private Button BDelete;
    private Button BUpdate;
    private Button BScore;
    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private EditText E5;
    private EditText E6;
    private EditText E7;
    private EditText E8;
    private EditText E9;
    private EditText E10;
    private EditText E11;
    private Spinner spinner1;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter3;
    private ArrayAdapter<String> adapter4;
    private ArrayAdapter<String> adapter5;
    private Spinner spinner3;
    private Spinner spinner4;
    private Spinner spinner5;
    private Button BUP;
    private Button BAD;

    private ArrayList<String> CourseStyle = new ArrayList<>();
    private ArrayList<String> CourseIsNotHasSingle = new ArrayList<>();
    private ArrayList<String> TeacherNameS = new ArrayList<>();
    private ArrayList<String> ClassRoomNameS = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.fragment_course_mgt, container, false);

        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());

        BAdd = root.findViewById(R.id.BAdd4);
        BDelete = root.findViewById(R.id.BDelete4);
        BUpdate = root.findViewById(R.id.BUpdate4);

        getScreenWidth();
        setTitle();
        try {
            getClassesforNotSingle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            getClassesforSingle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEmptyDialog();
            }
        });

        cursor.close();
        dbHelper.closeDatabase();
        return root;
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
        gridLayout = (GridLayout) root.findViewById(R.id.grid2);
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
            tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_head));
            gridLayout.addView(tvTitle, params);
        }

    }

    //通过查询数据库获得每周的基础课程
    public void getClassesforNotSingle() throws IOException {
        //对剩余两个变量初始化为0，不然默认为上次操作之后3和4的值，影响课表
        Day3_Single = 0;
        Day4_Single = 0;

        //获取数据库对象
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
    }

    //通过查询数据库获得每周的课程
    public void getClassesforSingle() throws IOException {
        //获取数据库对象
        //对剩余两个变量初始化为0，不然默认为上次操作之后1和2的值，影响课表
        Day1_NotSingle = 0;
        Day2_NotSingle = 0;

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

    public void getContent() {
        params = new GridLayout.LayoutParams();

        CourseName = cursor.getString(cursor.getColumnIndex("CourseName"));
        TeacherName = cursor.getString(cursor.getColumnIndex("Name"));
        CLassRoomName = cursor.getString(cursor.getColumnIndex("ClassRoomName"));
        StartWeek = cursor.getInt(cursor.getColumnIndex("StartWeek"));
        EndWeek = cursor.getInt(cursor.getColumnIndex("EndWeek"));

        //首先应该判断选择的周数 是否 在开课周数范围里面 ，如果不在，直接跳出
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
            setContentforSingle();
        }
        if (Day4_Single != 0) {
            params = new GridLayout.LayoutParams();
            params.width = mGridMinWidth;
            params.height = mGridMinHeight * 2;
            params.rowSpec = GridLayout.spec(Time4, 1, GridLayout.UNDEFINED);
            params.columnSpec = GridLayout.spec(Day4_Single, 1);
            setContentforSingle();
        }
    }

    //跳转逻辑：先选择一个格子的课程，再点下面的修改，再进入dialog里面修改，修改完了点dialog里面的确认按钮
    public void setContent() {
        final TextView tvTitle = new TextView(getContext());
        tvTitle.setText(CourseName + "\n" + TeacherName + "\n" + CLassRoomName + "\n" + StartWeek + "~" + EndWeek + "周");
        tvTitle.setTextSize(12);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(Color.parseColor("#009688"));
        tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_tv));
        gridLayout.addView(tvTitle, params);

        //为点击的textview添加监听，这里就不要想着对gridlayout进行处理了，而是对他里面的内容进行监听
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info = tvTitle.getText().toString();
            }
        });

        BUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CreateDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        BDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strArr = Info.split("\n");//获得课程名作为查找项
                ArrayList<String> myArray2 = new ArrayList();
                myArray2.add(strArr[0]);
                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DB = myHelper.getReadableDatabase();//获得可读写的对象
                cursor = DB.rawQuery("SELECT course.CourseId FROM course,coursetime WHERE course.CourseName = ? AND course.CourseId=coursetime.CourseId ", new String[]{myArray2.get(0)});
                cursor.moveToFirst();
                String no=cursor.getString(cursor.getColumnIndex("CourseId"));

                //DB = myHelper.getWritableDatabase();//获得可读写的对象
                ////只是删除了登陆表中的信息，不是删除了学生表的
                //DB.execSQL("DELETE FROM coursetime WHERE CourseId=? ;", new Object[]{no});
                Toast.makeText(getContext(), myArray2.get(0) + "课程删除成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //这里跟setContent差不多 一个点没想到就是 对于单周的课，她更改课时间的时候很难改，要说明是改之后是单周还是不分的
    public void setContentforSingle() {
        final TextView tvTitle = new TextView(getContext());
        tvTitle.setText(CourseName + "\n" + TeacherName + "\n" + CLassRoomName + "\n" + StartWeek + "~" + EndWeek + "周" + "\n" + "(单周)");
        tvTitle.setTextSize(12);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(Color.parseColor("#009688"));
        tvTitle.setBackground(getResources().getDrawable(R.drawable.shape_tv));

        gridLayout.addView(tvTitle, params);
        //为点击的textview添加监听，这里就不要想着对gridlayout进行处理了，而是对他里面的内容进行监听
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), tvTitle.getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //重写simpleadapterd的getview方法


    public void CreateDialog() throws IOException {
        // 动态加载一个listview的布局文件进来
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View getlistview = inflater.inflate(R.layout.showcourseinformation, null);

        E1 = getlistview.findViewById(R.id.ECN);
        E2 = getlistview.findViewById(R.id.ECC);
        E3 = getlistview.findViewById(R.id.ECY);
        E4 = getlistview.findViewById(R.id.ECSW);
        E5 = getlistview.findViewById(R.id.ECEW);
        E6 = getlistview.findViewById(R.id.ECI);
        E7 = getlistview.findViewById(R.id.EONE);
        E8 = getlistview.findViewById(R.id.EONE1);
        E9 = getlistview.findViewById(R.id.ETWO);
        E10 = getlistview.findViewById(R.id.ETWO1);
        E11 = getlistview.findViewById(R.id.ECT);
        spinner1 = getlistview.findViewById(R.id.SCSty);
        spinner3 = getlistview.findViewById(R.id.SCS);
        spinner4 = getlistview.findViewById(R.id.SCCR);
        spinner5 = getlistview.findViewById(R.id.SCT);
        BUP = getlistview.findViewById(R.id.ButtonUp);
        BAD=getlistview.findViewById(R.id.ButtonUp);
        GetSpinner();
        SetSpinner();

        String[] strArr = Info.split("\n");//获得课程名作为查找项
        myArray = new ArrayList();
        myArray.add(strArr[0]);

        //获取数据库对象
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("SELECT * FROM course,coursetime,teacher WHERE course.CourseName = ? AND course.CourseId=coursetime.CourseId AND course.TeacherNo=teacher.Nofortea\n", new String[]{myArray.get(0)});
        cursor.moveToFirst();
        E1.setText(cursor.getString(cursor.getColumnIndex("CourseName")));
        E2.setText(cursor.getString(cursor.getColumnIndex("Credit")));
        E3.setText(cursor.getString(cursor.getColumnIndex("TermYear")));
        E4.setText(cursor.getString(cursor.getColumnIndex("StartWeek")));
        E5.setText(cursor.getString(cursor.getColumnIndex("EndWeek")));
        E6.setText(cursor.getString(cursor.getColumnIndex("CourseId")));

        E11.setText(cursor.getInt(cursor.getColumnIndex("TermId")) + "");

        //cursor.getInt(cursor.getColumnIndex("CourseType"));
        spinner1.setSelection(cursor.getInt(cursor.getColumnIndex("CourseType")) - 1);
        spinner3.setSelection(cursor.getInt(cursor.getColumnIndex("SingleWeek")));
        for (int i = 0; i < TeacherNameS.size(); i++) {
            if (cursor.getString(cursor.getColumnIndex("Name")).equals(TeacherNameS.get(i))) {
                spinner5.setSelection(i);// 选中项
                break;
            }
        }

        //设置课时间
        int D1 = cursor.getInt(cursor.getColumnIndex("Day1_NotSingle"));
        int D2 = cursor.getInt(cursor.getColumnIndex("Day2_NotSingle"));
        int D3 = cursor.getInt(cursor.getColumnIndex("Day3_Single"));
        int D4 = cursor.getInt(cursor.getColumnIndex("Day4_Single"));

        if (D1 != 0) {
            E7.setText(cursor.getString(cursor.getColumnIndex("Day1_NotSingle")));
            E8.setText(cursor.getString(cursor.getColumnIndex("Time1")));
            if (D2 != 0) {
                E9.setText(cursor.getString(cursor.getColumnIndex("Day2_NotSingle")));
                E10.setText(cursor.getString(cursor.getColumnIndex("Time2")));
            }
            if (D3 != 0) {
                E7.setText(cursor.getString(cursor.getColumnIndex("Day3_Single")));
                E8.setText(cursor.getString(cursor.getColumnIndex("Time3")));
            }
        } else {
            E7.setText(cursor.getString(cursor.getColumnIndex("Day3_Single")));
            E8.setText(cursor.getString(cursor.getColumnIndex("Time3")));
            if (D4 != 0) {
                E9.setText(cursor.getString(cursor.getColumnIndex("Day4_Single")));
                E10.setText(cursor.getString(cursor.getColumnIndex("Time4")));
            }
        }

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setView(getlistview);
        builder.create().show();

        BUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EE1 = E1.getText() + "";
                String E22 = E2.getText() + "";
                String E33 = E3.getText() + "";
                String E44 = E4.getText() + "";
                String E55 = E5.getText() + "";
                String E66 = E6.getText() + "";
                String E77 = E7.getText() + "";
                String E88 = E8.getText() + "";
                String E99 = E9.getText() + "";
                String E100 = E10.getText() + "";
                String E111 = E11.getText() + "";
                int SP1 = SelectedId;//类别 直接写
                int SP2 = SelectedId2;//单双周 直接写
                int SP3 = SelectedId3+1;//教室 从0开始
                int SP4 = SelectedId4;//教师
                String Room = ClassRoomNameS.get(SP3);
                String Tea = TeacherNameS.get(SP4);


                try {
                    dbHelper.openDatabase();//连接数据库
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SQLiteDatabase DB2;
                DB2 = myHelper.getReadableDatabase();//获得可读写的对象
                cursor = DB2.rawQuery("SELECT Nofortea FROM teacher WHERE  Name=? ;", new String[]{Tea});
                cursor.moveToFirst();
                int No = cursor.getInt(cursor.getColumnIndex("Nofortea"));

               DB = myHelper.getWritableDatabase();//获得可读写的对象
                //只是删除了登陆表中的信息，不是删除了学生表的

                DB.execSQL("UPDATE course  SET CourseName =?, TeacherNo=?, StartWeek=?, EndWeek=?, CourseType=?,TermYear=?,TermId=?,ClassRoomId=?,Credit=?,SingleWeek=?" +
                        "WHERE CourseId=? ;", new Object[]{EE1,No,E44,E55,SP1,E33,E111,SP3,E22,SP2,E66});
                DB.execSQL("UPDATE coursetime SET Day1_NotSingle=?,Time1=?,Day2_NotSingle=?,Time2=? " +
                        "WHERE CourseId =? ;", new Object[]{E77,E88,E99,E100,E66});
                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void CreateEmptyDialog() {
        // 动态加载一个listview的布局文件进来
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View getlistview = inflater.inflate(R.layout.showcourseinformation, null);
        spinner1 = getlistview.findViewById(R.id.SCSty);
        spinner3 = getlistview.findViewById(R.id.SCS);
        spinner4 = getlistview.findViewById(R.id.SCCR);
        spinner5 = getlistview.findViewById(R.id.SCT);
        BAD=getlistview.findViewById(R.id.ButtonAd);
        GetSpinner();
        SetSpinner();

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setView(getlistview);
        builder.create().show();

        BAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void SetSpinner() {
        //将可选内容与ArrayAdapter连接起来
        adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, CourseStyle);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner1.setVisibility(View.VISIBLE);

        adapter3 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, CourseIsNotHasSingle);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(new SpinnerSelectedListener2());
        spinner3.setVisibility(View.VISIBLE);

        adapter4 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ClassRoomNameS);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter4);
        spinner4.setOnItemSelectedListener(new SpinnerSelectedListener3());
        spinner4.setVisibility(View.VISIBLE);

        adapter5 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, TeacherNameS);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter5);
        spinner5.setOnItemSelectedListener(new SpinnerSelectedListener4());
        spinner5.setVisibility(View.VISIBLE);
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            SelectedId = arg2 + 1;
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            SelectedId2 = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener3 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            SelectedId3 = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener4 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            SelectedId4 = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    private void GetSpinner() {
        CourseStyle.clear();
        CourseIsNotHasSingle.clear();
        TeacherNameS.clear();
        ClassRoomNameS.clear();
        CourseStyle.add("必修");//1 2 3    +1
        CourseStyle.add("公选");
        CourseStyle.add("实践");
        CourseIsNotHasSingle.add("否");//0 1  不变
        CourseIsNotHasSingle.add("是");

        try {
            dbHelper.openDatabase();//连接数据库
        } catch (IOException e) {
            e.printStackTrace();
        }
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("SELECT Name FROM teacher;", null);
        cursor.moveToFirst();
        TeacherNameS.add(cursor.getString(cursor.getColumnIndex("Name")));
        while (cursor.moveToNext()) {
            TeacherNameS.add(cursor.getString(cursor.getColumnIndex("Name")));
        }

        cursor = DB.rawQuery("SELECT ClassRoomName FROM classroom;", null);
        cursor.moveToFirst();
        ClassRoomNameS.add(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
        while (cursor.moveToNext()) {
            ClassRoomNameS.add(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
        }

    }




}