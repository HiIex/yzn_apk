package com.example.yzn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yzn.R;
import com.example.yzn.activity.util.Account;
import com.example.yzn.activity.util.AccountAdapter;
import com.example.yzn.activity.util.DividerItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class MySettingActivty extends AppCompatActivity {

    private RecyclerView recyclerView_account;
    private RecyclerView.LayoutManager layoutManager_account;
    private AccountAdapter accountAdapter;
    private ArrayList<Account> accountArrayList;

    //缓存路径
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";
    public Bitmap bitmap_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting_activty);

        initView();
    }

    public void initView(){
        Thread initThread=new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap_photo=getBitmapFromLocal("headPicture.jpg");
                initAccount();
            }
        });
        initThread.start();

        ImageButton imageButton_back=(ImageButton)findViewById(R.id.title_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MySettingActivty.this,AfterLoginActivity.class);
                startActivity(intent);
            }
        });

        // 建立数据源
        String[] mItems_storage = getResources().getStringArray(R.array.storage);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_storage = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems_storage);
        adapter_storage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        Spinner spinner_storage=(Spinner)findViewById(R.id.spinner_storage);
        spinner_storage.setAdapter(adapter_storage);
        spinner_storage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] storage = getResources().getStringArray(R.array.storage);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        recyclerView_account=(RecyclerView)findViewById(R.id.recycleView_account);
        layoutManager_account=new LinearLayoutManager(MySettingActivty.this);
        recyclerView_account.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView_account.setLayoutManager(layoutManager_account);
        if(initThread.isAlive()){
            try{
                initThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        accountAdapter=new AccountAdapter(accountArrayList);
        recyclerView_account.setAdapter(accountAdapter);
    }


    public void onClickMessage(View view){
        RelativeLayout relativeLayout_message_inside=(RelativeLayout)findViewById(R.id.layout_message_inside);
        ImageView imageView=(ImageView)findViewById(R.id.imageView_direction1);
        if(relativeLayout_message_inside.getVisibility()!=View.VISIBLE){
            relativeLayout_message_inside.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.drawable.up);
        }else{
            relativeLayout_message_inside.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.down);
        }
    }

    public void onClickNetwork(View view){
        RelativeLayout relativeLayout_network_inside=(RelativeLayout)findViewById(R.id.layout_network_inside);
        ImageView imageView=(ImageView)findViewById(R.id.imageView_direction2);
        if(relativeLayout_network_inside.getVisibility()!=View.VISIBLE){
            relativeLayout_network_inside.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.drawable.up);
        }else{
            relativeLayout_network_inside.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.down);
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

    public void initAccount(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Account account=new Account(bitmap_photo,preferences.getString("nickname",""),preferences.getString("number",""),true);
        accountArrayList=new ArrayList<>();
        accountArrayList.add(0,account);
    }

    public void onClickAccount(View view){
        RelativeLayout relativeLayout_account_inside=(RelativeLayout)findViewById(R.id.layout_account_inside);
        RelativeLayout relativeLayout_register=(RelativeLayout)findViewById(R.id.layout_register);
        RelativeLayout relativeLayout_exit=(RelativeLayout)findViewById(R.id.layout_exit);
        ImageView imageView=(ImageView)findViewById(R.id.imageView_direction0);
        if(relativeLayout_account_inside.getVisibility()!=View.VISIBLE){
            relativeLayout_account_inside.setVisibility(View.VISIBLE);
            relativeLayout_register.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.drawable.up);
            relativeLayout_exit.setVisibility(View.VISIBLE);
        }else{
            relativeLayout_account_inside.setVisibility(View.GONE);
            relativeLayout_register.setVisibility(View.GONE);
            relativeLayout_exit.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.down);
        }
    }

    public void onClickRegister(View view){
        Intent intent=new Intent(MySettingActivty.this,RegisterActivity.class);
        startActivity(intent);
    }

    public void onClickExit(View view){
        Intent intent=new Intent(MySettingActivty.this,MainActivity.class);
        startActivity(intent);
    }


}