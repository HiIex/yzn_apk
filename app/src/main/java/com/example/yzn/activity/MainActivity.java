package com.example.yzn.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yzn.R;
import com.example.yzn.activity.security.Config;
import com.example.yzn.activity.security.RSACipher;
import com.example.yzn.activity.ui.CircleImageView;
import com.example.yzn.activity.ui.NbButton;
import com.example.yzn.activity.util.Bill;
import com.example.yzn.activity.util.BillJson;
import com.example.yzn.activity.util.FriendJson;
import com.example.yzn.activity.util.IDRequest;
import com.example.yzn.activity.util.IDResponse;
import com.example.yzn.activity.util.LoginRequest;
import com.example.yzn.activity.util.LoginResult;
import com.example.yzn.activity.util.MyDatabaseHelper;
import com.example.yzn.activity.util.RSAKeyResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public String studyNum;
    public String password;

    private NbButton button;
    private RelativeLayout rlContent;
    private Handler handler;
    private Animator animator;

    public static int displayWidth;  //屏幕宽度
    public static int displayHeight; //屏幕高度

    private boolean clickLogin=true;//区别于注册

    private int flag=0;

    private Thread initThread;
    private Bitmap bitmap_photo=null;

    public final static String IP="210.41.103.237";
    public final static String PORT="8080";

    //缓存路径
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";

    //停止转圈，进入页面
    private boolean stopTurnAround=false;
    public ArrayList<Bill> billArrayList;

    //加载状态
    private static boolean BILL_GET_COMLPETE=false;
    private static boolean FRIEND_GET_COMOLETE=false;
    private static boolean FRIEND_HEAD_GET_COMPLETE=false;

    private MyDatabaseHelper myDatabaseHelper;

    @SuppressLint("HandlerLeak")
    Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //请求订单结束
                case 1:
                    if(FRIEND_HEAD_GET_COMPLETE&&FRIEND_GET_COMOLETE){
                        stopTurnAround=true;
                        BILL_GET_COMLPETE=false;
                        FRIEND_GET_COMOLETE=false;
                        FRIEND_HEAD_GET_COMPLETE=false;
                        gotoNew();
                    }else{
                        BILL_GET_COMLPETE=true;
                    }
                    break;

                    //加载好友列表完成
                case 6:
                    if(BILL_GET_COMLPETE&&FRIEND_HEAD_GET_COMPLETE){
                        stopTurnAround=true;
                        BILL_GET_COMLPETE=false;
                        FRIEND_GET_COMOLETE=false;
                        FRIEND_HEAD_GET_COMPLETE=false;
                        gotoNew();
                    }else{
                        FRIEND_GET_COMOLETE=true;
                    }

                    break;


                    //加载好友列表的头像结束
                case 7:
                    if(BILL_GET_COMLPETE&&FRIEND_GET_COMOLETE){
                        stopTurnAround=true;
                        BILL_GET_COMLPETE=false;
                        FRIEND_GET_COMOLETE=false;
                        FRIEND_HEAD_GET_COMPLETE=false;
                        gotoNew();
                    }else{
                        FRIEND_HEAD_GET_COMPLETE=true;
                    }
                    break;

                    //登录失败
                case -1:
                    button.regainBackground();
                    Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    break;

                    //登录成功
                case 2:
                    final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    //加载订单
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Gson gson=new Gson();
                                Type type=new TypeToken<List<BillJson>>(){}.getType();
                                String response=httpGet("http://"+IP+":"+PORT+"/sys/user/download");
                                //threadHandler.sendEmptyMessage(-1);
                                List<BillJson> billJsonList=gson.fromJson(response,type);
                                //threadHandler.sendEmptyMessage(-2);
                                //initBitmaps(billJsonList);
                                billArrayList=new ArrayList<>();
                                AfterLoginActivity.billJsonArrayList=billJsonList;
                                threadHandler.sendEmptyMessage(1);

                                //向SQLite更新全部数据
                                myDatabaseHelper=new MyDatabaseHelper(MainActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
                                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                                db.delete("bill",null,null);
                                for(int i=0;i<AfterLoginActivity.billJsonArrayList.size();i++){
                                    BillJson billJson=AfterLoginActivity.billJsonArrayList.get(i);
                                    ContentValues contentValues=new ContentValues();
                                    contentValues.put("billID",billJson.getBillID());
                                    contentValues.put("issureID",billJson.getIssuerID());
                                    contentValues.put("productName",billJson.getProductName());
                                    contentValues.put("price",billJson.getPrice());
                                    contentValues.put("type",billJson.getType());
                                    contentValues.put("middleName",billJson.getMiddleName());
                                    contentValues.put("currency",billJson.getCurrency());
                                    contentValues.put("detail",billJson.getDetail());
                                    db.insert("bill",null,contentValues);
                                    contentValues.clear();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                AfterLoginActivity.billJsonArrayList=new ArrayList<>();
                                threadHandler.sendEmptyMessage(1);
                                //向SQLite更新全部数据
                                myDatabaseHelper=new MyDatabaseHelper(MainActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
                                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                                db.delete("bill",null,null);
                            }
                        }
                    }).start();

                    //加载好友列表
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                String response=httpGet("http://"+ MainActivity.IP+":"+MainActivity.PORT+"/message/friend/get?id="+preferences.getString("id",""));
                                Gson gson=new Gson();
                                Type type=new TypeToken<List<FriendJson>>(){}.getType();
                                AfterLoginActivity.friendList=gson.fromJson(response,type);
                                threadHandler.sendEmptyMessage(6);

                                //向SQLite更新全部数据
                                myDatabaseHelper=new MyDatabaseHelper(MainActivity.this,"friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
                                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                                db.delete("friend",null,null);
                                if(AfterLoginActivity.friendList.size()>0){
                                    for(int i=0;i<AfterLoginActivity.friendList.size();i++){
                                        FriendJson friendJson=AfterLoginActivity.friendList.get(i);
                                        ContentValues contentValues=new ContentValues();
                                        contentValues.put("id",friendJson.getFriendid());
                                        contentValues.put("nickname",friendJson.getNickname());
                                        contentValues.put("remark",friendJson.getRemark());
                                        contentValues.put("time",friendJson.getTime());
                                        db.insert("friend",null,contentValues);
                                        contentValues.clear();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                AfterLoginActivity.friendList=new ArrayList<>();
                                threadHandler.sendEmptyMessage(6);
                                myDatabaseHelper=new MyDatabaseHelper(MainActivity.this,"friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
                                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                                db.delete("friend",null,null);
                            }

                        }
                    }).start();

                    //加载好友列表的头像
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/head/get?id="+preferences.getString("id",""));
                                Gson gson=new Gson();
                                Type type=new TypeToken<List<String>>(){}.getType();
                                List<String> headStringList=gson.fromJson(response,type);
                                AfterLoginActivity.headList=new ArrayList<>();
                                if(AfterLoginActivity.headList.size()>0){
                                    for(int i=0;i<headStringList.size();i++){
                                        if(headStringList.get(i)==null){
                                            AfterLoginActivity.headList.add(null);
                                        }else{
                                            AfterLoginActivity.headList.add(base64ToBitmap(headStringList.get(i)));
                                        }
                                    }
                                }

                                threadHandler.sendEmptyMessage(7);
                            }catch (Exception e){
                                e.printStackTrace();
                                AfterLoginActivity.headList=new ArrayList<>();
                                threadHandler.sendEmptyMessage(-7);
                            }
                        }
                    }).start();

                    final CheckBox checkBox_rememerPwd=(CheckBox)findViewById(R.id.remember_pwd);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clickLogin=true;
                            SharedPreferences.Editor editor=preferences.edit();
                            if(checkBox_rememerPwd.isChecked()){
                                editor.putBoolean("rememberPassword",true);
                                editor.putString("number",studyNum);
                                editor.putString("password",password);
                            }else{
                                editor.putBoolean("rememberPassword",false);
                                editor.putString("number",studyNum);
                                editor.putString("password",password);
                            }
                            //预加载历史，初始化任务池,请求科普
                            editor.apply();

                        }
                    }).start();
                    Toast.makeText(MainActivity.this,"登录成功！", Toast.LENGTH_SHORT).show();
                    //Intent intent=new Intent(MainActivity.this,AfterLginActivity.class);
                    //startActivity(intent);
                    button.startAnim();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //转圈圈
                            if(stopTurnAround){
                                stopTurnAround=false;
                            }else{
                                //gotoNew();
                                button.regainBackground();
                                Toast.makeText(MainActivity.this,"网络不给力哦~",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },20000);
                    break;

                    //密码错误
                case -2:
                    Toast.makeText(MainActivity.this,"手机号或密码不正确！",Toast.LENGTH_SHORT).show();
                    break;

                    //请求id失败
                case -3:
                    Toast.makeText(MainActivity.this,"请求ID失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //请求salt失败
                case -4:
                    Toast.makeText(MainActivity.this,"请求盐值失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //请求clientpk失败！
                case -5:
                    Toast.makeText(MainActivity.this,"请求客户端公钥失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置背景亮度
        /*
        Drawable drawable=getResources().getDrawable(R.drawable.hualian);
        drawable.setAlpha(200);

         */

        initThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    bitmap_photo=getBitmapFromLocal("headPicture.jpg");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        initThread.start();

        //注册后自动填入手机号、密码
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        final EditText editText_studyNum=(EditText)findViewById(R.id.account);
        final EditText editText_password=(EditText)findViewById(R.id.password);

        if(preferences.getBoolean("isRegister",false)){
            String number=preferences.getString("number","");
            String password=preferences.getString("password","");
            editText_studyNum.setText(number);
            editText_password.setText(password);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isRegister",false);
            editor.apply();
        }


        //密码隐藏与显示
        final CheckBox checkBox_hide=(CheckBox)findViewById(R.id.hide_pwd);
        checkBox_hide.setChecked(true);
        editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        checkBox_hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        //登录
        final CheckBox checkBox_rememerPwd=(CheckBox)findViewById(R.id.remember_pwd);
        //editText_studyNum.setText(preferences.getString("studyNum"," "));
        if(preferences.getBoolean("rememberPassword",false)){
            editText_studyNum.setText(preferences.getString("number"," "));
            editText_password.setText(preferences.getString("password"," "));
            checkBox_rememerPwd.setChecked(true);
        }

        handler=new Handler();
        rlContent=(RelativeLayout)findViewById(R.id.parent);
        rlContent.getBackground().setAlpha(150);
        button=(NbButton)findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                studyNum=editText_studyNum.getText().toString();
                password=editText_password.getText().toString();
                //登录成功
                judgeLogin(studyNum,password);
            }
        });

        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.head_picture);
        if(initThread.isAlive()){
            try{
                initThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(bitmap_photo!=null){
            circleImageView.setImageBitmap(bitmap_photo);
        }
    }

    //登录动画

    private void gotoNew() {
        button.gotoNew();

        final Intent intent=new Intent(MainActivity.this,AfterLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setClass(MainActivity.this,AfterLoginActivity.class);

        int xc=(button.getLeft()+button.getRight())/2;
        int yc=(button.getTop()+button.getBottom())/2;
        animator= ViewAnimationUtils.createCircularReveal(rlContent,xc,yc,0,1900);
        //转完圈后的持续时间
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);

                    }
                },400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.head_picture);
        circleImageView.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_account=(LinearLayout)findViewById(R.id.mid1);
        LinearLayout linearLayout_password=(LinearLayout)findViewById(R.id.mid2);
        linearLayout_account.setVisibility(View.INVISIBLE);
        linearLayout_password.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_checkbox=(LinearLayout)findViewById(R.id.checkbox);
        linearLayout_checkbox.setVisibility(View.INVISIBLE);
        RelativeLayout relativeLayout_login=(RelativeLayout)findViewById(R.id.buttom);
        relativeLayout_login.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_bottom1=(LinearLayout)findViewById(R.id.bottom1);
        linearLayout_bottom1.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_bottom2=(LinearLayout)findViewById(R.id.bottom2);
        linearLayout_bottom2.setVisibility(View.INVISIBLE);
        rlContent.getBackground().setAlpha(255);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(clickLogin){
            animator.cancel();
            rlContent.getBackground().setAlpha(0);
            button.regainBackground();
        }
    }

    public void onClickRegister(View view){
        clickLogin=false;
        Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 向本地SD卡写网络图片
     *
     * @param bitmap
     */
    public static void saveBitmapToLocal(String fileName, Bitmap bitmap) {
        try {
            // 创建文件流，指向该路径，文件名叫做fileName
            File file = new File(FILE_PATH, fileName);
            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }
            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地SD卡获取缓存的bitmap
     */
    public static Bitmap getBitmapFromLocal(String fileName) {
        try {
            File file = new File(FILE_PATH, fileName);
            if (file.exists()) {
                return BitmapFactory.decodeStream(new FileInputStream(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //打开隐私条款
    public void onClickPrivacy(View view){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://ti.qq.com/agreement/privateProtocal.html?&version=8.4.8.4810&appid=537065346&QUA=V1_AND_SQ_8.4.8_1492_YYB_D");
        intent.setData(content_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
        startActivity(intent);
    }

    private String httpGet(String requestURL){
        String result=null;
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        try{
            URL url=new URL(requestURL);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
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

    //生成哈希
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void judgeLogin(final String phone, final String password){
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String id = preferences.getString("id", "empty");
        final String salt=preferences.getString("salt","empty");
        String clientpk=preferences.getString("clientpk","empty");
        //如果id为空或salt为空，向服务器请求id
        final String[] id1 = {null};
        final String[] salt1 = {null};
        final String[] clientpk1 = {null};
        clientpk1[0]=clientpk;
        salt1[0]=salt;
        id1[0]=id;

        //请求id和salt的线程
        Thread idThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        threadHandler.sendEmptyMessage(-3);
                    }
                };
                timer.schedule(task,5000);
                try{
                    Gson gson=new Gson();
                    IDRequest idRequest=new IDRequest(phone);
                    String json=gson.toJson(idRequest);
                    String response =httpPost("http://"+IP+":"+PORT+"/sys/user/id",json);
                    Type type=new TypeToken<IDResponse>(){}.getType();
                    IDResponse idResponse=gson.fromJson(response,type);
                    timer.cancel();
                    id1[0] =idResponse.getId();
                    if(id1[0]==null){
                        threadHandler.sendEmptyMessage(-3);
                    }
                    salt1[0] =idResponse.getSalt();
                    if(salt1[0]==null){
                        threadHandler.sendEmptyMessage(-4);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    threadHandler.sendEmptyMessage(-3);
                }

            }
        });

        //请求clientpk的线程
        Thread pkThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        threadHandler.sendEmptyMessage(-5);
                    }
                };
                timer.schedule(task,5000);
                try{
                    Gson gson=new Gson();
                    String response =httpGet("http://"+IP+":"+PORT+"/sys/user/clientpk?id="+id1[0]);
                    Type type=new TypeToken<RSAKeyResult>(){}.getType();
                    RSAKeyResult rsaKeyResult=gson.fromJson(response,type);
                    clientpk1[0] =RSACipher.decrypt(Config.SERVER_PRIVATE_KEY,rsaKeyResult.getClientpk());
                    if(clientpk1[0] ==null){
                        threadHandler.sendEmptyMessage(-5);
                    }else{
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("clientpk", clientpk1[0]);
                        editor.apply();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        assert id != null;
        assert salt != null;
        //异地登录时请求salt和id
        if(id.equals("empty")||salt.equals("empty")){
            idThread.start();
            try{
                idThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }

            /*
            //异地登录请求clientpk
            if(clientpk.equals("empty")){
                pkThread.start();
                //clientpk=clientpk1[0];
            }

             */

            final int[] state = {LoginResult.FAIL};
            //登录线程
            Thread httpThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    Timer timer=new Timer();
                    TimerTask task=new TimerTask() {
                        @Override
                        public void run() {
                            threadHandler.sendEmptyMessage(-1);
                        }
                    };
                    timer.schedule(task,5000);
                    try{
                        Gson gson=new Gson();
                        LoginRequest loginRequest=new LoginRequest(id1[0],applySha256(phone+password+salt1[0]));
                        String json=gson.toJson(loginRequest);
                        String response=httpPost("http://"+IP+":"+PORT+"/sys/user/login",json);
                        Type type=new TypeToken<LoginResult>(){}.getType();
                        LoginResult loginResult=gson.fromJson(response,type);
                        timer.cancel();
                        state[0] =loginResult.getState();
                        if(state[0]==LoginResult.SUCCESS){
                            threadHandler.sendEmptyMessage(2);
                        }else{
                            threadHandler.sendEmptyMessage(-2);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            httpThread.start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("id",id1[0]);
                    editor.putString("salt",salt1[0]);
                    editor.apply();
                }
            }).start();

            /*
            try{
                httpThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }

             */

        }else{
            final int[] state = {LoginResult.FAIL};
            Thread httpThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    Timer timer=new Timer();
                    TimerTask task=new TimerTask() {
                        @Override
                        public void run() {
                            threadHandler.sendEmptyMessage(-1);
                        }
                    };
                    timer.schedule(task,5000);
                    try{
                        Gson gson=new Gson();
                        LoginRequest loginRequest=new LoginRequest(id,applySha256(phone+password+salt));
                        String json=gson.toJson(loginRequest);
                        String response=httpPost("http://"+IP+":"+PORT+"/sys/user/login",json);
                        Type type=new TypeToken<LoginResult>(){}.getType();
                        LoginResult loginResult=gson.fromJson(response,type);
                        timer.cancel();
                        state[0] =loginResult.getState();
                        if(state[0]==LoginResult.SUCCESS){
                            threadHandler.sendEmptyMessage(2);
                        }else{
                            threadHandler.sendEmptyMessage(-2);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            httpThread.start();

            /*
            try{
                httpThread.join();
            }catch (Exception e){
                e.printStackTrace();
                //Toast.makeText(MainActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                //return false;
            }

             */

        }

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

    /*
    public static void initBitmaps(List<BillJson> billJsonArrayList){
        //提前加载bitmap
        int i;
        AfterLoginActivity.bitmaps=new Bitmap[20];
        for(i=0;i<20;i++){
            AfterLoginActivity.bitmaps[i]=null;
        }
        int len=billJsonArrayList.size();
        if(len==1){
            GetBitmap getBitmap=new GetBitmap(billJsonArrayList,0,1);
            getBitmap.start();
            try{
                getBitmap.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(len==2){
            GetBitmap getBitmap1=new GetBitmap(billJsonArrayList,0,1);
            GetBitmap getBitmap2=new GetBitmap(billJsonArrayList,1,2);
            getBitmap1.start();
            getBitmap2.start();
            try{
                getBitmap1.join();
                getBitmap2.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(len<=6){
            GetBitmap getBitmap1=new GetBitmap(billJsonArrayList,0,len/3);
            GetBitmap getBitmap2=new GetBitmap(billJsonArrayList,len/3,2*len/3);
            GetBitmap getBitmap3=new GetBitmap(billJsonArrayList,2*len/3,len);
            getBitmap1.start();
            getBitmap2.start();
            getBitmap3.start();
            try{
                getBitmap1.join();
                getBitmap2.join();
                getBitmap3.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(len<=8){
            GetBitmap getBitmap1=new GetBitmap(billJsonArrayList,0,len/4);
            GetBitmap getBitmap2=new GetBitmap(billJsonArrayList,len/4,len/2);
            GetBitmap getBitmap3=new GetBitmap(billJsonArrayList,len/2,3*len/4);
            GetBitmap getBitmap4=new GetBitmap(billJsonArrayList,3*len/4,len);
            getBitmap1.start();
            getBitmap2.start();
            getBitmap3.start();
            getBitmap4.start();
            try{
                getBitmap1.join();
                getBitmap2.join();
                getBitmap3.join();
                getBitmap4.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(len<=10){
            GetBitmap getBitmap1=new GetBitmap(billJsonArrayList,0,len/5);
            GetBitmap getBitmap2=new GetBitmap(billJsonArrayList,len/5,2*len/5);
            GetBitmap getBitmap3=new GetBitmap(billJsonArrayList,2*len/5,3*len/5);
            GetBitmap getBitmap4=new GetBitmap(billJsonArrayList,3*len/5,4*len/5);
            GetBitmap getBitmap5=new GetBitmap(billJsonArrayList,4*len/5,len);
            getBitmap1.start();
            getBitmap2.start();
            getBitmap3.start();
            getBitmap4.start();
            getBitmap5.start();
            try{
                getBitmap1.join();
                getBitmap2.join();
                getBitmap3.join();
                getBitmap4.join();
                getBitmap5.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            GetBitmap getBitmap1=new GetBitmap(billJsonArrayList,0,len/6);
            GetBitmap getBitmap2=new GetBitmap(billJsonArrayList,len/6,len/3);
            GetBitmap getBitmap3=new GetBitmap(billJsonArrayList,len/3,len/2);
            GetBitmap getBitmap4=new GetBitmap(billJsonArrayList,len/2,2*len/3);
            GetBitmap getBitmap5=new GetBitmap(billJsonArrayList,2*len/3,5*len/6);
            GetBitmap getBitmap6=new GetBitmap(billJsonArrayList,5*len/6,len);
            getBitmap1.start();
            getBitmap2.start();
            getBitmap3.start();
            getBitmap4.start();
            getBitmap5.start();
            getBitmap6.start();
            try{
                getBitmap1.join();
                getBitmap2.join();
                getBitmap3.join();
                getBitmap4.join();
                getBitmap5.join();
                getBitmap6.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

     */

    //PNG
    private Bitmap compressQualit3(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,30,bos);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 5;
        byte[] bytes = bos.toByteArray();
        Bitmap bitmap1=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        return ThumbnailUtils.extractThumbnail(bitmap1, 100, 150);
    }

    //JPEG
    private Bitmap compressQualit2(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bos);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 5;
        byte[] bytes = bos.toByteArray();
        Bitmap bitmap1=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        return ThumbnailUtils.extractThumbnail(bitmap1, 100, 150);
    }

    private static String byteToString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        String strContent = "";
        strContent = new String(bytes, StandardCharsets.UTF_8);
        return strContent;
    }

}