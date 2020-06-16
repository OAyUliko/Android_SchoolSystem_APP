package com.example.android_student_system.student.ui.evaluate;

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

import java.io.IOException;
import java.util.ArrayList;

public class evaluateFragment extends Fragment {
    private MyHelper myHelper;
    private DBManager dbHelper;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private String nowcoursename;
    private evaluateViewModel toolsViewModel;
    private TextView textView;
    private View root;
    private ListView LV;
    public static int num;
    private ArrayList<String> course = new ArrayList<>();
    //这个数组是对每个Item进行标记，点击过了就变成true，最后对整个数组进行判断，如果全部是true，则表示全部课程已完成评教
    public static ArrayList<Boolean> IsClicked=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(evaluateViewModel.class);
        root = inflater.inflate(R.layout.evaluatemainfragment, container, false);
        try {
            getCourse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<course.size(); i++){
            IsClicked.add(false);
        }
        textView = root.findViewById(R.id.textViewAA);

        LV.setOnItemClickListener(new MyOnItemClickListener());
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
        cursor = DB.rawQuery("SELECT CourseName FROM course WHERE TermYear=2019 AND TermId=2", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            nowcoursename = cursor.getString(cursor.getColumnIndex("CourseName"));
            course.add(nowcoursename);
            while (cursor.moveToNext()) {
                nowcoursename = cursor.getString(cursor.getColumnIndex("CourseName"));
                course.add(nowcoursename);
            }
        }
        MyAdapter adapter = new MyAdapter();
        LV.setAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return course.size();//返回Item的总数
        }

        @Override
        public Object getItem(int position) {
            return course.get(position);//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.list_item_course, null);//加载Item布局
            TextView tv = view.findViewById(R.id.tv_name);
            tv.setText(course.get(position));
            return view;
        }

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), evaluate.class);
            intent.putExtra("CourseName", course.get(position));
            intent.putExtra("CourseNun", cursor.getCount());//多少门课
            startActivity(intent);
            IsClicked.set(position,true);//对Item对应数组位置 进行改变
            try {
                HowManyItemNotClicked();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //全部完成教评的标志
    private void HowManyItemNotClicked() throws IOException {
        //测试代码：IsClicked是否随着每次点击发生变化，缺点就是，这里只是对点击进去进行了判断，而不是对进行提交 进行了判断
/*        for (Boolean b: IsClicked) {
            Log.v("luyao",b+"");
        }*/
        if(!IsClicked.contains(false))
            Toast.makeText(getContext(), "所有课程已完成评教！", Toast.LENGTH_SHORT).show();
        submittoDB();
    }

    //将交评完成结果填入DB，①是个人年度评教=1 ②是老师任课的交评成绩录入
    public void submittoDB() throws IOException {
        //获取数据库对象
        dbHelper = new DBManager(getContext());
        myHelper = new MyHelper(getContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("UPDATE student SET pingjiao2020 = 1 WHERE No=201707771;", null);
        cursor.moveToFirst();
        cursor.close();
        dbHelper.closeDatabase();
    }
}