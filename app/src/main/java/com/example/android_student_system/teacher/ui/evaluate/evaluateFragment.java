package com.example.android_student_system.teacher.ui.evaluate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.example.android_student_system.student.ui.evaluate.evaluateViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class evaluateFragment extends Fragment {
    private evaluateViewModel toolsViewModel;
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;

    private View root;
    private ListView LV;
    private Intent intent;

    private TextView AA;
    private TextView BB;
    private ArrayList<String> CourseName=new ArrayList<>();
    private ArrayList<Integer> CourseEvaluate=new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.evaluatemainfragment, container, false);

        AA=root.findViewById(R.id.textViewAA);
        BB=root.findViewById(R.id.textViewBB);
        AA.setText("您的教学评分如下");
        BB.setText("");

        try {
            getCourse();
        } catch (IOException e) {
            e.printStackTrace();
        }



        cursor.close();
        dbHelper.closeDatabase();

        return root;
    }

    //onActivityResult()方法是从Activity下发的，即首先接收数据的是Activity，
    // 通过super.onActivityResult()方法来判断是由Activity还是Activity内嵌的fragment 来接收数据，
    // 所以，如果Activity中内嵌的Fragment要接收由Fragment调用的StartActivityForResult()方法开启的Activity回传的数据的收，
    // 该Fragment所在的Activity就一定要复写super.onActivityResult()方法，不能省略

    public void getCourse() throws IOException {
        LV = root.findViewById(R.id.courselv);
        //获取数据库对象
        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        intent = getActivity().getIntent();//获得login页面在数据库查询到的相关数
        cursor = DB.rawQuery("SELECT course.Score_pingjiao,CourseName FROM course ,teacher WHERE teacher.Nofortea = course.TeacherNo AND teacher.Nofortea=? AND course.Score_pingjiao IS NOT NULL", new String[]{intent.getIntExtra("teacher_no",0)+""});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            CourseName.add(cursor.getString(cursor.getColumnIndex("CourseName")));
            CourseEvaluate.add(cursor.getInt(cursor.getColumnIndex("Score_pingjiao")));
            while (cursor.moveToNext()) {
                CourseName.add(cursor.getString(cursor.getColumnIndex("CourseName")));
                CourseEvaluate.add(cursor.getInt(cursor.getColumnIndex("Score_pingjiao")));
            }
        }
        MyAdapter adapter = new MyAdapter();
        LV.setAdapter(adapter);
    }

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
            View view = View.inflate(getContext(), R.layout.list_item_course2, null);//加载Item布局
            TextView tv = view.findViewById(R.id.tv_name2);
            tv.setText(CourseName.get(position));
            TextView tv2 = view.findViewById(R.id.tv_score2);
            tv2.setText(CourseEvaluate.get(position)+"");
            return view;
        }

    }


}