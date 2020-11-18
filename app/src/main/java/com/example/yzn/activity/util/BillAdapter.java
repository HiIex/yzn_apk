package com.example.yzn.activity.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yzn.R;
import com.example.yzn.activity.AfterLoginActivity;
import com.example.yzn.activity.MainActivity;
import com.example.yzn.activity.ui.ShineButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder>{

    private List<BillJson> billJsonList;
    private Object AfterLoginActivity;
    protected boolean isScrolling=false;
    //private Bitmap bitmaps[];


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_product;
        ImageView imageView_picture;
        ShineButton shineButton;
        ImageButton imageButton_client;
        ImageButton imageButton_share;
        Button button_accept;
        Spinner spinner;
        TextView textView_price;
        TextView textView_detail;

        public ViewHolder(View view){
            super(view);
            textView_product=(TextView)view.findViewById(R.id.textView_product);
            imageView_picture =(ImageView)view.findViewById(R.id.imageView_picture);
            shineButton=(ShineButton)view.findViewById(R.id.shineButton_special);
            imageButton_client=(ImageButton)view.findViewById(R.id.button_client);
            imageButton_share=(ImageButton)view.findViewById(R.id.button_share);
            button_accept=(Button)view.findViewById(R.id.button_accept);
            spinner=(Spinner)view.findViewById(R.id.spinner_money);
            textView_price=(TextView)view.findViewById(R.id.textView_price);
            textView_detail=(TextView)view.findViewById(R.id.textView_detail);
        }

    }

    public BillAdapter(List<BillJson> billList, AfterLoginActivity afterLoginActivity){
        this.billJsonList=billList;
        AfterLoginActivity=afterLoginActivity;
    }

    @SuppressLint("HandlerLeak")
    Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //请求原图结束
                case 2:

                    break;

            }
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bill,parent, false);
        final ViewHolder holder=new ViewHolder(view);

        holder.shineButton.init((Activity)AfterLoginActivity);
        holder.button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
            }
        });
        holder.imageButton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
            }
        });

        holder.imageButton_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
            }
        });


        holder.imageView_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int position=holder.getLayoutPosition();
                final OriginalImage[] originalImage = {null};
                final Thread httpThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson=new Gson();
                        Type type=new TypeToken<OriginalImage>(){}.getType();
                        String billID=billJsonList.get(position).getBillID();
                        String response=httpGet("http://"+ MainActivity.IP+":"+MainActivity.PORT+"/sys/user/image?billID="+billID);
                        originalImage[0] =gson.fromJson(response,type);
                    }
                });
                httpThread.start();

                try{
                    httpThread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    final Bitmap bitmap=base64ToBitmap(originalImage[0].getBase64Str());
                    final Dialog dialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    ImageView imageView=new ImageView(view.getContext());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
                    imageView.setImageBitmap(bitmap);
                    dialog.setContentView(imageView);
                    //再次点击返回，回收bitmap
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if(bitmap!=null&&!bitmap.isRecycled()){
                                bitmap.recycle();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });


        // 建立数据源
        String[] mItems_money = view.getContext().getResources().getStringArray(R.array.money);
        // 建立Adapter并且绑定数据源
        final ArrayAdapter<String> adapter_money = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, mItems_money);
        adapter_money.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        holder.spinner=(Spinner)view.findViewById(R.id.spinner_money);
        holder.spinner.setAdapter(adapter_money);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int position=holder.getLayoutPosition();
                double price=Double.parseDouble(billJsonList.get(position).getPrice());
                int from=billJsonList.get(position).getCurrency();
                if(pos!=from){
                    holder.textView_price.setText(new DecimalFormat("0.00").format(changeCurrency(price,from,pos)));
                }else{
                    holder.textView_price.setText(String.valueOf(price));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillJson billJson=billJsonList.get(position);
        holder.textView_product.setText(billJson.getProductName());
        //惯性时加载
        if(billJson.getBase64Str()!=null){
            String base64Str=billJson.getBase64Str();
            Bitmap bitmap=base64ToBitmap(base64Str);
            holder.imageView_picture.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 100, 150));
            holder.imageView_picture.setVisibility(View.VISIBLE);
        }
        holder.textView_price.setText(billJson.getPrice());
        holder.spinner.setSelection(billJson.getCurrency());
        holder.textView_detail.setText(billJson.getDetail());
    }

    @Override
    public int getItemCount() {
        return billJsonList.size();
    }

    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(view.getContext(),R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    //货币换算
    public double changeCurrency(double price,int from,int to){
        double temp,result;
        switch(from){
            case Bill.RMB:
                temp=price;
                break;
            case Bill.USD:
                temp=6.803862*price;
                break;
            case Bill.EUR:
                temp=7.978306*price;
                break;
            case Bill.GBP:
                temp=price*8.7374996;
                break;
            case Bill.JPY:
                temp=0.0646151*price;
                break;
            case Bill.HKD:
                temp=0.876879*price;
                break;
            default:
                temp=price;
                break;
        }
        switch(to){
            case Bill.RMB:
                result=temp;
                break;
            case Bill.USD:
                result=0.14714902*temp;
                break;
            case Bill.EUR:
                result=0.12533989*temp;
                break;
            case Bill.GBP:
                result=0.11445053*temp;
                break;
            case Bill.JPY:
                result=15.4762577*temp;
                break;
            case Bill.HKD:
                result=1.1402954*temp;
                break;
            default:
                result=temp;
                break;
        }
        return result;
    }

    //PNG
    private Bitmap compressQualit3(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,30,bos);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        byte[] bytes = bos.toByteArray();

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
    }

    //JPEG
    private Bitmap compressQualit2(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,10,bos);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        byte[] bytes = bos.toByteArray();

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
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

    public void setScrolling(Boolean isScrolling){
        this.isScrolling=isScrolling;
    }

    public void setBillJsonList(List<BillJson> billJsonList){
        this.billJsonList=billJsonList;
    }
}
