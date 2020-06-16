package com.example.android_student_system;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReleaseDataBaseActivity {
    /** Called when the activity is first created. */
    //SD卡下的目录
    private final String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/student";
    //数据库名
    private final String DATABASE_FILENAME = "student.db";
    //这个context是必需的，没有context，怎么都不能实现数据库的拷贝操作；
    private Context context;
    //构造函数必需传入Context，数据库的操作都带有这个参数的传入
    public ReleaseDataBaseActivity(Context ctx) {
        this.context = ctx;
    }

    public SQLiteDatabase OpenDataBase() {
        try {
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            //判断SD卡下是否存在存放数据库的目录，如果不存在，新建目录
            if (!dir.exists()) {
                dir.mkdir();
            } else {
            }
            try {
                //如果数据库已经在SD卡的目录下存在，那么不需要重新创建，否则创建文件，并拷贝/res/raw下面的数据库文件
                if (!(new File(databaseFilename)).exists()) {
                    ///res/raw数据库作为输出流
                    InputStream is = this.context.getResources().openRawResource(
                            R.raw.student);
                    //测试用
                    int size = is.available();
                    //Log.i( "ReleaseDataBaseActivity", "DATABASE_SIZE:" + 1 );
                    //Log.i("ReleaseDataBaseActivity", "count:" + 0);
                    //用于存放数据库信息的数据流
                    FileOutputStream fos = new FileOutputStream(
                            databaseFilename);
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    //Log.i("ReleaseDataBaseActivity", "count:" + count);
                    //把数据写入SD卡目录下
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                }
            } catch (FileNotFoundException e) {
                //Log.e("Database", "File not found");
                e.printStackTrace();
            } catch (IOException e) {
                //Log.e("Database", "IO exception");
                e.printStackTrace();
            }
            //实例化sd卡上得数据库，database作为返回值，是后面所有插入，删除，查询操作的借口。
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;

        } catch (Exception e) {
        }
        return null;
    }

}
