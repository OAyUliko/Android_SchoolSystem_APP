package com.example.android_student_system;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jsoup.Connection;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    String cookie;
    String jsess;
    String gsess;
    @Test
    public void useAppContext() throws IOException {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //assertEquals("com.example.android_student_system", appContext.getPackageName());
        //其实就是使用程序去模拟浏览器的行为，向服务器请求和发送数据
        //获取cookie

        URL url = new URL("http://jwc3.yangtzeu.edu.cn/eams/login.action");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.out.println("连接失败");
        }
        conn.setReadTimeout(1000);//设置请求超时时间
        if (conn.getResponseCode() != 200)
            System.out.println("error");
        String str = conn.getHeaderField("Set-Cookie");//获取Set-Cookie
        int begin = str.indexOf("adc-ck-jwxt_pools");
        int end = str.indexOf(';', begin);
        cookie = str.substring(18, end);

        String str2=conn.getHeaderField(3);
        jsess = str2.substring(11, str2.indexOf(';', begin));
        gsess=jsess;
        /*String str3=conn.getHeaderField(4);
        gsess = str.substring(str2.indexOf("GSESSIONID"), str2.indexOf(';', begin));*/
/*        Request request = new Request.Builder()
                .url("http://jwc3.yangtzeu.edu.cn/eams/home.action")*/
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                //.add("GSESSIONID", "AAD795B1B4201ECD96DCE5395E63999D.node138")
                //.add("JSESSIONID", "AAD795B1B4201ECD96DCE5395E63999D.node138")
                //.add("__guid", "243026329.2947726295430014500.1588683002300.5115")
                //.add("adc-ck-jwxt_pools",cookie)
                .add("username","201707771")
                .add("password","6987a9bcdd7452c686e8d5b355805cf666095c1e")
                //.add("monitor_count","8")
                .add("encodedPassword","")
               /* .add("semester.id","109")
                .add("project.id","1")
                .add("ignoreHead","1")
                .add("setting.kind","std")
                .add("ids","411226")
                .add("startWeek","10")*/
                .add("session_locale:","zh_CN")
                .build();
        Request request1 = new Request.Builder()
                .url("http://jwc3.yangtzeu.edu.cn/eams/home.action")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                //.header("Accept", "*/*")
                //.header("Accept-Encoding", "gzip, deflate")
                //这样可以注释掉，不注释的话返回数据乱码
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                //.header("Cache-Control", "max-age=0")
                //.header("Content-Type", "application/x-www-form-urlencoded")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Connection", "keep-alive")
                .header("Cookie", "JSESSIONID="+jsess+"; GSESSIONID="+gsess+"adc-ck-jwxt_pools="+cookie+"; __guid=243026329.3392287921113946000.1588752857715.7922;monitor_count=8")
                .header("Host", "jwc3.yangtzeu.edu.cn")
                .header("Referer", "http://jwc3.yangtzeu.edu.cn/eams/login.action")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                .post(requestBody)
                .build();


//        Request request = new Request.Builder()
//                .url("http://jwc3.yangtzeu.edu.cn/eams/home.action")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
//                 .header("Accept-Encoding", "gzip, deflate")
//                //这样可以注释掉，不注释的话返回数据乱码
//                .header("Accept-Language", "zh-CN,zh;q=0.9")
//                .header("Cache-Control", "max-age=0")
//                .header("Accept-Language", "zh-CN,zh;q=0.9")
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .header("Connection", "keep-alive")
//                .header("Cookie", "JSESSIONID=AAD795B1B4201ECD96DCE5395E63999D.node138; adc-ck-jwxt_pools=IKALAKAK; __guid=243026329.2947726295430014500.1588683002300.5115; GSESSIONID=AAD795B1B4201ECD96DCE5395E63999D.node138; monitor_count=3")
//                .header("Host", "jwc3.yangtzeu.edu.cn")
//                .header("Referer", "http://jwc3.yangtzeu.edu.cn/eams/login.action;jsessionid=AAD795B1B4201ECD96DCE5395E63999D.node138")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
//                .build();

/*                .url("http://jwc3.yangtzeu.edu.cn/eams/home.action")
                .post(requestBody)
                .build();*/

        Response response = client.newCall(request1).execute();

        String responseData = response.body().string();
        //System.out.println(responseData);


 /*       Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        showResponse(responseData);


                .url("/eams/home.action")
                .post(requestBody)
                .build();
*/


    }



}
