package com.example.android_student_system;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class StuMainActivity extends AppCompatActivity {
    private Intent intent;
    private AppBarConfiguration mAppBarConfiguration;

    //侧滑
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //头部的view,不然是空指针
    private View headview;
    private View menuview;
    private TextView Name;
    private TextView Major_Class;
    private ListView LV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//添加头部条
/*        FloatingActionButton fab = findViewById(R.id.fab);
        //Snackbar 是 Android 5.0 新特性——Material Design 中的一个控件，用来代替 Toast
        // Snackbar与Toast的主要区别是：Snackbar可以滑动退出，也可以处理用户交互（点击）事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.person_information, R.id.score, R.id.about_class,
                R.id.evaluate)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        intent = getIntent();//获得login页面在数据库查询到的相关数据
        setInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void setInformation()
    {
       headview = navigationView.getHeaderView(0);//避免了重复出现头部部分 不要用inflateHeaderView()
       Name=headview.findViewById(R.id.stu_name);
       Major_Class=headview.findViewById(R.id.stu_major_class);
       Name.setText("你好呀 "+intent.getStringExtra("student_name"));
       Major_Class.setText(intent.getStringExtra("student_majorname")+"  "+intent.getStringExtra("student_classname"));
    }

    }




