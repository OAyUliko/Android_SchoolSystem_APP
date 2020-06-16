package com.example.android_student_system.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.android_student_system.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBManager {
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "student.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.example.android_student_system";
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() +"/"+ PACKAGE_NAME+"/databases/";
    //在手机里存放数据库的位置(/data/data/com.example.android_student_system/databases/student.db)
    private SQLiteDatabase database;
    private Context context;


    public DBManager(Context context) {
        this.context = context;
    }


    public SQLiteDatabase getDatabase() {
        return database;
    }


    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }


    public void openDatabase() throws IOException {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }


    private SQLiteDatabase openDatabase(String dbfile) throws IOException {
            if (!(new File(dbfile).exists())) {
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(R.raw.student); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            return db;
    }

    public void closeDatabase() {
        this.database.close();
    }
}
