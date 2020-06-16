package com.example.android_student_system.student.ui.person_information;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.android_student_system.R;

import jp.wasabeef.glide.transformations.BlurTransformation;


/*每个文件夹下有两个文件，一个是用来承载控件的fragment，另一个是与之对应的viewModel。
viewModel就是mvvm框架下的vm，首先这两个文件全是普通的java类。fragment用来显示ui界面，而viewmodel则是给ui界面提供数据，
view里的每一个控件在viewmodel里都有一个对应的数据对象，如果要更新view上的ui界面，只需要更新viewmodel里与之对应的对象即可。*/

public class person_informationFragment extends Fragment {
    private Intent intent;
    private int getno;
    //StuMainActivity stu=new StuMainActivity();
    View root;
    private person_informationViewModel homeViewModel;
    private ImageView back;
    private TextView ChangePass;
    private ListView LV;
    private TextView Name;
    private TextView No;
    private String[] left = {"学院", "班级", "性别", "身份证号"};
    private int[] icons = {R.drawable.major, R.drawable.classes, R.drawable.sex, R.drawable.selfid};

    /* 创建 Fragment 时的一个区别是您必须使用 onCreateView() 回调定义布局
     * 类似Activity里面的onCreate（）*/
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(person_informationViewModel.class);
        root = inflater.inflate(R.layout.fragment_person_information, container, false);

        return root;
    }

    //布局创建好之后要执行什么
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = root.findViewById(R.id.back);
        Glide.with(getActivity())
                .load(R.drawable.meiko)
                .bitmapTransform(new BlurTransformation(getActivity(), 14, 3))//// "14":模糊度；"3":图片缩放3倍后再进行模糊，缩放3-5倍个人感觉比较好。
                .into(back);
        LV = root.findViewById(R.id.lv);
        Name=root.findViewById(R.id.name);
        No=root.findViewById(R.id.no);
        ChangePass=root.findViewById(R.id.changepass);
        MyAdapter adapter = new MyAdapter();
        LV.setAdapter(adapter);
        intent = getActivity().getIntent();//获得login页面在数据库查询到的相关数
        Name.setText(intent.getStringExtra("student_name"));
        No.setText(intent.getIntExtra("student_no",0)+"");

        ChangePass.setOnClickListener(new OnClickListenerChange());
        getno=intent.getIntExtra("student_no",0);
    }

    //点击【更改密码】，出现对话框输入新密码，修改DB
    private class OnClickListenerChange implements View.OnClickListener {
        public void onClick(View v) {
            MyDialog myDialog=new MyDialog(getContext(),getno);
            myDialog.show();
        }
    }


    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return left.length;//返回Item的总数
        }

        @Override
        public Object getItem(int position) {
            return left[position];//返回Item代表的对象
        }

        @Override
        public long getItemId(int position) {
            return position;//返回Item的id
        }

        @Override//得到View视图
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.list_item_personimformation, null);//加载Item布局
            TextView tv = view.findViewById(R.id.tv_left);
            tv.setText(left[position]);
            String[] right = {intent.getStringExtra("student_majorname"), intent.getStringExtra("student_classname"), intent.getStringExtra("student_sex"), intent.getStringExtra("student_id")};
            TextView tv2 = view.findViewById(R.id.tv_right);
            tv2.setText(right[position]);
            ImageView imageView = view.findViewById(R.id.img);
            imageView.setBackgroundResource(icons[position]);
            return view;
        }
    }
}