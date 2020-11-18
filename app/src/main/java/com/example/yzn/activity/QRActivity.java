package com.example.yzn.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yzn.R;
import com.example.yzn.activity.ui.CircleImageView;
import com.example.yzn.activity.util.AccountInfo;
import com.example.yzn.activity.util.QRCodeUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class QRActivity extends AppCompatActivity {

    private Bitmap bitmap_photo=null;
    //缓存路径
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";
    //个人信息json
    private String json_accountInfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r);

        initView();
    }

    @SuppressLint("HandlerLeak")
    Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //完成头像加载
                case 1:
                    CircleImageView circleImageView=(CircleImageView)findViewById(R.id.circleView);
                    if(bitmap_photo!=null){
                        circleImageView.setImageBitmap(bitmap_photo);
                    }
                    break;

                    //完成个人信息加载
                case 2:
                    ImageView imageView_QR=(ImageView)findViewById(R.id.imageView_QR);
                    if(bitmap_photo!=null){
                        imageView_QR.setImageBitmap(QRCodeUtil.createQRCodeWithLogo(json_accountInfo,bitmap_photo));
                        imageView_QR.setVisibility(View.VISIBLE);
                    }else{
                        imageView_QR.setImageBitmap(QRCodeUtil.createQRCode(json_accountInfo));
                        imageView_QR.setVisibility(View.VISIBLE);
                    }
                    break;

                    //保存二维码图片到本地失败
                case -1:
                    Toast.makeText(QRActivity.this,"保存二维码失败！",Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    Toast.makeText(QRActivity.this,"保存二维码成功！",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    private void initView(){
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(QRActivity.this);

        //加载头像和个人信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    bitmap_photo=getBitmapFromLocal("headPicture.jpg");
                }catch (Exception e){
                    e.printStackTrace();
                }
                threadHandler.sendEmptyMessage(1);

                String id=preferences.getString("id","");
                boolean sex=preferences.getBoolean("sex",true);
                String nickname=preferences.getString("nickname","");
                String province=preferences.getString("province","");
                String city=preferences.getString("city","");
                String birthday=String.valueOf(preferences.getInt("year",2000))+"-"+String.valueOf(preferences.getInt("month",1))+"-"+String.valueOf(preferences.getInt("day",1));
                String company=preferences.getString("company","");
                String position=preferences.getString("job","");
                AccountInfo accountInfo=new AccountInfo(id,sex,nickname,province,city,birthday,company,position);
                Gson gson=new Gson();
                json_accountInfo=gson.toJson(accountInfo);
                threadHandler.sendEmptyMessage(2);

            }
        }).start();

        //昵称
        TextView textView_nickname=(TextView)findViewById(R.id.textView_nickname);
        textView_nickname.setText(preferences.getString("nickname",""));

        //省份
        TextView textView_province=(TextView)findViewById(R.id.textView_province);
        textView_province.setText(preferences.getString("province",""));

        //城市
        TextView textView_city=(TextView)findViewById(R.id.textView_city);
        textView_city.setText(preferences.getString("city",""));

        //性别
        ImageView imageView_boy=(ImageView)findViewById(R.id.imageView_male);
        ImageView imageView_girl=(ImageView)findViewById(R.id.imageView_female);
        if(preferences.getBoolean("sex",true)){
            imageView_boy.setVisibility(View.VISIBLE);
            imageView_girl.setVisibility(View.INVISIBLE);
        }else{
            imageView_boy.setVisibility(View.INVISIBLE);
            imageView_girl.setVisibility(View.VISIBLE);
        }

        //分享至qq
        ImageButton imageButton_qq=(ImageButton)findViewById(R.id.imageButton_qq);
        imageButton_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
            }
        });

        //分享至微信
        ImageButton imageButton_wechat=(ImageButton)findViewById(R.id.imageButton_wechat);
        imageButton_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
            }
        });

        //保存二维码到本地
        ImageButton imageButton_download=(ImageButton)findViewById(R.id.imageButton_download);
        imageButton_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(bitmap_photo!=null){
                                ContentResolver cr = QRActivity.this.getContentResolver();
                                insertImage(cr, QRCodeUtil.createQRCodeWithLogo(json_accountInfo,bitmap_photo), "myQRImage", "","myQRImage.jpg");
                            }else{
                                ContentResolver cr = QRActivity.this.getContentResolver();
                                insertImage(cr, QRCodeUtil.createQRCode(json_accountInfo), "myQRImage", "","myQRImage.jpg");
                            }
                            threadHandler.sendEmptyMessage(3);
                        }catch (Exception e){
                            e.printStackTrace();
                            threadHandler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            }
        });

        //返回
        ImageButton imageButton_back=(ImageButton)findViewById(R.id.title_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(QRActivity.this,AfterLoginActivity.class);
                intent.putExtra("viewState",4);
                startActivity(intent);
            }
        });


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

    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    /**
     * Insert an image and create a thumbnail for it.
     *
     * @param cr          The content resolver to use
     * @param source      The stream to use for the image
     * @param title       The name of the image
     * @param description The description of the image
     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
     * for any reason.
     */
    public static String insertImage(ContentResolver cr, Bitmap source,
                                     String title, String description, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());

        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.LATITUDE,36);
        values.put(MediaStore.Images.Media.LONGITUDE, 120);
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "6666");


        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 36, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                Bitmap microThumb = StoreThumbnail(cr, miniThumb, id, 50F, 50F,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                //JLogUtils.i("Alex", "Failed to create thumbnail, removing original");
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            Log.i("Alex", "Failed to insert image", e);
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private static final Bitmap StoreThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width, float height,
            int kind) {
        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true);

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);

            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


}