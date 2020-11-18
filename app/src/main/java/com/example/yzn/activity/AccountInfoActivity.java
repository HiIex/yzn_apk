package com.example.yzn.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yzn.R;
import com.example.yzn.activity.ui.CircleImageView;
import com.example.yzn.activity.util.AccountInfo;
import com.example.yzn.activity.util.FriendApply;
import com.example.yzn.activity.util.HeadJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountInfoActivity extends AppCompatActivity {

    private Gson gson=new Gson();
    private Bitmap bitmap_head=null;

    @SuppressLint("HandlerLeak")
    Handler threadHandler=new Handler(){
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                //头像请求完成
                case 1:
                    CircleImageView circleImageView=(CircleImageView)findViewById(R.id.circleView);
                    if(bitmap_head!=null){
                        circleImageView.setImageBitmap(bitmap_head);
                        break;
                    }

                    //上传好友申请成功
                case 2:
                    Toast.makeText(AccountInfoActivity.this,"上传好友申请成功！",Toast.LENGTH_SHORT).show();
                    break;

                    //上传好友申请失败
                case -2:
                    Toast.makeText(AccountInfoActivity.this,"上传好友申请失败！",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        initView();
    }

    public void initView(){
        final Intent intent=getIntent();
        String qrJson=intent.getStringExtra("qrJson");
        Type type=new TypeToken<AccountInfo>(){}.getType();
        final AccountInfo accountInfo=gson.fromJson(qrJson,type);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/head?id="+accountInfo.getId());
                Type type1=new TypeToken<HeadJson>(){}.getType();
                HeadJson headJson=gson.fromJson(response,type1);
                if(headJson.getBase64Str()!=null){
                    bitmap_head=base64ToBitmap(headJson.getBase64Str());
                }else{
                    bitmap_head=null;
                }
                threadHandler.sendEmptyMessage(1);
            }
        }).start();

        //昵称
        TextView textView_nickname=(TextView)findViewById(R.id.textView_nickname);
        textView_nickname.setText(accountInfo.getNickname());

        //生日
        TextView textView_birthday=(TextView)findViewById(R.id.textView_birthday);
        textView_birthday.setText(accountInfo.getBirthday());

        //性别
        ImageView imageView_boy=(ImageView)findViewById(R.id.imageView_male);
        ImageView imageView_girl=(ImageView)findViewById(R.id.imageView_female);
        if(accountInfo.isSex()){
            imageView_boy.setVisibility(View.VISIBLE);
            imageView_girl.setVisibility(View.INVISIBLE);
        }else{
            imageView_boy.setVisibility(View.INVISIBLE);
            imageView_girl.setVisibility(View.VISIBLE);
        }

        //公司
        TextView textView_company=(TextView)findViewById(R.id.textView_company_content);
        textView_company.setText(accountInfo.getCompany());

        //职位
        TextView textView_job=(TextView)findViewById(R.id.textView_job_content);
        textView_job.setText(accountInfo.getPosition());

        //省份
        TextView textView_province=(TextView)findViewById(R.id.textView_province);
        textView_province.setText(accountInfo.getProvince());

        //城市
        TextView textView_city=(TextView)findViewById(R.id.textView_city);
        textView_city.setText(accountInfo.getCity());

        //返回
        ImageButton imageButton=(ImageButton)findViewById(R.id.title_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(AccountInfoActivity.this,AfterLoginActivity.class);
                startActivity(intent1);
            }
        });

        //加好友
        Button button_concern=(Button)findViewById(R.id.button_collect);
        button_concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(AccountInfoActivity.this);
                        FriendApply friendApply=new FriendApply(preferences.getString("id",null),accountInfo.getId());
                        String json=gson.toJson(friendApply);
                        String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/apply/post",json);
                        if(!response.equals("friend apply complete")){
                            threadHandler.sendEmptyMessage(-1);
                        }else{
                            threadHandler.sendEmptyMessage(2);
                        }

                    }
                }).start();
            }
        });

    }

    private String httpGet(String requestURL){
        String result=null;
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        try{
            URL url=new URL(requestURL);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "Content-Type=application/json");
            //设置客户端与服务连接类型
            connection.addRequestProperty("Connection", "Keep-Alive");
            //connection.setUseCaches(false);
            //connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.connect();

            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream inputStream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response=new StringBuilder();
                String line=null;
                while((line=reader.readLine())!=null){
                    response.append(line);
                    //response.append("\n");
                }
                inputStream.close();
                result=response.toString();
            }else{
                result="error";
            }

        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        return result;
    }

    private Bitmap base64ToBitmap(String code){
        Bitmap bitmap=null;
        try {
            if(code!=null){
                byte[] bitmapByte = Base64.decode(code, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            }else{
                bitmap=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bitmap_head!=null&&!bitmap_head.isRecycled()){
            bitmap_head.recycle();
        }
    }

    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    private String httpPost(String requestURL,String json){
        String result=null;
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        try{
            URL url=new URL(requestURL);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //设置客户端与服务连接类型
            connection.addRequestProperty("Connection", "Keep-Alive");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.connect();

            DataOutputStream outputStream=new DataOutputStream(connection.getOutputStream());
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();

            if(connection.getResponseCode()== HttpURLConnection.HTTP_OK){
                InputStream inputStream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response=new StringBuilder();
                String line=null;
                while((line=reader.readLine())!=null){
                    response.append(line);
                    //response.append("\n");
                }
                inputStream.close();
                result =response.toString();
                //textView.setText(response.toString());
            }else{
                result =String.valueOf(connection.getResponseCode());
                //textView.setText(connection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        return result;
    }
}