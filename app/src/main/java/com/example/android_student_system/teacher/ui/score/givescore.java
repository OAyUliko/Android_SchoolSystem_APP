package com.example.android_student_system.teacher.ui.score;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_student_system.R;
import com.example.android_student_system.db.DBManager;
import com.example.android_student_system.db.MyHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class givescore extends AppCompatActivity {
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
    ArrayList<Integer> S = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_givescore);

        listView = findViewById(R.id.student_name);
        button = findViewById(R.id.SubmitButton);

        intent = getIntent();//获得选择课程页面选择的课程名

        TV = findViewById(R.id.score_name);
        TV.setText("请对 " + intent.getStringExtra("CourseName") + " 课程进行成绩提交");
        try {
            GetInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    public void Submit(View view) throws IOException {
/*测试代码：看成绩录入正确没有
        for (int m = 0; m < StudentName.size(); m++) {
            System.out.println(S.get(m));
        }
*/
        dbHelper = new DBManager(getBaseContext());
        myHelper = new MyHelper(getBaseContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getWritableDatabase();//获得可读写的对象
        for (int m = 0; m < StudentName.size(); m++) {
            DB.execSQL("UPDATE score_软工11701 SET Score = ? WHERE score_软工11701.CourseId=(select score_软工11701.CourseId from course,score_软工11701 where course.CourseId = score_软工11701.CourseId AND CourseName=?) AND StudentNo= ?;", new Object[]{S.get(m), intent.getStringExtra("CourseName") ,StudentNo.get(m)});
        }
        cursor.close();//关闭DB 释放资源
        dbHelper.closeDatabase();
        Toast.makeText(this,"成绩提交成功！",Toast.LENGTH_SHORT).show();
        TAG=1;
    }

    private void GetInfo() throws IOException {
        dbHelper = new DBManager(getBaseContext());
        myHelper = new MyHelper(getBaseContext());
        dbHelper.openDatabase();//连接数据库
        DB = myHelper.getReadableDatabase();//获得可读写的对象
        cursor = DB.rawQuery("SELECT student.No , student.Name, Class FROM score_软工11701 , student ,course WHERE course.CourseName=? AND score_软工11701.StudentNo= student.No AND score_软工11701.CourseId=course.CourseId", new String[]{intent.getStringExtra("CourseName")});
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
    }

    public void saveEditData() {
        for (int m = 0; m < 3; m++) {
            System.out.println(m);
        }
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
            ViewHolder holder;
            if (null == convertView) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_item_singlecourse, null);
                holder = new ViewHolder(convertView, position);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            holder.tv.setText("学生姓名：" + StudentName.get(position));
            holder.tv2.setText("学号：" + StudentNo.get(position));
            holder.tv3.setText("班级：" + StudentClass.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView tv;
            TextView tv2;
            TextView tv3;
            EditText editText;

            public ViewHolder(View view, int position) {
                tv = view.findViewById(R.id.StudentName);
                tv2 = view.findViewById(R.id.StudentNo);
                tv3 = view.findViewById(R.id.StudentClass);
                editText = view.findViewById(R.id.value);

                editText.setTag(position);//存tag值
                editText.addTextChangedListener(new TextSwitcher(this));
            }
        }

        class TextSwitcher implements TextWatcher {
            private ViewHolder mHolder;

            public TextSwitcher(ViewHolder mHolder) {
                this.mHolder = mHolder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            ////当前状态的前一个状态的值
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int position = (int) mHolder.editText.getTag();//取tag值
                S.add(position, Integer.parseInt(s.toString()));
                Handler handler = new Handler();//延迟监听，不然每次改一个字符就调用一次，很不方便
                if (handler.hasMessages(3)) {
                    handler.removeMessages(3);
                }
                handler.sendEmptyMessageDelayed(3, 4000);
                saveEditData();
            }

        }


    }
}
