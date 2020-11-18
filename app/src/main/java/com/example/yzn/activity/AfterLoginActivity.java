package com.example.yzn.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yzn.R;
import com.example.yzn.activity.ui.CircleImageView;
import com.example.yzn.activity.ui.DragFloatActionButton;
import com.example.yzn.activity.ui.ImageTextButton;
import com.example.yzn.activity.ui.JumpingSpan;
import com.example.yzn.activity.ui.RotateDownTransformer;
import com.example.yzn.activity.ui.SlideRecyclerView;
import com.example.yzn.activity.util.ApkVersion;
import com.example.yzn.activity.util.BgmService;
import com.example.yzn.activity.util.Bill;
import com.example.yzn.activity.util.BillAdapter;
import com.example.yzn.activity.util.BillJson;
import com.example.yzn.activity.util.DownloadService;
import com.example.yzn.activity.util.FriendAdapter;
import com.example.yzn.activity.util.FriendApply;
import com.example.yzn.activity.util.FriendJson;
import com.example.yzn.activity.util.HeadJson;
import com.example.yzn.activity.util.IOUtils;
import com.example.yzn.activity.util.Info;
import com.example.yzn.activity.util.MyDatabaseHelper;
import com.example.yzn.activity.util.NetworkChangeReciever;
import com.example.yzn.activity.util.SlideMessageAdapter;
import com.example.yzn.activity.util.Space;
import com.example.yzn.activity.util.SpaceAdapter;
import com.example.yzn.activity.util.SpacesItemDecoration;
import com.example.yzn.activity.util.WeatherService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class AfterLoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<View>pageView;
    public View view1;
    public View view2;
    public View view3;
    public View view4;

    public ImageTextButton imageTextButton_left;
    public ImageTextButton imageTextButton_middle;
    public ImageTextButton imageTextButton_middle2;
    public ImageTextButton imageTextButton_right;

    private RecyclerView recyclerView_bill;
    private BillAdapter billAdapter;
    private RecyclerView.LayoutManager layoutManager_bill;
    public static List<BillJson> billJsonArrayList;
    //public static Bitmap[] bitmaps;

    private RecyclerView recyclerView_friend;
    private FriendAdapter friendAdapter;
    private RecyclerView.LayoutManager layoutManager_friend;
    public static List<FriendJson> friendList;
    public static List<Bitmap> headList;

    private RecyclerView recyclerView_space;
    private SpaceAdapter spaceAdapter;
    private RecyclerView.LayoutManager layoutManager_space;
    private ArrayList<Space> spaceArrayList;

    private SlideRecyclerView slideRecyclerView_phonebook;
    private SlideMessageAdapter slideMessageAdapter;

    private MyDatabaseHelper myDatabaseHelper;

    private NetworkChangeReciever networkChangeReciever=null;

    public final static String heUsername="HE2010022220001957";
    public final static String heKey="01238d99c2f047a287e157952f8a96d8";

    private LocationClient mLocationClient;
    public String city;
    public String disttrict;
    private boolean firstEnter=false;
    final private String TAG="AfterLoginActivity";

    private MediaPlayer mediaPlayer=new MediaPlayer();
    public Intent intent_bgm=null;

    private Thread initThread;
    private Thread sqliteThread;

    public final static int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO = 2;
    public final static int REQUEST_LOCATION=3;
    private Uri imageUri;
    private Bitmap bitmap_photo=null;

    public static final int VIDEO_PAUSE=1;
    public static final int VIDEO_PLAY=2;
    private int videoState=VIDEO_PAUSE;

    public NotificationManager notificationManager;

    public int year;
    public int month;
    public int day;

    public final static int VIEW1=1;
    public final static int VIEW2=2;
    public final static int VIEW3=3;
    public final static int VIEW4=4;
    private int viewState=VIEW1;

    //缓存路径
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";

    //上传bill时的类型
    private int type;
    private int currency;
    private String base64Str=null;

    public static final int MaxFLingVelocity=5000;

    public static final String version="1.0";
    private DownloadService.DownloadBinder mDownloadBinder;
    public final static String APK_URL="http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/update/download";

    private static final int REQUEST_CODE_SCAN = 111;

    public static boolean FRIEND_GET_COMPLETE=false;
    public static boolean FRIEND_HEAD_GET_COMPLETE=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new AfterLoginActivity.MyLocationListener());
        setContentView(R.layout.activity_after_login);

        final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
        //请求头像
        initThread=new Thread(new Runnable() {
            @Override
            public void run() {
                //initPhonebook();
                //initContacts();
                //initSpace();
                bitmap_photo=getBitmapFromLocal("headPicture.jpg");
                //缓存没有时向服务器请求
                if(bitmap_photo==null){
                    Gson gson=new Gson();
                    Type type=new TypeToken<HeadJson>(){}.getType();
                    String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/head?id="+preferences.getString("id",""));
                    HeadJson headJson=gson.fromJson(response,type);
                    if(headJson.getBase64Str()!=null){
                        bitmap_photo=base64ToBitmap(headJson.getBase64Str());
                        handler.sendEmptyMessage(2);
                        if(bitmap_photo!=null){
                            saveBitmapToLocal("headPicture.jpg",bitmap_photo);
                        }
                    }

                }
            }
        });

        HeConfig.init(heUsername,heKey);
        HeConfig.switchToFreeServerNode();
        firstEnter=true;

        Intent intent=getIntent();
        viewState=intent.getIntExtra("viewState",1);

        //申请权限
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECEIVE_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_CONTACTS);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(AfterLoginActivity.this,permissions,REQUEST_LOCATION);
            //ActivityCompat.requestPermissions(AfterLoginActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS}, 1);
        }else{
            //请求位置和天气
            requestLocation();
            //获得联系人
            initThread.start();
        }

        //httpTest();

        //开启天气后台服务
        Intent intent_weather=new Intent(this,WeatherService.class);
        startService(intent_weather);

        //动态注册广播,监测网络变化
        networkChangeReciever=new NetworkChangeReciever();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReciever,intentFilter);

        //判断billJsonList是否为空,为空时向SQLite读取
        sqliteThread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(billJsonArrayList==null||billJsonArrayList.size()==0){
                    billJsonArrayList=new ArrayList<>();
                    try{
                        myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
                        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                        Cursor cursor=db.query("bill",null,null,null,null,null,null);
                        if(cursor.moveToFirst()){
                            do{
                                String billID=cursor.getString(cursor.getColumnIndex("billID"));
                                String issureID=cursor.getString(cursor.getColumnIndex("issureID"));
                                String productName=cursor.getString(cursor.getColumnIndex("productName"));
                                String price=cursor.getString(cursor.getColumnIndex("price"));
                                int type=cursor.getInt(cursor.getColumnIndex("type"));
                                String middleName=cursor.getString(cursor.getColumnIndex("middleName"));
                                int currency=cursor.getInt(cursor.getColumnIndex("currency"));
                                String detail=cursor.getString(cursor.getColumnIndex("detail"));
                                BillJson billJson=new BillJson(billID,issureID,productName,price,currency,type,middleName,null,detail,false);
                                billJsonArrayList.add(billJson);
                            }while(cursor.moveToNext());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        sqliteThread.start();



        //启动bgm
        startService(new Intent(AfterLoginActivity.this,BgmService.class));


        viewPager=(ViewPager)findViewById(R.id.viewPage);
        viewPager.setOffscreenPageLimit(4);
        final LayoutInflater layoutInflater=getLayoutInflater();

        view1=layoutInflater.inflate(R.layout.tab1,null);
        initView1();

        view2=layoutInflater.inflate(R.layout.tab2,null);
        initView2();

        view3=layoutInflater.inflate(R.layout.tab3,null);
        initView3();

        view4=layoutInflater.inflate(R.layout.tab4,null);
        initView4();

        pageView=new ArrayList<>();
        pageView.add(view3);
        pageView.add(view2);
        pageView.add(view1);
        pageView.add(view4);

        //refreshBills();

        final PagerAdapter mPagerAdapter = new PagerAdapter() {

            private boolean firstEnter = true;

            public void setFirstEnter(boolean firstEnter) {
                this.firstEnter = firstEnter;
            }

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageView.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }
            //使从ViewGroup中移出当前View


            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //super.destroyItem(container, position, object);
            }


            //可以在这里初始化view
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(pageView.get(arg1));
                return pageView.get(arg1);
            }

        };

        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(viewState-1);
        viewPager.setPageTransformer(true,new RotateDownTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageTextButton_left=(ImageTextButton)findViewById(R.id.left_button);
                imageTextButton_middle=(ImageTextButton)findViewById(R.id.middle_button);
                imageTextButton_middle2=(ImageTextButton)findViewById(R.id.middle2_button);
                imageTextButton_right=(ImageTextButton)findViewById(R.id.right_button);
                switch (position){
                    case 0:
                        //接单
                        imageTextButton_left.setImageResource(R.drawable.bill_blue);
                        imageTextButton_left.setTextColor(R.color.blue);
                        imageTextButton_middle.setImageResource(R.drawable.take_task);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.message2);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.personal);
                        imageTextButton_right.setTextColor(R.color.black);
                        viewState=VIEW1;

                        break;

                    //联系人&短信
                    case 1:
                        /*
                        final RelativeLayout relativeLayout_top=(RelativeLayout)view2.findViewById(R.id.layout_top);
                        final RelativeLayout relativeLayout_bill=(RelativeLayout)view2.findViewById(R.id.layout_bill);
                        final RelativeLayout relativeLayout_load=(RelativeLayout)view2.findViewById(R.id.upload_layout);
                        relativeLayout_top.setAlpha(1);
                        relativeLayout_bill.setAlpha(1);
                        relativeLayout_load.setVisibility(View.GONE);
                        allowTouch=true;

                         */

                        /*
                        recyclerView_bill=(RecyclerView)view2.findViewById(R.id.recycleView_bill);
                        //billAdapter=new BillAdapter(billArrayList,AfterLoginActivity.this);
                        layoutManager_bill=new LinearLayoutManager(AfterLoginActivity.this);
                        recyclerView_bill.addItemDecoration(new SpacesItemDecoration(20));
                        recyclerView_bill.setLayoutManager(layoutManager_bill);
                        recyclerView_bill.setAdapter(billAdapter);
                         */

                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.bill);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.take_task_blue);
                        imageTextButton_middle.setTextColor(R.color.blue);
                        imageTextButton_middle2.setImageResource(R.drawable.message2);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.personal);
                        imageTextButton_right.setTextColor(R.color.black);
                        viewState=VIEW2;


                        break;

                    //拍照
                    case 2:
                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.bill);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.take_task);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.message2_blue);
                        imageTextButton_middle2.setTextColor(R.color.blue);
                        imageTextButton_right.setImageResource(R.drawable.personal);
                        imageTextButton_right.setTextColor(R.color.black);
                        viewState=VIEW3;
                        break;

                    //历史
                    case 3:

                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.bill);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.take_task);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.message2);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.personal_blue);
                        imageTextButton_right.setTextColor(R.color.blue);
                        viewState=VIEW4;

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initView();


    }

    public static class VersionDialog extends Dialog {
        public VersionDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = View.inflate(getContext(),R.layout.app_info,null);
            ImageButton imageButton_cancel=(ImageButton)view.findViewById(R.id.button_cancel);
            imageButton_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });
            TextView textView_version=(TextView)view.findViewById(R.id.textView_version);
            String versionName=null;
            PackageManager packageManager=view.getContext().getPackageManager();
            try{
                PackageInfo packageInfo=packageManager.getPackageInfo(view.getContext().getPackageName(),0);
                versionName=packageInfo.versionName;
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
            if(versionName!=null){
                textView_version.setText("版本号: "+versionName);
            }
            setContentView(view);
        }
    }

    public class UpdateDialog extends Dialog {
        public String version;

        public UpdateDialog(Context context,String version) {

            super(context);
            this.version=version;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = View.inflate(getContext(),R.layout.app_version,null);
            ImageButton imageButton_cancel=(ImageButton)view.findViewById(R.id.button_cancel);
            imageButton_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });

            //版本号
            TextView textView_version=(TextView)view.findViewById(R.id.textView_app_version);
            textView_version.setText("新版本: "+version+"  ");

            //app更新
            Button button_update=(Button)view.findViewById(R.id.button_update);
            button_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();

                    try {
                        downloadApk("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/update/download",AfterLoginActivity.this);
                        Toast.makeText(AfterLoginActivity.this,"YZN: "+version+" 开始下载",Toast.LENGTH_SHORT).show();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            setContentView(view);
        }
    }

    public class ApplyDialog extends Dialog{
        public String fromid;
        public Info info;
        public String nickname;
        public View view;

        public ApplyDialog(Context context, String frmoid){
            super(context);
            this.fromid=frmoid;
            //this.nickname=nickname;
        }

        @SuppressLint("HandlerLeak")
        Handler applyHandler=new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    //完成头像申请
                    case 1:
                        if(bitmap_photo!=null){
                            CircleImageView circleImageView=(CircleImageView)view.findViewById(R.id.circleView);
                            circleImageView.setImageBitmap(bitmap_photo);
                        }
                        setContentView(view);
                        break;

                        //完成个人信息申请
                    case 2:
                        TextView textView_nickname=(TextView)view.findViewById(R.id.textView_nickname);
                        ImageView imageView_boy=(ImageView)view.findViewById(R.id.imageView_male);
                        ImageView imageView_girl=(ImageView)view.findViewById(R.id.imageView_female);
                        TextView textView_province=(TextView)view.findViewById(R.id.textView_province);
                        TextView textView_city=(TextView)view.findViewById(R.id.textView_city);
                        TextView textView_birthday=(TextView)view.findViewById(R.id.textView_birthday);
                        TextView textView_company=(TextView)view.findViewById(R.id.textView_company_content);
                        TextView textView_job=(TextView)view.findViewById(R.id.textView_job_content);
                        textView_nickname.setText(info.getNickname());
                        if(info.getSex()==0){
                            imageView_boy.setVisibility(View.VISIBLE);
                            imageView_girl.setVisibility(View.INVISIBLE);
                        }else{
                            imageView_boy.setVisibility(View.INVISIBLE);
                            imageView_girl.setVisibility(View.VISIBLE);
                        }
                        textView_province.setText(info.getProvince());
                        textView_city.setText(info.getCity());
                        textView_birthday.setText(info.getBirthday());
                        textView_company.setText(info.getCompany());
                        textView_job.setText(info.getJob());

                        //nickname加载出来后才允许同意
                        nickname=info.getNickname();
                        Button button_confirm=(Button)view.findViewById(R.id.button_confirm);
                        button_confirm.setEnabled(true);
                        setContentView(view);
                        break;

                }
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            view=View.inflate(getContext(),R.layout.apply,null);

            //获得申请人头像
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/head?id="+fromid);
                    Gson gson=new Gson();
                    Type type=new TypeToken<HeadJson>(){}.getType();
                    HeadJson headJson=gson.fromJson(response,type);
                    if(headJson.getBase64Str()!=null){
                        bitmap_photo=base64ToBitmap(headJson.getBase64Str());
                    }else{
                        bitmap_photo=null;
                    }
                    applyHandler.sendEmptyMessage(1);
                }
            }).start();

            //获得个人信息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/info/get?id="+fromid);
                    Gson gson=new Gson();
                    Type type=new TypeToken<com.example.yzn.activity.util.Info>(){}.getType();
                    info=gson.fromJson(response,type);
                    applyHandler.sendEmptyMessage(2);
                }
            }).start();

            //同意好友申请
            final Button button_confirm=(Button)view.findViewById(R.id.button_confirm);
            button_confirm.setEnabled(false);
            button_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    executeAnimation(view);
                    final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                    //向服务器发送confirm
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson=new Gson();
                            FriendApply friendApply=new FriendApply(fromid,preferences.getString("id"," "));
                            String json=gson.toJson(friendApply);
                            String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/confirm/post",json);
                            if(response.equals("friend confirm success")){
                                handler.sendEmptyMessage(4);
                            }else{
                                handler.sendEmptyMessage(-4);
                            }
                        }
                    }).start();

                    //向服务器数据库插入新的好友
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson=new Gson();
                            FriendJson friendJson=new FriendJson(preferences.getString("id",""),fromid,nickname,null,null);
                            String json=gson.toJson(friendJson);
                            String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/post",json);
                            if(response.equals("friend post success")){
                                handler.sendEmptyMessage(5);
                            }else{
                                handler.sendEmptyMessage(-5);
                            }
                        }
                    }).start();

                    //向SQLITE插入数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"Friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
                            SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                            List<String> friendIDList=new ArrayList<>();
                            Cursor cursor=db.query("friend",null,null,null,null,null,null);
                            if(cursor.moveToFirst()){
                                do{
                                    String id=cursor.getString(cursor.getColumnIndex("id"));
                                    friendIDList.add(id);
                                }while(cursor.moveToNext());
                            }
                            if(friendIDList.size()>0){
                                for(int i=0;i<friendIDList.size();i++){
                                    if(friendIDList.get(i).equals(fromid)){
                                        finish();
                                    }
                                }
                            }else{
                                ContentValues contentValues=new ContentValues();
                                contentValues.put("id",fromid);
                                contentValues.put("nickname",nickname);
                                contentValues.put("remark","");
                                contentValues.put("time","");
                                db.insert("friend",null,contentValues);
                            }

                        }
                    }).start();
                    //Toast.makeText(AfterLoginActivity.this,"同意好友申请成功！",Toast.LENGTH_SHORT).show();
                    button_confirm.setText("已同意");
                    button_confirm.setEnabled(false);
                }
            });

            //取消
            ImageButton imageButton_cancel=(ImageButton)view.findViewById(R.id.button_cancel);
            imageButton_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });

            //setContentView(view);
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadBinder = null;
        }
    };

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //String location=bdLocation.getAddrStr();
            String province=bdLocation.getProvince();
            city=bdLocation.getCity();
            disttrict=bdLocation.getDistrict();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView_location = (TextView) view3.findViewById(R.id.textView_location);
                    textView_location.setText(city+disttrict);
                }
            });
            final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("province",province);
            editor.putString("city",city);
            editor.putString("district",disttrict);
            editor.apply();
            if(true){
                //请求天气
                HeWeather.getWeatherNow(getApplicationContext(), city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        Log.i(TAG, "Weather Now onError: ", throwable);
                    }

                    @Override
                    public void onSuccess(Now now) {
                        Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(now));
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if ( Code.OK.getCode().equalsIgnoreCase(now.getStatus()) ){
                            //此时返回数据
                            NowBase nowBase = now.getNow();
                            final String weather=nowBase.getCond_txt();
                            final String temperature=nowBase.getTmp();
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("weather",weather);
                            editor.putString("temperature",temperature);
                            editor.apply();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView_weather = (TextView) view3.findViewById(R.id.textView_weather);
                                    textView_weather.setText(weather+"  "+temperature+"℃");
                                    ImageView imageView_weather=(ImageView)view3.findViewById(R.id.imageView_weather);
                                    switch (weather){
                                        case "晴":
                                            imageView_weather.setImageResource(R.drawable.sunny);
                                            break;
                                        case "多云":
                                            imageView_weather.setImageResource(R.drawable.cloudy);
                                            break;
                                        case "阴":
                                            imageView_weather.setImageResource(R.drawable.shady);
                                            break;
                                        case "雨":
                                            imageView_weather.setImageResource(R.drawable.rainy);
                                            break;

                                    }
                                }
                            });



                        } else {
                            //在此查看返回数据失败的原因
                            String status = now.getStatus();
                            Code code = Code.toEnum(status);
                            Log.i(TAG, "failed code: " + code);
                        }
                    }
                });
                firstEnter=false;
            }
        }
    }

    public void initView(){
        //版本
        ImageButton imageButton_version=(ImageButton)findViewById(R.id.title_version);
        imageButton_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VersionDialog versionDialog= new VersionDialog(AfterLoginActivity.this);
                versionDialog.setCanceledOnTouchOutside(true);
                versionDialog.show();
            }
        });

        //检查是否有好友申请
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/apply/get?toid="+preferences.getString("id",""));
                Gson gson=new Gson();
                Type type=new TypeToken<List<FriendApply>>(){}.getType();
                List<FriendApply> friendApplies=gson.fromJson(response,type);
                if(friendApplies.size()>0){
                    FriendApply friendApply=friendApplies.get(0);
                    Bundle bundle=new Bundle();
                    bundle.putString("fromid",friendApply.getFromid());
                    Message message=Message.obtain();
                    message.setData(bundle);
                    applyHandler.sendMessage(message);

                }
            }
        }).start();

         */


        //扫码
        ImageButton imageButton_scan=(ImageButton)findViewById(R.id.title_scan);
        imageButton_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请权限
                List<String> permissionList=new ArrayList<>();
                if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    permissionList.add(Manifest.permission.CAMERA);
                }
                if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if(!permissionList.isEmpty()){
                    String[] permissions=permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(AfterLoginActivity.this,permissions,REQUEST_CODE_SCAN);
                    //ActivityCompat.requestPermissions(AfterLoginActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS}, 1);
                }else{
                    Intent intent = new Intent(AfterLoginActivity.this, CaptureActivity.class);
                    /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                     * 也可以不传这个参数
                     * 不传的话  默认都为默认不震动  其他都为true
                     * */

                    ZxingConfig config = new ZxingConfig();
                    //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
                    //config.setPlayBeep(true);//是否播放提示音
                    config.setShake(true);//是否震动
                    //config.setShowAlbum(true);//是否显示相册
                    //config.setShowFlashLight(true);//是否显示闪光灯
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }

            }
        });

        //设置导航栏
        imageTextButton_left = (ImageTextButton) findViewById(R.id.left_button);
        imageTextButton_left.setText("发单");
        imageTextButton_left.setImageResource(R.drawable.bill_blue);
        imageTextButton_left.setTextSize(10);
        imageTextButton_left.setTextBold();
        imageTextButton_left.setTextColor(R.color.blue);
        imageTextButton_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(0);
            }
        });

        imageTextButton_middle = (ImageTextButton) findViewById(R.id.middle_button);
        imageTextButton_middle.setText("接单");
        imageTextButton_middle.setImageResource(R.drawable.take_task);
        imageTextButton_middle.setTextSize(10);
        imageTextButton_middle.setTextColor(R.color.black);
        imageTextButton_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(1);
            }
        });

        imageTextButton_middle2=(ImageTextButton)findViewById(R.id.middle2_button);
        imageTextButton_middle2.setText("消息");
        imageTextButton_middle2.setImageResource(R.drawable.message2);
        imageTextButton_middle2.setTextSize(10);
        imageTextButton_middle2.setTextColor(R.color.black);
        imageTextButton_middle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(2);
            }
        });

        imageTextButton_right = (ImageTextButton) findViewById(R.id.right_button);
        imageTextButton_right.setText("我的");
        imageTextButton_right.setImageResource(R.drawable.personal);
        imageTextButton_right.setTextSize(10);
        imageTextButton_right.setTextColor(R.color.black);
        imageTextButton_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(3);
            }
        });

        final DragFloatActionButton dragFloatActionButton=(DragFloatActionButton)findViewById(R.id.music_button);
        dragFloatActionButton.setImageResource(R.drawable.play);
        dragFloatActionButton.setSize(FloatingActionButton.SIZE_AUTO);
        dragFloatActionButton.setColorFilter(R.color.colorAccent);
        dragFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preferences.edit();
                int mediaState=preferences.getInt("mediaState",BgmService.MUSIC_STOP);
                int musicID=preferences.getInt("musicID",R.raw.heavenly_blessing);
                switch(mediaState){
                    case BgmService.MUSIC_PLAY:
                        if(intent_bgm!=null){
                            stopService(intent_bgm);
                        }
                        dragFloatActionButton.setImageResource(R.drawable.play);
                        sendBroadcastData(BgmService.MUSIC_PAUSE,musicID);
                        editor.putInt("mediaState",BgmService.MUSIC_PAUSE);
                        break;
                    case BgmService.MUSIC_PAUSE:
                    case BgmService.MUSIC_STOP:
                        if(intent_bgm!=null){
                            stopService(intent_bgm);
                        }
                        dragFloatActionButton.setImageResource(R.drawable.pause);
                        sendBroadcastData(BgmService.MUSIC_PLAY,musicID);
                        editor.putInt("mediaState",BgmService.MUSIC_PLAY);

                        //状态栏通知
                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = null;
                        Intent intent1=new Intent(AfterLoginActivity.this, MessageActivity.class);
                        PendingIntent pendingIntent=PendingIntent.getActivity(AfterLoginActivity.this,0,intent1,0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel1 = new NotificationChannel("bgm","BGM消息",NotificationManager.IMPORTANCE_DEFAULT);

                            //通过NotificationManager.createNotificationChannel(channel)方法建立通知渠道
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            //Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
                            Log.i(TAG, channel1.toString());
                            notificationManager.createNotificationChannel(channel1);
                            notification = new NotificationCompat.Builder(AfterLoginActivity.this,"bgm")
                                    .setContentTitle("Bgm").setContentText("wish you heavenly blessings")     //标题 内容
                                    .setWhen(System.currentTimeMillis())         //发送时间
                                    .setContentIntent(pendingIntent)       //点击打开活动
                                    .setSmallIcon(R.drawable.hongzaomantou)   //小图标
                                    .setAutoCancel(true)               //自动消失
                                    .setLights(Color.BLUE,1000,1000)   //LED，颜色，亮起时间，暗去时间
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.hongzaomantou)).build();
                            notificationManager.notify(1110, notification);
                        } else {
                            Toast.makeText(AfterLoginActivity.this,"SDK版本过低，无法显示通知",Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                editor.apply();
            }
        });

        if(initThread.isAlive()){
            try{
                initThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.title_left);
        if(bitmap_photo!=null){
            circleImageView.setImageBitmap(bitmap_photo);
        }

        //检查版本更新
        checkVersion(AfterLoginActivity.this);
        //检查好友申请
        checkFriendApply();


        /*
        dragFloatActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int musicID=preferences.getInt("musicID",R.raw.heavenly_blessing);
                SharedPreferences.Editor editor=preferences.edit();
                switch(musicID){
                    case R.raw.heavenly_blessing:
                        dragFloatActionButton.setColorFilter(R.color.lightBlue);
                        musicID=R.raw.lunak;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                    case R.raw.lunak:
                        dragFloatActionButton.setColorFilter(R.color.lightGreen);
                        musicID=R.raw.pagoda;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                    case R.raw.pagoda:
                        dragFloatActionButton.setColorFilter(R.color.colorAccent);
                        musicID=R.raw.heavenly_blessing;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                }
                return true;
            }
        });

         */
    }

    public void initView1(){
        final ImageTextButton imageTextButton_condition=(ImageTextButton)view1.findViewById(R.id.button_condition);
        imageTextButton_condition.setImageResource(R.drawable.condition);
        imageTextButton_condition.setText("订单状态");
        imageTextButton_condition.setTextSize(13);

        final ImageTextButton imageTextButton_system=(ImageTextButton)view1.findViewById(R.id.button_system);
        imageTextButton_system.setImageResource(R.drawable.system);
        imageTextButton_system.setText("系统消息");
        imageTextButton_system.setTextSize(13);

        final ImageTextButton imageTextButton_activity=(ImageTextButton)view1.findViewById(R.id.button_activity);
        imageTextButton_activity.setImageResource(R.drawable.activity);
        imageTextButton_activity.setText("活动中心");
        imageTextButton_activity.setTextSize(13);

        /*
        //向SQLite数据库请求好友列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                friendList=new ArrayList<>();
                myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"Friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                Cursor cursor=db.query("Friend",null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    try{
                        do{
                            String id=cursor.getString(cursor.getColumnIndex("id"));
                            String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                            String remark=cursor.getString(cursor.getColumnIndex("remark"));
                            String time=cursor.getString(cursor.getColumnIndex("time"));
                            friendList.add(new Friend(id,nickname,remark,time));
                        }while (cursor.moveToNext());
                        handler.sendEmptyMessage(3);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
        }).start();

         */


        RecyclerView recyclerView_friend=(RecyclerView)view1.findViewById(R.id.recycleView_friend);
        friendAdapter=new FriendAdapter(friendList,headList);
        layoutManager_friend=new LinearLayoutManager(AfterLoginActivity.this);
        recyclerView_friend.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView_friend.setLayoutManager(layoutManager_friend);
        recyclerView_friend.setAdapter(friendAdapter);

        SwipeRefreshLayout swipeRefreshLayout_friend=(SwipeRefreshLayout)view1.findViewById(R.id.layout_refresh_friend);
        swipeRefreshLayout_friend.setColorSchemeColors(Color.BLUE,Color.GREEN);
        swipeRefreshLayout_friend.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                //加载好友列表
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String response=httpGet("http://"+ MainActivity.IP+":"+MainActivity.PORT+"/message/friend/get?id="+preferences.getString("id",""));
                            Gson gson=new Gson();
                            Type type=new TypeToken<List<FriendJson>>(){}.getType();
                            friendList=gson.fromJson(response,type);
                            handler.sendEmptyMessage(3);

                            //向SQLite更新全部数据
                            myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
                            SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                            db.delete("friend",null,null);
                            if(friendList.size()>0){
                                for(int i=0;i<friendList.size();i++) {
                                    FriendJson friendJson = friendList.get(i);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("id", friendJson.getFriendid());
                                    contentValues.put("nickname", friendJson.getNickname());
                                    contentValues.put("remark", friendJson.getRemark());
                                    contentValues.put("time", friendJson.getTime());
                                    db.insert("friend", null, contentValues);
                                    contentValues.clear();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            friendList=new ArrayList<>();
                            handler.sendEmptyMessage(-3);
                            myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"friend.db",null,1,MyDatabaseHelper.TABLE_FRIEND);
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
                            headList=new ArrayList<>();
                            if(headStringList.size()>0){
                                for(int i=0;i<headStringList.size();i++){
                                    if(headStringList.get(i)==null){
                                        headList.add(null);
                                    }else{
                                        headList.add(base64ToBitmap(headStringList.get(i)));
                                    }
                                }
                            }
                            handler.sendEmptyMessage(7);
                        }catch (Exception e){
                            e.printStackTrace();
                            AfterLoginActivity.headList=new ArrayList<>();
                            handler.sendEmptyMessage(-7);
                        }
                    }
                }).start();
            }
        });



    }

    public void initView2(){
        /*
        final RelativeLayout relativeLayout_top=(RelativeLayout)view2.findViewById(R.id.layout_top);
        final RelativeLayout relativeLayout_bill=(RelativeLayout)view2.findViewById(R.id.layout_bill);
        final RelativeLayout relativeLayout_load=(RelativeLayout)view2.findViewById(R.id.upload_layout);
        final TextView textView_jumping=(TextView)view2.findViewById(R.id.jumping_textView);
        flaskView_bill=(FlaskView)view2.findViewById(R.id.fv2);
        relativeLayout_top.setAlpha(1);
        relativeLayout_bill.setAlpha(1);
        relativeLayout_load.setVisibility(View.GONE);

         */

        // 建立数据源
        String[] mItems_type = getResources().getStringArray(R.array.type);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_type);
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        Spinner spinner_type=(Spinner)view2.findViewById(R.id.spinner_type);
        spinner_type.setAdapter(adapter_type);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.money);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //refreshBills();
        recyclerView_bill=(RecyclerView)view2.findViewById(R.id.recycleView_bill);

        //如果从SQLite加载订单的线程还活着
        if(sqliteThread.isAlive()){
            try{
                sqliteThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        billAdapter=new BillAdapter(billJsonArrayList,AfterLoginActivity.this);
        layoutManager_bill=new LinearLayoutManager(AfterLoginActivity.this);
        recyclerView_bill.addItemDecoration(new SpacesItemDecoration(20));
        recyclerView_bill.setLayoutManager(layoutManager_bill);
        recyclerView_bill.setAdapter(billAdapter);

        try{
            Field field = recyclerView_bill.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recyclerView_bill, MaxFLingVelocity);
        }catch (Exception e){
            e.printStackTrace();
        }


        // 外部对RecyclerView设置监听,静止时加载
        /*
        recyclerView_bill.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // State有三种状态：SCROLL_STATE_IDLE（静止）、SCROLL_STATE_DRAGGING（上升）、SCROLL_STATE_SETTLING（下落）
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // 滚动静止时才加载图片资源
                    billAdapter.setScrolling(false);
                    billAdapter.notifyDataSetChanged(); // notify调用后onBindViewHolder会响应调用
                } else{
                    billAdapter.setScrolling(true);
                    //historyAdapter.setFirstLoad(false);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

         */

        //下拉刷新
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view2.findViewById(R.id.layout_refresh_bill);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBills();
            }
        });


    }

    public void initView3(){

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setImageResource(R.drawable.individual);
        imageTextButton_space.setText("个人");
        imageTextButton_space.setTextSize(13);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setImageResource(R.drawable.enterprise);
        imageTextButton_record.setTextSize(13);
        imageTextButton_record.setText("企业");
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setImageResource(R.drawable.organization);
        imageTextButton_notebook.setTextSize(13);
        imageTextButton_notebook.setText("其他组织");
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_middle=(ImageTextButton)view3.findViewById(R.id.button_middle);
        imageTextButton_middle.setImageResource(R.drawable.middle);
        imageTextButton_middle.setTextSize(13);
        imageTextButton_middle.setText("中间人");
        imageTextButton_middle.setVisibility(View.GONE);

        final RelativeLayout relativeLayout_personal=(RelativeLayout)view3.findViewById(R.id.layout_personal);

        //隐藏取消按钮
        final ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        final ImageView imageView_camera=(ImageView)view3.findViewById(R.id.button_camera);
        final ImageButton imageButton_cancel=(ImageButton)view3.findViewById(R.id.button_cancel);
        imageView_photo.setVisibility(View.GONE);
        imageButton_cancel.setVisibility(View.GONE);
        imageButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_photo.setVisibility(View.GONE);
                imageView_camera.setVisibility(View.VISIBLE);
                imageButton_cancel.setVisibility(View.GONE);
            }
        });

        final ScrollView scrollView=(ScrollView)view3.findViewById(R.id.scrollView);
        Button button_bottom=(Button)view3.findViewById(R.id.button_bottom);
        button_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(View.FOCUS_UP);
                if(imageTextButton_notebook.getVisibility()!=View.VISIBLE&&relativeLayout_personal.getVisibility()!=View.VISIBLE){
                    imageTextButton_notebook.setVisibility(View.VISIBLE);
                    imageTextButton_record.setVisibility(View.VISIBLE);
                    imageTextButton_space.setVisibility(View.VISIBLE);
                    imageTextButton_middle.setVisibility(View.VISIBLE);
                }

            }
        });

        // 建立数据源
        String[] mItems_money = getResources().getStringArray(R.array.money);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_money = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_money);
        adapter_money.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        Spinner spinner_money=(Spinner)view3.findViewById(R.id.spinner_money);
        spinner_money.setAdapter(adapter_money);
        spinner_money.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.money);
                switch(number[pos]){
                    case "人民币":
                        currency=Bill.RMB;
                        break;
                    case "美元":
                        currency=Bill.USD;
                        break;
                    case "欧元":
                        currency=Bill.EUR;
                        break;
                    case "英镑":
                        currency=Bill.GBP;
                        break;
                    case "港元":
                        currency=Bill.HKD;
                        break;
                    case "日元":
                        currency=Bill.JPY;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //上传订单
        Button button_submit=(Button)view3.findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                executeAnimation(view);
                //上传Bill到服务器

                EditText editText_product=(EditText)view3.findViewById(R.id.editText_product);
                final String productName=editText_product.getText().toString();
                EditText editText_price=(EditText)view3.findViewById(R.id.editText_money);
                final String price=editText_price.getText().toString();
                EditText editText_detail=(EditText)view3.findViewById(R.id.editText_detail);
                final String detail=editText_detail.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson=new Gson();
                        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String issureID=preferences.getString("id","empty");
                        BillJson billJson=new BillJson(getBillID(10),issureID,productName,price,currency,type,"",base64Str,detail,false);
                        String json=gson.toJson(billJson);
                        String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/upload/bill",json);

                    }
                }).start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageTextButton_notebook.setVisibility(View.GONE);
                        imageTextButton_record.setVisibility(View.GONE);
                        imageTextButton_space.setVisibility(View.GONE);
                        imageTextButton_middle.setVisibility(View.GONE);
                        relativeLayout_personal.setVisibility(View.GONE);
                        Toast.makeText(view.getContext(),"发布订单成功！",Toast.LENGTH_SHORT).show();
                    }
                });

                bitmap_photo=null;
                base64Str=null;
            }
        });


        final ImageButton imageButton_add=(ImageButton)view3.findViewById(R.id.button_add);
        imageButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
                if(imageTextButton_notebook.getVisibility()==View.GONE&&imageTextButton_record.getVisibility()==View.GONE&&imageTextButton_space.getVisibility()==View.GONE&&relativeLayout_personal.getVisibility()==View.GONE){
                    imageTextButton_notebook.setVisibility(View.VISIBLE);
                    imageTextButton_record.setVisibility(View.VISIBLE);
                    imageTextButton_space.setVisibility(View.VISIBLE);
                    imageTextButton_middle.setVisibility(View.VISIBLE);
                }else if(relativeLayout_personal.getVisibility()== View.GONE&&imageTextButton_middle.getVisibility()==View.VISIBLE){
                    imageTextButton_notebook.setVisibility(View.GONE);
                    imageTextButton_record.setVisibility(View.GONE);
                    imageTextButton_space.setVisibility(View.GONE);
                    imageTextButton_middle.setVisibility(View.GONE);
                    relativeLayout_personal.setVisibility(View.GONE);
                }else if(imageTextButton_record.getVisibility()==View.VISIBLE){
                    imageTextButton_notebook.setVisibility(View.GONE);
                    imageTextButton_record.setVisibility(View.GONE);
                    imageTextButton_space.setVisibility(View.GONE);
                    imageTextButton_middle.setVisibility(View.GONE);
                }else if(relativeLayout_personal.getVisibility()==View.VISIBLE&&imageTextButton_middle.getVisibility()==View.GONE){
                    relativeLayout_personal.setVisibility(View.GONE);
                }

            }
        });

    }

    public void initView4(){
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String nickname=preferences.getString("nickname","");
        final TextView textView_nickname=(TextView)view4.findViewById(R.id.textView_nickname);
        textView_nickname.setText(nickname);
        String id=preferences.getString("id","");
        TextView textView_id=(TextView)view4.findViewById(R.id.textView_id);
        textView_id.setText(id);

        //头像
        CircleImageView circleImageView=(CircleImageView)view4.findViewById(R.id.circleView);
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


        //修改昵称
        final ImageButton imageButton_change=(ImageButton)view4.findViewById(R.id.button_change);
        final ImageButton imageButton_update=(ImageButton)view4.findViewById(R.id.button_update);
        final EditText editText_nickname=(EditText)view4.findViewById(R.id.editText_nickname);
        imageButton_change.setVisibility(View.VISIBLE);
        imageButton_update.setVisibility(View.INVISIBLE);
        textView_nickname.setVisibility(View.VISIBLE);
        editText_nickname.setVisibility(View.INVISIBLE);
        imageButton_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_nickname.setText(nickname);
                imageButton_change.setVisibility(View.INVISIBLE);
                textView_nickname.setVisibility(View.INVISIBLE);
                editText_nickname.setVisibility(View.VISIBLE);
                imageButton_update.setVisibility(View.VISIBLE);
            }
        });

        imageButton_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname=editText_nickname.getText().toString();
                imageButton_update.setVisibility(View.INVISIBLE);
                imageButton_change.setVisibility(View.VISIBLE);
                editText_nickname.setVisibility(View.INVISIBLE);
                textView_nickname.setVisibility(View.VISIBLE);
                textView_nickname.setText(nickname);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("nickname",nickname);
                editor.apply();
            }
        });

        //选择身份
        ImageTextButton imageTextButton_individual=(ImageTextButton)view4.findViewById(R.id.button_individual);
        imageTextButton_individual.setImageResource(R.drawable.individual);
        imageTextButton_individual.setText("个人");
        imageTextButton_individual.setTextSize(13);

        ImageTextButton imageTextButton_enterprise=(ImageTextButton)view4.findViewById(R.id.button_enterprise);
        imageTextButton_enterprise.setImageResource(R.drawable.enterprise);
        imageTextButton_enterprise.setText("企业");
        imageTextButton_enterprise.setTextSize(13);

        ImageTextButton imageTextButton_organization=(ImageTextButton)view4.findViewById(R.id.button_organization);
        imageTextButton_organization.setImageResource(R.drawable.organization);
        imageTextButton_organization.setText("其他组织");
        imageTextButton_organization.setTextSize(13);

        ImageTextButton imageTextButton_middle=(ImageTextButton)view4.findViewById(R.id.button_middle);
        imageTextButton_middle.setImageResource(R.drawable.middle);
        imageTextButton_middle.setText("中间人");
        imageTextButton_middle.setTextSize(13);

        final CheckBox checkBox_boy=(CheckBox)view4.findViewById(R.id.checkbox_boy);
        final CheckBox checkBox_girl=(CheckBox)view4.findViewById(R.id.checkbox_girl);
        boolean sex=preferences.getBoolean("sex",true);
        if(sex){
            checkBox_boy.setChecked(true);
        }else{
            checkBox_girl.setChecked(true);
        }
        checkBox_boy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox_boy.isChecked()){
                    checkBox_girl.setChecked(false);
                }else{
                    checkBox_girl.setChecked(true);
                }
            }
        });
        checkBox_girl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox_girl.isChecked()){
                    checkBox_boy.setChecked(false);
                }else{
                    checkBox_boy.setChecked(true);
                }
            }
        });

        year=preferences.getInt("year",0);
        String[] mItems_year = getResources().getStringArray(R.array.year);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_year = (Spinner) view4.findViewById(R.id.spinner_year);
        //绑定 Adapter到控件
        String[] number = getResources().getStringArray(R.array.year);
        spinner_year.setAdapter(adapter_year);
        for(int i=0;i<number.length;i++){
            if(number[i].equals(String.valueOf(year))){
                spinner_year.setSelection(i);
            }
        }
        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.year);
                year=Integer.parseInt(number[pos]);
                //Toast.makeText(EvidenceDeliverActivity.this, "你选择了阶段:"+number[pos],Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        month=preferences.getInt("month",1);
        String[] mItems_month = getResources().getStringArray(R.array.month);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_month);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_month = (Spinner) view4.findViewById(R.id.spinner_month);
        //绑定 Adapter到控件
        spinner_month.setAdapter(adapter_month);
        String[] number2 = getResources().getStringArray(R.array.month);
        for(int i=0;i<number2.length;i++){
            if(number2[i].equals(String.valueOf(month))){
                spinner_month.setSelection(i);
            }
        }
        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.month);
                month=Integer.parseInt(number[pos]);
                //Toast.makeText(EvidenceDeliverActivity.this, "你选择了阶段:"+number[pos],Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        day=preferences.getInt("day",0);
        String[] mItems_day = getResources().getStringArray(R.array.day);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_day = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_day);
        adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_day = (Spinner) view4.findViewById(R.id.spinner_day);
        //绑定 Adapter到控件
        spinner_day.setAdapter(adapter_day);
        String[] number3 = getResources().getStringArray(R.array.day);
        for(int i=0;i<number3.length;i++){
            if(number3[i].equals(String.valueOf(day))){
                spinner_day.setSelection(i);
            }
        }
        spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.day);
                day=Integer.parseInt(number[pos]);
                //Toast.makeText(EvidenceDeliverActivity.this, "你选择了阶段:"+number[pos],Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        String job=preferences.getString("job"," ");
        final ImageButton button_change_job=(ImageButton)view4.findViewById(R.id.button_change_job);
        final ImageButton button_save_job=(ImageButton)view4.findViewById(R.id.button_save_job);
        final TextView textView_job_content=(TextView)view4.findViewById(R.id.textView_job_content);
        textView_job_content.setText(job);
        final EditText editText_job=(EditText)view4.findViewById(R.id.editText_job);
        button_change_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_change_job.setVisibility(View.INVISIBLE);
                String job=textView_job_content.getText().toString();
                editText_job.setText(job);
                editText_job.setVisibility(View.VISIBLE);
                textView_job_content.setVisibility(View.INVISIBLE);
                button_save_job.setVisibility(View.VISIBLE);
            }
        });
        button_save_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String job=editText_job.getText().toString();
                button_save_job.setVisibility(View.INVISIBLE);
                textView_job_content.setText(job);
                editText_job.setVisibility(View.INVISIBLE);
                button_change_job.setVisibility(View.VISIBLE);
                textView_job_content.setVisibility(View.VISIBLE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("job",job);
                editor.apply();
            }
        });

        String company=preferences.getString("company"," ");
        final ImageButton button_change_company=(ImageButton)view4.findViewById(R.id.button_change_company);
        final ImageButton button_save_company=(ImageButton)view4.findViewById(R.id.button_save_company);
        final TextView textView_company_content=(TextView)view4.findViewById(R.id.textView_company_content);
        textView_company_content.setText(company);
        final EditText editText_company=(EditText)view4.findViewById(R.id.editText_company);
        button_change_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_change_company.setVisibility(View.INVISIBLE);
                String company=textView_company_content.getText().toString();
                editText_company.setText(company);
                editText_company.setVisibility(View.VISIBLE);
                textView_company_content.setVisibility(View.INVISIBLE);
                button_save_company.setVisibility(View.VISIBLE);
            }
        });
        button_save_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String company=editText_company.getText().toString();
                button_save_company.setVisibility(View.INVISIBLE);
                textView_company_content.setText(company);
                editText_company.setVisibility(View.INVISIBLE);
                button_change_company.setVisibility(View.VISIBLE);
                textView_company_content.setVisibility(View.VISIBLE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("company",company);
                editor.apply();
            }
        });


    }

    /**
     * 播放回调类
     */
    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(AfterLoginActivity.this,"播放结束！",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //请求订单结束
                case 1:
                    if(viewState==VIEW2){
                        billAdapter.setBillJsonList(billJsonArrayList);
                        billAdapter.notifyDataSetChanged();
                        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view2.findViewById(R.id.layout_refresh_bill);
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        billAdapter.setBillJsonList(billJsonArrayList);
                    }
                    Toast.makeText(AfterLoginActivity.this,"刷新完毕!",Toast.LENGTH_SHORT).show();
                    break;

                    //请求订单失败
                case -1:
                    Toast.makeText(AfterLoginActivity.this,"请求订单失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //头像请求结束
                case 2:
                    CircleImageView circleImageView=(CircleImageView)view4.findViewById(R.id.circleView);
                    circleImageView.setImageBitmap(bitmap_photo);
                    break;

                    //从SQLite获得好友列表结束
                //或从服务器请求好友列表结束
                case 3:
                    //当头像已经加载完成时
                    if(FRIEND_HEAD_GET_COMPLETE){
                        RecyclerView recyclerView_friend=(RecyclerView)view1.findViewById(R.id.recycleView_friend);
                        friendAdapter=new FriendAdapter(friendList,headList);
                        layoutManager_friend=new LinearLayoutManager(AfterLoginActivity.this);
                        recyclerView_friend.addItemDecoration(new SpacesItemDecoration(10));
                        recyclerView_friend.setLayoutManager(layoutManager_friend);
                        recyclerView_friend.setAdapter(friendAdapter);

                        SwipeRefreshLayout swipeRefreshLayout_friend=(SwipeRefreshLayout)view1.findViewById(R.id.layout_refresh_friend);
                        swipeRefreshLayout_friend.setRefreshing(false);

                        FRIEND_HEAD_GET_COMPLETE=false;
                        FRIEND_GET_COMPLETE=false;
                    }else{
                        FRIEND_GET_COMPLETE=true;
                    }
                    break;

                    //请求好友列表失败
                case -3:
                    Toast.makeText(AfterLoginActivity.this,"请求好友列表失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //请求好友列表的头像结束
                case 7:
                    //当好友列表请求完成时
                    if(FRIEND_GET_COMPLETE){
                        RecyclerView recyclerView_friend=(RecyclerView)view1.findViewById(R.id.recycleView_friend);
                        friendAdapter=new FriendAdapter(friendList,headList);
                        layoutManager_friend=new LinearLayoutManager(AfterLoginActivity.this);
                        recyclerView_friend.addItemDecoration(new SpacesItemDecoration(10));
                        recyclerView_friend.setLayoutManager(layoutManager_friend);
                        recyclerView_friend.setAdapter(friendAdapter);

                        SwipeRefreshLayout swipeRefreshLayout_friend=(SwipeRefreshLayout)view1.findViewById(R.id.layout_refresh_friend);
                        swipeRefreshLayout_friend.setRefreshing(false);

                        FRIEND_HEAD_GET_COMPLETE=false;
                        FRIEND_GET_COMPLETE=false;
                    }else{
                        FRIEND_HEAD_GET_COMPLETE=true;
                    }

                    //请求好友列表头像失败
                case -7:
                    Toast.makeText(AfterLoginActivity.this,"请求好友头像失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //同意好友申请成功
                case 4:
                    Toast.makeText(AfterLoginActivity.this, "同意好友申请成功！", Toast.LENGTH_SHORT).show();
                    break;

                    //同意好友申请失败
                case -4:
                    Toast.makeText(AfterLoginActivity.this, "同意好友申请失败！", Toast.LENGTH_SHORT).show();
                    break;

                //插入好友列表成功
                case 5:
                    Toast.makeText(AfterLoginActivity.this, "插好友列表成功！", Toast.LENGTH_SHORT).show();
                    break;

                //同意好友申请失败
                case -5:
                    Toast.makeText(AfterLoginActivity.this, "插入好友列表失败！", Toast.LENGTH_SHORT).show();
                    break;

                    //更新个人信息失败
                case -6:
                    Toast.makeText(AfterLoginActivity.this,"更新个人信息失败！",Toast.LENGTH_SHORT).show();
                    break;

            };
        }
    };

    @SuppressLint("HandlerLeak")
    Handler versionHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            String latestVersion=bundle.getString("version");
            UpdateDialog updateDialog=new UpdateDialog(AfterLoginActivity.this,latestVersion);
            updateDialog.setCanceledOnTouchOutside(true);
            updateDialog.show();

        }
    };

    @SuppressLint("HandlerLeak")
    Handler applyHandler=new Handler(){
        public void handleMessage(Message message){
            Bundle bundle=message.getData();
            String id=bundle.getString("fromid");
            ApplyDialog applyDialog=new ApplyDialog(AfterLoginActivity.this,id);
            applyDialog.setCanceledOnTouchOutside(true);
            applyDialog.show();
        }

    };



    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    public void onClickCancel(View view){
        EditText editText_name=(EditText)view1.findViewById(R.id.editText_name);
        EditText editText_phone=(EditText)view1.findViewById(R.id.editText_phone);
        editText_name.setText("");
        editText_phone.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_LOCATION:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(AfterLoginActivity.this,"定位功能受限！",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(AfterLoginActivity.this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case CHOOSE_PHOTO:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(AfterLoginActivity.this,"you denied the permission!", Toast.LENGTH_SHORT).show();
                }
                break;

            case TAKE_PHOTO:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //创建File，用于存储照片
                    File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                    try{
                        if(outputImage.exists()){
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(Build.VERSION.SDK_INT>=24){
                        imageUri= FileProvider.getUriForFile(AfterLoginActivity.this,"com.example.yzn.fileprovider",outputImage);
                    }else {
                        imageUri=Uri.fromFile(outputImage);
                    }
                    //启动相机
                    Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                }else{
                    Toast.makeText(AfterLoginActivity.this,"you denied the permission!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_SCAN:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(AfterLoginActivity.this, CaptureActivity.class);
                    /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                     * 也可以不传这个参数
                     * 不传的话  默认都为默认不震动  其他都为true
                     * */

                    ZxingConfig config = new ZxingConfig();
                    //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
                    //config.setPlayBeep(true);//是否播放提示音
                    config.setShake(true);//是否震动
                    //config.setShowAlbum(true);//是否显示相册
                    //config.setShowFlashLight(true);//是否显示闪光灯
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }else{
                    Toast.makeText(AfterLoginActivity.this,"you denied the permission!", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        //每隔10秒更新位置
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        //高精度模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭位置
        mLocationClient.stop();
        //关闭天气服务
        Intent intent_weather=new Intent(this, WeatherService.class);
        stopService(intent_weather);

        //关闭bgm
        if(intent_bgm!=null){
            stopService(intent_bgm);
        }
    }

    private void sendBroadcastData(int type,int musicID) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.putExtra("musicID",musicID);
        intent.setAction("flag");
        sendBroadcast(intent);
    }

    public void initContacts(){
        ContentResolver contentResolver=AfterLoginActivity.this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        Log.d("SmallLetters", ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
        //personArrayList=new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                /*
                Person person=new Person(personArrayList.size(),name,number,Person.CONTACT_PUBLIC);
                personArrayList.add(0,person);

                 */
            }
            //更新数据
            cursor.close();

        }

    }

    public void onClickPersonal(View view){

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageTextButton imageButton_middle=(ImageTextButton)view3.findViewById(R.id.button_middle);
        imageButton_middle.setVisibility(View.GONE);

        RelativeLayout relativeLayout_personal=(RelativeLayout)view3.findViewById(R.id.layout_personal);
        ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        TextView textView_name=(TextView)view3.findViewById(R.id.textView_name);
        textView_name.setText("个人名称");
        relativeLayout_personal.setVisibility(View.VISIBLE);
        imageView_photo.setVisibility(View.GONE);
        type=Bill.Individual;
    }

    public void onClickEnterprise(View view){

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageTextButton imageButton_middle=(ImageTextButton)view3.findViewById(R.id.button_middle);
        imageButton_middle.setVisibility(View.GONE);

        RelativeLayout relativeLayout_personal=(RelativeLayout)view3.findViewById(R.id.layout_personal);
        ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        TextView textView_name=(TextView)view3.findViewById(R.id.textView_name);
        textView_name.setText("企业名称");
        relativeLayout_personal.setVisibility(View.VISIBLE);
        imageView_photo.setVisibility(View.GONE);
        type=Bill.Enterprise;
    }

    public void onClickOrganization(View view){

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageTextButton imageButton_middle=(ImageTextButton)view3.findViewById(R.id.button_middle);
        imageButton_middle.setVisibility(View.GONE);

        RelativeLayout relativeLayout_personal=(RelativeLayout)view3.findViewById(R.id.layout_personal);
        ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        TextView textView_name=(TextView)view3.findViewById(R.id.textView_name);
        textView_name.setText("组织名称");
        relativeLayout_personal.setVisibility(View.VISIBLE);
        imageView_photo.setVisibility(View.GONE);
        type=Bill.Organization;
    }

    public void onClickMiddle(View view){

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageTextButton imageButton_middle=(ImageTextButton)view3.findViewById(R.id.button_middle);
        imageButton_middle.setVisibility(View.GONE);

        RelativeLayout relativeLayout_personal=(RelativeLayout)view3.findViewById(R.id.layout_personal);
        ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        TextView textView_name=(TextView)view3.findViewById(R.id.textView_name);
        textView_name.setText("上家名称");
        relativeLayout_personal.setVisibility(View.VISIBLE);
        imageView_photo.setVisibility(View.GONE);
        type=Bill.Middle;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将照片显示出来
                        if(viewState==VIEW1){
                            bitmap_photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            base64Str=bitmapToBase64(bitmap_photo);
                            ImageView imageView_camere=(ImageView)view3.findViewById(R.id.button_camera);
                            ImageView imageView_photo=(ImageView) view3.findViewById(R.id.imageView_photo);
                            imageView_camere.setVisibility(View.GONE);
                            imageView_photo.setImageBitmap(compressQualit2(bitmap_photo));
                            imageView_photo.setVisibility(View.VISIBLE);
                            ImageButton imageButton_cancel=(ImageButton)view3.findViewById(R.id.button_cancel);
                            imageButton_cancel.setVisibility(View.VISIBLE);
                        }else if(viewState==VIEW4){
                            bitmap_photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            CircleImageView circleImageView=(CircleImageView)view4.findViewById(R.id.circleView);
                            circleImageView.setImageBitmap(bitmap_photo);
                            CircleImageView circleImageView_title=(CircleImageView)findViewById(R.id.title_left);
                            circleImageView_title.setImageBitmap(bitmap_photo);
                            //保存至SD卡
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    saveBitmapToLocal("headPicture.jpg",bitmap_photo);
                                }
                            }).start();

                            //将头像上传服务器
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    Gson gson=new Gson();
                                    HeadJson headJson=new HeadJson(preference.getString("id",""),bitmapToBase64(bitmap_photo));
                                    String json=gson.toJson(headJson);
                                    httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/upload/head",json);
                                }
                            }).start();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        assert data != null;
                        handleImageBeforeKitKat(data);
                    }
                }
                break;

                //扫码结果
            case REQUEST_CODE_SCAN:
                if (data != null) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    Intent intent=new Intent(AfterLoginActivity.this, AccountInfoActivity.class);
                    intent.putExtra("qrJson",content);
                    startActivity(intent);
                    break;
                }
            default:
                break;
        }
    }

    public void initSpace(){
        spaceArrayList=new ArrayList<>();
        /*
        myDatabaseHelper=new MyDatabaseHelper(getApplicationContext(),"Space.db",null,1,3);
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        Cursor cursor=db.query("Space",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            try{
                do{
                    String date=cursor.getString(cursor.getColumnIndex("date"));
                    String note=cursor.getString(cursor.getColumnIndex("note"));
                    byte[] bytes=cursor.getBlob(cursor.getColumnIndex("T_BLOB"));
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Space space=new Space(date,bitmap,note);
                    spaceArrayList.add(0,space);
                }while(cursor.moveToNext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();

         */
    }

    public void onClickVedio(View view){
        if(videoState==VIDEO_PAUSE){

        }
    }

    //底部弹出动画
    private void setDialog() {
        final Dialog mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);

        //打开相册
        root.findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.cancel();
                if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AfterLoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CHOOSE_PHOTO);
                }else{
                    openAlbum();
                }
            }
        });

        //打开摄像头
        root.findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.cancel();

                if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AfterLoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},TAKE_PHOTO);
                }else{
                    //创建File，用于存储照片
                    File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                    try{
                        if(outputImage.exists()){
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(Build.VERSION.SDK_INT>=24){
                        imageUri= FileProvider.getUriForFile(AfterLoginActivity.this,"com.example.yzn.fileprovider",outputImage);
                    }else {
                        imageUri=Uri.fromFile(outputImage);
                    }
                    //启动相机
                    Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                }

            }
        });

        //取消
        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.cancel();
            }
        });

        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    //打开相册
    private  void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }


    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docID=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docID.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri ContentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docID));
                imagePath=getImagePath(ContentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data ){
        Uri uri=data.getData();
        String imagePath =getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        //存储imagePath
        //this.imagePath=path;
        //存储无损bitmap
        if(path!=null){
            /*
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPreferredConfig=Bitmap.Config.ARGB_8888;
            bitmap=BitmapFactory.decodeFile(path);

             */
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            //压缩图片
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPreferredConfig= Bitmap.Config.ARGB_8888;
            final Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            if(viewState==VIEW1){
                ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
                imageView_photo.setImageBitmap(compressQualit2(bitmap));
                imageView_photo.setVisibility(View.VISIBLE);
                base64Str=bitmapToBase64(bitmap);
                //显示取消按钮
                ImageView imageView_camera=(ImageView)view3.findViewById(R.id.button_camera);
                imageView_camera.setVisibility(View.GONE);
                ImageButton imageButton_cancel=(ImageButton)view3.findViewById(R.id.button_cancel);
                imageButton_cancel.setVisibility(View.VISIBLE);
            }else if(viewState==VIEW4){
                CircleImageView circleImageView=(CircleImageView)view4.findViewById(R.id.circleView);
                circleImageView.setImageBitmap(bitmap);
                CircleImageView circleImageView_title=(CircleImageView)findViewById(R.id.title_left);
                circleImageView_title.setImageBitmap(bitmap);
                //保存至SD卡
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveBitmapToLocal("headPicture.jpg",bitmap);
                    }
                }).start();

                //将头像上传服务器
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        Gson gson=new Gson();
                        HeadJson headJson=new HeadJson(preference.getString("id",""),bitmapToBase64(bitmap));
                        String json=gson.toJson(headJson);
                        httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/upload/head",json);
                    }
                }).start();
            }

        }else{
            Toast.makeText(this,"failed to get the image",Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickCamera(View view){
        setDialog();
    }

    //我的身份
    public void onClickIdentity(View view){
        //选择身份
        ImageTextButton imageTextButton_individual=(ImageTextButton)view4.findViewById(R.id.button_individual);
        ImageTextButton imageTextButton_enterprise=(ImageTextButton)view4.findViewById(R.id.button_enterprise);
        ImageTextButton imageTextButton_organization=(ImageTextButton)view4.findViewById(R.id.button_organization);
        ImageTextButton imageTextButton_middle=(ImageTextButton)view4.findViewById(R.id.button_middle);
        ImageView imageView_direction=(ImageView)view4.findViewById(R.id.imageView_direction);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(imageTextButton_individual.getVisibility()!=View.VISIBLE){
            imageTextButton_individual.setVisibility(View.VISIBLE);
            imageTextButton_enterprise.setVisibility(View.VISIBLE);
            imageTextButton_organization.setVisibility(View.VISIBLE);
            imageTextButton_middle.setVisibility(View.VISIBLE);
            imageView_direction.setBackgroundResource(R.drawable.up);
            if(preferences.getBoolean("individual",false)){
                imageTextButton_individual.setAlpha(1);
            }else{
                imageTextButton_individual.setAlpha((float) 0.3);
            }
            if(preferences.getBoolean("enterprise",true)){
                imageTextButton_enterprise.setAlpha(1);
            }else{
                imageTextButton_enterprise.setAlpha((float) 0.3);
            }
            if(preferences.getBoolean("organization",true)){
                imageTextButton_organization.setAlpha(1);
            }else{
                imageTextButton_organization.setAlpha((float) 0.3);
            }
            if(preferences.getBoolean("middle",true)){
                imageTextButton_middle.setAlpha(1);
            }else{
                imageTextButton_middle.setAlpha(0.3f);
            }
        }else{
            imageTextButton_individual.setVisibility(View.GONE);
            imageTextButton_enterprise.setVisibility(View.GONE);
            imageTextButton_organization.setVisibility(View.GONE);
            imageTextButton_middle.setVisibility(View.GONE);
            imageView_direction.setBackgroundResource(R.drawable.down);
        }

    }

    //我的资料
    public void onCLickInformation(View view){
        boolean sex=true;
        RelativeLayout relativeLayout=(RelativeLayout)view4.findViewById(R.id.layout_information_inside);
        ImageView imageView=(ImageView) view4.findViewById(R.id.imageView_direction2);
        CheckBox checkBox=(CheckBox)view4.findViewById(R.id.checkbox_boy);
        if(!(relativeLayout.getVisibility() ==View.VISIBLE)){
            relativeLayout.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.drawable.up);
        }else{
            relativeLayout.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.down);
            if(!checkBox.isChecked()){
                sex=false;
            }
            final boolean finalSex = sex;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("year",year);
                    editor.putInt("month",month);
                    editor.putInt("day",day);
                    editor.putBoolean("sex", finalSex);
                    editor.apply();
                }
            }).start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        ///活动不可见时更新个人信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                Gson gson=new Gson();
                String id=preferences.getString("id","");
                String phone=preferences.getString("number","");
                String nickname=preferences.getString("nickname","");
                int sex;
                if(preferences.getBoolean("sex",true)){
                    sex=0;
                }else{
                    sex=1;
                }
                String birthday=String.valueOf(preferences.getInt("year",2020))+"-"+String.valueOf(preferences.getInt("month",1))+"-"+String.valueOf(preferences.getInt("day",1));
                String company=preferences.getString("company","");
                String job=preferences.getString("job","");
                String province=preferences.getString("province","");
                String city=preferences.getString("city","");
                Info info=new Info(id,phone,nickname,sex,birthday,company,job,province,city);
                String json=gson.toJson(info);
                String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/info/upgrade",json);
                if(!response.equals("info upgrade complete")){
                    handler.sendEmptyMessage(-6);
                }
            }
        }).start();

        //将新订单存入数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                Cursor cursor=db.query("bill",null,null,null,null,null,null);
                List<String> dbBillList=new ArrayList<>();
                if(cursor.moveToFirst()){
                    do{
                        String billID=cursor.getString(cursor.getColumnIndex("billID"));
                        if(billID!=null){
                            dbBillList.add(billID);
                        }
                    }while(cursor.moveToNext());
                }
                cursor.close();
                for(int i=0;i<billJsonArrayList.size();i++){
                    boolean isMatch=false;
                    BillJson billJson=billJsonArrayList.get(i);
                    for(int j=0;j<dbBillList.size();j++){
                        if(billJson.getBillID().equals(dbBillList.get(j))){
                            isMatch=true;
                            break;
                        }
                    }

                    //在SQLite中没匹配到就存入
                    if(!isMatch){
                        ContentValues values=new ContentValues();
                        values.put("billID",billJson.getBillID());
                        values.put("issureID",billJson.getIssuerID());
                        values.put("productName",billJson.getProductName());
                        values.put("price",billJson.getPrice());
                        values.put("type",billJson.getType());
                        values.put("middleName",billJson.getMiddleName());
                        values.put("currency",billJson.getCurrency());
                        values.put("detail",billJson.getDetail());
                        db.insert("bill",null,values);
                        values.clear();
                    }
                }
            }
        }).start();
    }

    //我的二维码名片
    public void onClickQR(View view){
        Intent intent=new Intent(AfterLoginActivity.this,QRActivity.class);
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

    public void onClickIdentity1(View view){
        final ImageTextButton imageTextButton=(ImageTextButton)view4.findViewById(R.id.button_individual);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        if(preferences.getBoolean("individual",false)){
            imageTextButton.setAlpha(0.3f);
            editor.putBoolean("individual",false);
        }else{
            imageTextButton.setAlpha(1);
            editor.putBoolean("individual",true);
        }
        editor.apply();
    }

    public void onClickIdentity2(View view){
        final ImageTextButton imageTextButton=(ImageTextButton)view4.findViewById(R.id.button_enterprise);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        if(preferences.getBoolean("enterprise",false)){
            imageTextButton.setAlpha(0.3f);
            editor.putBoolean("enterprise",false);
        }else{
            imageTextButton.setAlpha(1);
            editor.putBoolean("enterprise",true);
        }
        editor.apply();
    }

    public void onClickIdentity3(View view){
        final ImageTextButton imageTextButton=(ImageTextButton)view4.findViewById(R.id.button_organization);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        if(preferences.getBoolean("organization",false)){
            imageTextButton.setAlpha(0.3f);
            editor.putBoolean("organization",false);
        }else{
            imageTextButton.setAlpha(1);
            editor.putBoolean("organization",true);
        }
        editor.apply();
    }

    public void onClickIdentity4(View view){
        final ImageTextButton imageTextButton=(ImageTextButton)view4.findViewById(R.id.button_middle);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        if(preferences.getBoolean("middle",false)){
            imageTextButton.setAlpha(0.3f);
            editor.putBoolean("middle",false);
        }else{
            imageTextButton.setAlpha(1);
            editor.putBoolean("middle",true);
        }
        editor.apply();
    }

    public void onClickHeadPicture(View view){
        Intent intent=new Intent(AfterLoginActivity.this,MySettingActivty.class);
        startActivity(intent);
    }

    private Bitmap compressQuality(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bos);
        byte[] bytes = bos.toByteArray();
        Log.d(TAG, "compressQuality: length = "+bytes.length);

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
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

    //日期+随机数10位产生billID
    public static String getBillID(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        Calendar calendar=Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR))+String.valueOf(calendar.get(Calendar.MONTH)+1)+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+sb.toString();
    }

    //bitmap转base64
    public String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    //向服务器请求订单
    public void refreshBills(){
        //final RelativeLayout relativeLayout_top=(RelativeLayout)view2.findViewById(R.id.layout_top);
        //final RelativeLayout relativeLayout_bill=(RelativeLayout)view2.findViewById(R.id.layout_bill);
        //final RelativeLayout relativeLayout_load=(RelativeLayout)view2.findViewById(R.id.upload_layout);
        //final TextView textView_jumping=(TextView)view2.findViewById(R.id.jumping_textView);
        //flaskView_bill=(FlaskView)view2.findViewById(R.id.fv2);

        final Thread httpThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Gson gson=new Gson();
                    Type type=new TypeToken<List<BillJson>>(){}.getType();
                    String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/download");
                    billJsonArrayList=gson.fromJson(response,type);
                    //initBitmaps(billJsonList);
                    //AfterLoginActivity.billJsonArrayList=billJsonList;
                    //stop_flaskView=true;
                    handler.sendEmptyMessage(1);

                    //向SQLite更新全部数据
                    myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
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
                    handler.sendEmptyMessage(-1);
                    myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"bill.db",null,1,MyDatabaseHelper.TABLE_BILL);
                    SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                    db.delete("bill",null,null);
                }

            }
        });
        httpThread.start();

        //处于界面2时才进行加载
        /*
        if(viewState==VIEW2){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //模糊化背景
                    relativeLayout_bill.setAlpha(0.2f);
                    relativeLayout_top.setAlpha(0.2f);
                    relativeLayout_load.setVisibility(View.VISIBLE);


                    water_percentage=0.35f;
                    flaskView_bill.setWaterHeightPercent(water_percentage);

                    //文字跳动
                    SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(textView_jumping.getText());
                    buildWavingSpans(spannableStringBuilder,textView_jumping);
                    textView_jumping.setText(spannableStringBuilder);

                    //拦截点击事件
                    allowTouch=false;
                    //烧瓶水位上升
                    final CountDownTimer countDownTimer=new CountDownTimer(10000,25) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if(water_percentage==1){
                                water_percentage=0.35f;
                            }
                            water_percentage=water_percentage+0.005f;
                            flaskView_bill.setWaterHeightPercent(water_percentage);
                            if(stop_flaskView){
                                if(flaskView_bill.getVisibility()==View.VISIBLE){
                                    flaskView_bill.stop();
                                    flaskView_bill.release();
                                    stop_flaskView=false;
                                    relativeLayout_bill.setAlpha(1);
                                    relativeLayout_top.setAlpha(1);
                                    relativeLayout_load.setVisibility(View.GONE);
                                }
                                water_percentage=0.35f;
                                allowTouch=true;
                                cancel();
                            }
                        }

                        @Override
                        public void onFinish() {
                            //活动结束时回收flaskView
                            if(isFinishing())
                                flaskView_bill.release();
                            else flaskView_bill.stop();

                            stop_flaskView=false;
                            allowTouch=true;
                            relativeLayout_bill.setAlpha(1);
                            relativeLayout_top.setAlpha(1);
                            relativeLayout_load.setVisibility(View.GONE);
                            //handler.sendEmptyMessage(1);
                            //Toast.makeText(AfterLoginActivity.this,"证据上传成功！",Toast.LENGTH_LONG).show();
                            //viewPager.setCurrentItem(0);
                        }
                    };
                    countDownTimer.start();
                    flaskView_bill.start();


                }


            });
        }else{
            billAdapter.setBillJsonList(billJsonArrayList);
        }

         */

    }

    //上传动画时拦截点击事件，返回true时拦截
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    //文字跳动
    private JumpingSpan[] buildWavingSpans(SpannableStringBuilder sbb, TextView tv) {
        JumpingSpan[] spans;
        int loopDuration = 1000;
        int startPos = 0;//textview字体的开始位置
        int endPos = tv.getText().length();//结束位置
        int waveCharDelay = loopDuration / (3 * (endPos - startPos));//每个字体延迟的时间

        spans = new JumpingSpan[endPos - startPos];
        for (int pos = startPos; pos < endPos; pos++) {//设置每个字体的jumpingspan
            JumpingSpan jumpingBean = new JumpingSpan(tv, loopDuration, pos - startPos, waveCharDelay, 0.65f);
            sbb.setSpan(jumpingBean, pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spans[pos - startPos] = jumpingBean;
        }
        return spans;
    }

    //检查更新
    public void checkVersion(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获得版本名字
                PackageManager packageManager=context.getPackageManager();
                String versionName=null;
                try{
                    PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
                    versionName=packageInfo.versionName;
                }catch (PackageManager.NameNotFoundException e){
                    e.printStackTrace();
                }
                String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/update/check?version="+versionName);
                Gson gson=new Gson();
                Type type=new TypeToken<ApkVersion>(){}.getType();
                ApkVersion apkVersion=gson.fromJson(response,type);
                boolean needUpdate=!apkVersion.isLatest();
                if(needUpdate){
                    Bundle bundle=new Bundle();
                    bundle.putString("version",apkVersion.getLatestVersion());
                    Message message=Message.obtain();
                    message.setData(bundle);
                    versionHandler.sendMessage(message);
                }
            }
        }).start();
    }

    //检查好友申请
    public void checkFriendApply(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                    String response=httpGet("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/friend/apply/get?toid="+preferences.getString("id",""));
                    Gson gson=new Gson();
                    Type type=new TypeToken<List<FriendApply>>(){}.getType();
                    List<FriendApply> friendApplyList=gson.fromJson(response,type);
                    if(friendApplyList.size()>0){
                       //懒，只写第一个
                        Bundle bundle=new Bundle();
                        bundle.putString("fromid",friendApplyList.get(0).getFromid());
                        Message message=Message.obtain();
                        message.setData(bundle);
                        applyHandler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //下载apk
    public void downloadApk(String apkUrl,Context context) throws PackageManager.NameNotFoundException {
        //删除原有的APK
        IOUtils.clearApk(context,"YZN.apk");
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(apkUrl));
        File file=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"YZN.apk");
        String filePath=file.getAbsolutePath();
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("apkPath",filePath);
        editor.apply();
        request.setDestinationUri(Uri.fromFile(file));
        //在通知栏中显示
        request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("正在下载: YZN");
        request.setVisibleInDownloadsUi(true);
        long downLoadID=downloadManager.enqueue(request);
    }



}