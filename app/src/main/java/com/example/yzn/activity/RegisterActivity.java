package com.example.yzn.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.yzn.R;
import com.example.yzn.activity.ui.ImageTextButton;
import com.example.yzn.activity.ui.VerificationCodeView;
import com.example.yzn.activity.util.Info;
import com.example.yzn.activity.util.LoginResult;
import com.example.yzn.activity.util.MD5;
import com.example.yzn.activity.util.PhoneRequest;
import com.example.yzn.activity.util.PhoneResponse;
import com.example.yzn.activity.util.RegisterResult;
import com.example.yzn.activity.util.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity implements VerificationCodeView.OnCodeFinishListener{

    private boolean isAgreed=true;
    private boolean hidePassword1=true;
    private boolean hidePassword2=true;

    public final static int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    private Bitmap bitmap_photo=null;
    //Mob API key
    public static final String APPKey="m30eefbd13356e";
    public static final String APPSecret="5c95b6fcc6ead83a040a08307f786236";
    private String phone="";

    private int registerState=0;//为2时表示完成注册

    @SuppressLint("HandlerLeak")
    Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //注册成功
                case 1:
                    if(++registerState==2){
                        Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    break;

                    //注册失败
                case -1:
                    Toast.makeText(RegisterActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //加密失败
                case -2:
                    Toast.makeText(RegisterActivity.this,"RSA加密失败！",Toast.LENGTH_SHORT).show();
                    break;

                //解密失败
                case -3:
                    Toast.makeText(RegisterActivity.this,"RSA解密失败！",Toast.LENGTH_SHORT).show();
                    break;

                    //上传信息失败
                case -4:
                    Toast.makeText(RegisterActivity.this,"个人信息上传失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //初始化ModSDK
        MobSDK.init(this,APPKey,APPSecret);

        //短信验证码
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        initView();
    }

    public void initView(){
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //设置验证码输入完成监听
        VerificationCodeView verificationCodeView = findViewById(R.id.verificationcodeview);
        verificationCodeView.setOnCodeFinishListener(RegisterActivity.this);

        //隐私政策
        final ImageButton imageButton_privacy=(ImageButton)findViewById(R.id.button_done);
        imageButton_privacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(isAgreed){
                    imageButton_privacy.setBackgroundResource(R.drawable.done_gray);
                    isAgreed=false;
                }else{
                    imageButton_privacy.setBackgroundResource(R.drawable.done);
                    isAgreed=true;
                }
            }
        });

        //隐藏密码
        final EditText editText_password=(EditText)findViewById(R.id.editText_password);
        editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        EditText editText_repeat=(EditText)findViewById(R.id.editText_repeat);
        editText_repeat.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //第一步
        Button button_next=(Button)findViewById(R.id.button_next1);
        button_next.setEnabled(false);

        //号码前缀
        String[] mItems_number = getResources().getStringArray(R.array.number);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_number = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_number);
        adapter_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        Spinner spinner_number=(Spinner)findViewById(R.id.spinner_number);
        spinner_number.setAdapter(adapter_number);
        spinner_number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] number = getResources().getStringArray(R.array.number);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //获取验证码
        final Button button_check=(Button)findViewById(R.id.button_check);
        button_check.setEnabled(true);
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText_number=(EditText)findViewById(R.id.editText_number);
                phone=editText_number.getText().toString();
                if(!judgePhoneNums(phone)){
                    //判断手机号格式
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号！",Toast.LENGTH_SHORT).show();
                    return;
                }
                button_check.setEnabled(false);
                //handler.sendEmptyMessage(0);

                new CountDownTimer(90*1000,1000) {
                    @Override
                    public void onTick(long l) {
                        if(!button_check.isEnabled()){
                            handler.sendEmptyMessage(-9);
                            button_check.setText("重新发送("+l/1000+"s)");
                        }
                    }

                    @Override
                    public void onFinish() {
                        button_check.setText(R.string.getCheckCode);
                        button_check.setEnabled(true);
                        handler.sendEmptyMessage(-8);
                    }
                }.start();
                SMSSDK.getVerificationCode("86",phone);

            }
        });

        //监听输入密码,显示密码强度
        //EditText editText_password=(EditText)findViewById(R.id.editText_password);
        final RelativeLayout relativeLayout_security1=(RelativeLayout)findViewById(R.id.layout_security1);
        final RelativeLayout relativeLayout_security2=(RelativeLayout)findViewById(R.id.layout_security2);
        final RelativeLayout relativeLayout_security3=(RelativeLayout)findViewById(R.id.layout_security3);
        relativeLayout_security1.setBackgroundResource(R.color.Gray);
        relativeLayout_security2.setBackgroundResource(R.color.Gray);
        relativeLayout_security3.setBackgroundResource(R.color.Gray);

        final TextView textView_secutrity_low=(TextView)findViewById(R.id.textView_security_low);
        final TextView textView_secutrity_mid=(TextView)findViewById(R.id.textView_security_mid);
        final TextView textView_secutrity_high=(TextView)findViewById(R.id.textView_security_high);
        textView_secutrity_low.setVisibility(View.INVISIBLE);
        textView_secutrity_mid.setVisibility(View.INVISIBLE);
        textView_secutrity_high.setVisibility(View.INVISIBLE);

        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password=editText_password.getText().toString();
                switch(checkPasswordSecurity(password)){
                    case 1:
                        relativeLayout_security1.setBackgroundResource(R.color.SecurityLow);
                        relativeLayout_security2.setBackgroundResource(R.color.Gray);
                        relativeLayout_security3.setBackgroundResource(R.color.Gray);
                        textView_secutrity_low.setVisibility(View.VISIBLE);
                        textView_secutrity_mid.setVisibility(View.INVISIBLE);
                        textView_secutrity_high.setVisibility(View.INVISIBLE);

                        break;
                    case 2:
                        relativeLayout_security1.setBackgroundResource(R.color.SecurityLow);
                        relativeLayout_security2.setBackgroundResource(R.color.SecurityMid);
                        relativeLayout_security3.setBackgroundResource(R.color.Gray);
                        textView_secutrity_low.setVisibility(View.INVISIBLE);
                        textView_secutrity_mid.setVisibility(View.VISIBLE);
                        textView_secutrity_high.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        relativeLayout_security1.setBackgroundResource(R.color.SecurityLow);
                        relativeLayout_security2.setBackgroundResource(R.color.SecurityMid);
                        relativeLayout_security3.setBackgroundResource(R.color.SecurityHigh);
                        textView_secutrity_low.setVisibility(View.INVISIBLE);
                        textView_secutrity_mid.setVisibility(View.INVISIBLE);
                        textView_secutrity_high.setVisibility(View.VISIBLE);

                        break;
                    default:
                        relativeLayout_security1.setBackgroundResource(R.color.Gray);
                        relativeLayout_security2.setBackgroundResource(R.color.Gray);
                        relativeLayout_security3.setBackgroundResource(R.color.Gray);
                        textView_secutrity_low.setVisibility(View.INVISIBLE);
                        textView_secutrity_mid.setVisibility(View.INVISIBLE);
                        textView_secutrity_high.setVisibility(View.INVISIBLE);
                        break;

                }
            }
        };
        editText_password.addTextChangedListener(textWatcher);

        //选择身份
        ImageTextButton imageTextButton_individual=(ImageTextButton)findViewById(R.id.button_individual);
        imageTextButton_individual.setImageResource(R.drawable.individual);
        imageTextButton_individual.setText("个人");
        imageTextButton_individual.setTextSize(13);

        ImageTextButton imageTextButton_enterprise=(ImageTextButton)findViewById(R.id.button_enterprise);
        imageTextButton_enterprise.setImageResource(R.drawable.enterprise);
        imageTextButton_enterprise.setText("企业");
        imageTextButton_enterprise.setTextSize(13);

        ImageTextButton imageTextButton_organization=(ImageTextButton)findViewById(R.id.button_organization);
        imageTextButton_organization.setImageResource(R.drawable.organization);
        imageTextButton_organization.setText("其他组织");
        imageTextButton_organization.setTextSize(13);

        ImageTextButton imageTextButton_middle=(ImageTextButton)findViewById(R.id.button_middle);
        imageTextButton_middle.setImageResource(R.drawable.middle);
        imageTextButton_middle.setText("中间人");
        imageTextButton_middle.setTextSize(13);

        //隐藏取消按钮
        final ImageView imageView_photo=(ImageView)findViewById(R.id.imageView_photo);
        final ImageView imageView_camera=(ImageView)findViewById(R.id.button_camera);
        final ImageButton imageButton_cancel=(ImageButton)findViewById(R.id.button_cancel);
        imageButton_cancel.setVisibility(View.GONE);
        imageButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_photo.setVisibility(View.GONE);
                imageView_camera.setVisibility(View.VISIBLE);
                imageButton_cancel.setVisibility(View.GONE);
            }
        });

        //第二步
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(false);
        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        final EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        imageView_enterprise_name.setVisibility(View.GONE);
        textView_name.setVisibility(View.GONE);
        editText_name.setVisibility(View.GONE);
        textView_upload.setVisibility(View.GONE);
        imageView_picture.setVisibility(View.GONE);
        relativeLayout_camera.setVisibility(View.GONE);

        final RelativeLayout relativeLayout1=(RelativeLayout)findViewById(R.id.layout1);
        final RelativeLayout relativeLayout2=(RelativeLayout)findViewById(R.id.layout2);
        final RelativeLayout relativeLayout3=(RelativeLayout)findViewById(R.id.layout3);
        relativeLayout1.setVisibility(View.VISIBLE);
        relativeLayout2.setVisibility(View.GONE);
        relativeLayout3.setVisibility(View.GONE);

        Button button_next1=(Button)findViewById(R.id.button_next1);
        button_next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText_number=(EditText)findViewById(R.id.editText_number);
                String number=editText_number.getText().toString();
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("number",number);
                editor.apply();
                relativeLayout1.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.VISIBLE);
            }
        });
        //Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                relativeLayout2.setVisibility(View.GONE);
                relativeLayout3.setVisibility(View.VISIBLE);
            }
        });
        Button button_complete=(Button)findViewById(R.id.button_complete);
        button_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检擦两次密码是否一次
                EditText editText_password=(EditText)findViewById(R.id.editText_password);
                EditText editText_repeat=(EditText)findViewById(R.id.editText_repeat);
                final String password=editText_password.getText().toString();
                final String repeat=editText_repeat.getText().toString();
                if(!password.equals(repeat)){
                    Toast.makeText(RegisterActivity.this,"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText editText_nickname=(EditText)findViewById(R.id.editText_nickname);
                final String nickname=editText_nickname.getText().toString();

                final String salt=getRandomString(10);
                String id=null;
                try {
                    id=MD5.getMD5(phone+salt);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                final String finalId = id;
                final int[] state = {LoginResult.FAIL};
                //注册线程
                Thread httpThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //用serverpk加密注册信息
                        User user=null;
                        try{
                            String hash=applySha256(phone+password+salt);
                            //String cypher= byteToString(RSAUtil.encrypt(Config.SERVER_PUBLIC_KEY,hash.getBytes()));
                            user=new User(finalId,phone,salt,hash,nickname);
                        }catch (Exception e){
                            e.printStackTrace();
                            threadHandler.sendEmptyMessage(-2);
                        }
                        Gson gson=new Gson();
                        String json=gson.toJson(user);
                        String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/register",json);
                        Type type=new TypeToken<RegisterResult>(){}.getType();
                        RegisterResult registerResult=gson.fromJson(response,type);
                        state[0] =registerResult.getState();
                        if(state[0]==RegisterResult.SUCCESS){
                            /*
                            try{
                                //用serversk解密得到clientpk
                                String clientpk=RSACipher.decrypt(Config.SERVER_PRIVATE_KEY,registerResult.getClientpk());
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("clientpk",clientpk);
                                editor.apply();
                            }catch (Exception e){
                                e.printStackTrace();
                                threadHandler.sendEmptyMessage(-3);
                            }

                             */
                            threadHandler.sendEmptyMessage(1);
                        }else{
                            threadHandler.sendEmptyMessage(-1);
                        }
                    }
                });
                httpThread.start();

                //上传个人信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putBoolean("isRegister",true);
                        editor.putString("password",password);
                        editor.putString("nickname",nickname);
                        editor.putString("salt",salt);
                        editor.putString("id", finalId);
                        editor.apply();

                        Gson gson=new Gson();
                        Info info=new Info(finalId,phone,nickname,0,null,null,null,null,null);
                        String json=gson.toJson(info);
                        String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/message/info/post",json);
                        if(!response.equals("info post complete")){
                            threadHandler.sendEmptyMessage(-4);
                        }else{
                            threadHandler.sendEmptyMessage(1);
                        }
                    }
                }).start();

            }
        });


    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                //倒计时中
            } else if (msg.what == -8) {
                //倒计时结束
                //requestCodeButton.setText("获取验证码");

                //i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);

                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 提交验证码成功,判断手机号是否已注册
                        if(!judgePhoneExit(phone)){
                            Button button_next=(Button)findViewById(R.id.button_next1);
                            button_next.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Button button_check=(Button)findViewById(R.id.button_check);
                            button_check.setEnabled(true);
                            button_check.setText("获取验证码");
                            Toast.makeText(getApplicationContext(), "该手机号已注册", Toast.LENGTH_SHORT).show();
                        }


                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(RegisterActivity.this, "验证码已发送,请注意查收", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(RegisterActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"验证码不正确！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    public void onTextChange(View view, String content) {

    }

    //验证码输入完成监听
    @Override
    public void onComplete(View view, String content) {
        //String checkCode=String.valueOf(verificationCodeView);
        //Toast.makeText(RegisterActivity.this,content,Toast.LENGTH_SHORT).show();
        SMSSDK.submitVerificationCode("86",phone,content);
    }

    public void onClickHide(View view){
        EditText editText_password=(EditText)findViewById(R.id.editText_password);
        ImageButton imageButton_hide1=(ImageButton)findViewById(R.id.button_hide1);
        if(hidePassword1){
            editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageButton_hide1.setBackgroundResource(R.drawable.hide_gray);
            hidePassword1=false;
        }else{
            editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageButton_hide1.setBackgroundResource(R.drawable.hide_blue);
            hidePassword1=true;
        }
    }

    public void onClickHide2(View view){
        EditText editText_repeat=(EditText)findViewById(R.id.editText_repeat);
        ImageButton imageButton_hide2=(ImageButton)findViewById(R.id.button_hide2);
        if(hidePassword2){
            editText_repeat.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageButton_hide2.setBackgroundResource(R.drawable.hide_gray);
            hidePassword2=false;
        }else{
            editText_repeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageButton_hide2.setBackgroundResource(R.drawable.hide_blue);
            hidePassword2=true;
        }
    }

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /*  一、假定密码字符数范围6-16，除英文数字和字母外的字符都视为特殊字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*){2,}$
    二、假定密码字符数范围6-16，密码字符允许范围为ASCII码表字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[\x00-\x2f\x3A-\x40\x5B-\xFF][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[\x00-\x2F\x3A-\x40\x5B-\xFF][0-9A-Za-z]*){2,}$*/
    public int checkPasswordSecurity(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return 1;
        }
        if (passwordStr.matches(regexS)) {
            return 1;
        }
        if (passwordStr.matches(regexT)) {
            return 1;
        }
        if (passwordStr.matches(regexZT)) {
            return 2;
        }
        if (passwordStr.matches(regexST)) {
            return 2;
        }
        if (passwordStr.matches(regexZS)) {
            return 2;
        }
        if (passwordStr.matches(regexZST)) {
            return 3;
        }
        return 0;

    }

    public void onClickNext2(View view){
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
    }

    public void onClickUpload(View view){
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        imageView_enterprise_name.setVisibility(View.VISIBLE);
        textView_name.setVisibility(View.VISIBLE);
        editText_name.setVisibility(View.VISIBLE);
        textView_upload.setVisibility(View.VISIBLE);
        imageView_picture.setVisibility(View.VISIBLE);
        relativeLayout_camera.setVisibility(View.VISIBLE);
    }

    public void onClickCamera(View view){
        setDialog();
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
                if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CHOOSE_PHOTO);
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

                if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},TAKE_PHOTO);
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
                        imageUri= FileProvider.getUriForFile(RegisterActivity.this,"com.example.yzn.fileprovider",outputImage);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将照片显示出来
                        bitmap_photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ImageView imageView_photo=(ImageView)findViewById(R.id.imageView_photo);
                        ImageView imageView_camera=(ImageView) findViewById(R.id.button_camera);
                        imageView_camera.setVisibility(View.GONE);
                        imageView_photo.setVisibility(View.VISIBLE);
                        imageView_photo.setImageBitmap(bitmap_photo);

                        //显示取消按钮
                        ImageButton imageButton_cancel=(ImageButton)findViewById(R.id.button_cancel);
                        imageButton_cancel.setVisibility(View.VISIBLE);
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
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //打开相册
    private  void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CHOOSE_PHOTO:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(RegisterActivity.this,"you denied the permission!", Toast.LENGTH_SHORT).show();
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
                        imageUri= FileProvider.getUriForFile(RegisterActivity.this,"com.example.yzn.fileprovider",outputImage);
                    }else {
                        imageUri=Uri.fromFile(outputImage);
                    }
                    //启动相机
                    Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                }else{
                    Toast.makeText(RegisterActivity.this,"you denied the permission!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
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
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            ImageView imageView_photo=(ImageView)findViewById(R.id.imageView_photo);
            imageView_photo.setImageBitmap(bitmap);
            imageView_photo.setVisibility(View.VISIBLE);
            //显示取消按钮
            ImageView imageView_camera=(ImageView)findViewById(R.id.button_camera);
            imageView_camera.setVisibility(View.GONE);
            ImageButton imageButton_cancel=(ImageButton) findViewById(R.id.button_cancel);
            imageButton_cancel.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this,"failed to get the image",Toast.LENGTH_SHORT).show();
        }
    }

    //随机产生盐值
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private String initID(String phone) throws NoSuchAlgorithmException {
        String salt=getRandomString(10);
        String id= MD5.getMD5(phone+salt);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("id",id);
        editor.apply();
        return id;
    }

    public void onClickIndividual(View view){
        ImageTextButton imageTextButton_individual=(ImageTextButton)findViewById(R.id.button_individual);
        ImageTextButton imageTextButton_enterprise=(ImageTextButton)findViewById(R.id.button_enterprise);
        ImageTextButton imageTextButton_organization=(ImageTextButton)findViewById(R.id.button_organization);
        ImageTextButton imageTextButton_middle=(ImageTextButton)findViewById(R.id.button_middle);
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
        imageTextButton_individual.setAlpha(1);
        imageTextButton_enterprise.setAlpha(0.3f);
        imageTextButton_organization.setAlpha(0.3f);
        imageTextButton_middle.setAlpha(0.3f);

        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        if(editText_name.getVisibility()==View.VISIBLE){
            imageView_enterprise_name.setVisibility(View.GONE);
            textView_name.setVisibility(View.GONE);
            editText_name.setVisibility(View.GONE);
            textView_upload.setVisibility(View.GONE);
            imageView_picture.setVisibility(View.GONE);
            relativeLayout_camera.setVisibility(View.GONE);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preference.edit();
                editor.putBoolean("individual",true);
                editor.apply();
            }
        }).start();
    }

    public void onClickEnterprise(View view){
        ImageTextButton imageTextButton_individual=(ImageTextButton)findViewById(R.id.button_individual);
        ImageTextButton imageTextButton_enterprise=(ImageTextButton)findViewById(R.id.button_enterprise);
        ImageTextButton imageTextButton_organization=(ImageTextButton)findViewById(R.id.button_organization);
        ImageTextButton imageTextButton_middle=(ImageTextButton)findViewById(R.id.button_middle);
        imageTextButton_individual.setAlpha(0.3f);
        imageTextButton_enterprise.setAlpha(1);
        imageTextButton_organization.setAlpha(0.3f);
        imageTextButton_middle.setAlpha(0.3f);
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        imageView_enterprise_name.setVisibility(View.VISIBLE);
        textView_name.setVisibility(View.VISIBLE);
        editText_name.setVisibility(View.VISIBLE);
        textView_upload.setVisibility(View.VISIBLE);
        imageView_picture.setVisibility(View.VISIBLE);
        relativeLayout_camera.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preference.edit();
                editor.putBoolean("enterprise",true);
                editor.apply();
            }
        }).start();

    }

    public void onClickOrganization(View view){
        ImageTextButton imageTextButton_individual=(ImageTextButton)findViewById(R.id.button_individual);
        ImageTextButton imageTextButton_enterprise=(ImageTextButton)findViewById(R.id.button_enterprise);
        ImageTextButton imageTextButton_organization=(ImageTextButton)findViewById(R.id.button_organization);
        ImageTextButton imageTextButton_middle=(ImageTextButton)findViewById(R.id.button_middle);
        imageTextButton_individual.setAlpha(0.3f);
        imageTextButton_enterprise.setAlpha(0.3f);
        imageTextButton_organization.setAlpha(1);
        imageTextButton_middle.setAlpha(0.3f);
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        imageView_enterprise_name.setVisibility(View.VISIBLE);
        textView_name.setVisibility(View.VISIBLE);
        editText_name.setVisibility(View.VISIBLE);
        textView_upload.setVisibility(View.VISIBLE);
        imageView_picture.setVisibility(View.VISIBLE);
        relativeLayout_camera.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preference.edit();
                editor.putBoolean("organization",true);
                editor.apply();
            }
        }).start();
    }

    public void onClickMiddle(View view){
        ImageTextButton imageTextButton_individual=(ImageTextButton)findViewById(R.id.button_individual);
        ImageTextButton imageTextButton_enterprise=(ImageTextButton)findViewById(R.id.button_enterprise);
        ImageTextButton imageTextButton_organization=(ImageTextButton)findViewById(R.id.button_organization);
        ImageTextButton imageTextButton_middle=(ImageTextButton)findViewById(R.id.button_middle);
        Button button_next2=(Button)findViewById(R.id.button_next2);
        button_next2.setEnabled(true);
        imageTextButton_individual.setAlpha(0.3f);
        imageTextButton_enterprise.setAlpha(0.3f);
        imageTextButton_organization.setAlpha(0.3f);
        imageTextButton_middle.setAlpha(1);
        ImageView imageView_enterprise_name=(ImageView)findViewById(R.id.imageView_enterprise_name);
        TextView textView_name=(TextView)findViewById(R.id.textView_enterprise_name);
        EditText editText_name=(EditText)findViewById(R.id.editText_enterprise_name);
        TextView textView_upload=(TextView)findViewById(R.id.textView_upload);
        ImageView imageView_picture=(ImageView)findViewById(R.id.imageView_picture);
        RelativeLayout relativeLayout_camera=(RelativeLayout)findViewById(R.id.layout_camera);
        if(editText_name.getVisibility()==View.VISIBLE){
            imageView_enterprise_name.setVisibility(View.GONE);
            textView_name.setVisibility(View.GONE);
            editText_name.setVisibility(View.GONE);
            textView_upload.setVisibility(View.GONE);
            imageView_picture.setVisibility(View.GONE);
            relativeLayout_camera.setVisibility(View.GONE);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preference.edit();
                editor.putBoolean("middle",true);
                editor.apply();
            }
        }).start();

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

    public boolean judgePhoneExit(final String phone){
        final int[] state = {PhoneResponse.EXIST};
        Thread httpThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson=new Gson();
                PhoneRequest phoneRequest=null;
                try{
                    phoneRequest=new PhoneRequest(phone);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(phoneRequest==null){
                    threadHandler.sendEmptyMessage(-2);
                }else{
                    String json=gson.toJson(phoneRequest);
                    String response=httpPost("http://"+MainActivity.IP+":"+MainActivity.PORT+"/sys/user/phone",json);
                    Type type=new TypeToken<PhoneResponse>(){}.getType();
                    PhoneResponse phoneResponse=gson.fromJson(response,type);
                    state[0] =phoneResponse.getState();
                }
            }
        });
        httpThread.start();

        try{
            httpThread.join();
        }catch (Exception e){
            e.printStackTrace();
        }

        return state[0] != PhoneResponse.PERMIT;
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