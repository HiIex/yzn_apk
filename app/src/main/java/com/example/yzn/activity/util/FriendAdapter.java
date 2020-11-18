package com.example.yzn.activity.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yzn.R;
import com.example.yzn.activity.ui.CircleImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{

    private List<FriendJson> friendList;
    private List<Bitmap> headList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView textView_nickname;
        TextView textView_remark;
        TextView textView_time;

        public ViewHolder(View view){
            super(view);
            circleImageView=(CircleImageView)view.findViewById(R.id.circleView);
            textView_nickname=(TextView)view.findViewById(R.id.textView_nickname);
            textView_remark=(TextView)view.findViewById(R.id.textView_remark);
            textView_time=(TextView)view.findViewById(R.id.textView_time);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                //请求头像结束
                case 1:

                    break;

            }
        }
    };

    public FriendAdapter(List<FriendJson> friendList,List<Bitmap> headList){
        this.friendList=friendList;
        this.headList=headList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend,parent, false);
        final FriendAdapter.ViewHolder holder=new FriendAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(friendList.size()>0&&headList.size()>0){
            final FriendJson friend=friendList.get(position);
            holder.textView_time.setText(friend.getTime());
            holder.textView_remark.setText(friend.getRemark());
            holder.textView_nickname.setText(friend.getNickname());
            if(headList.get(position)!=null){
                holder.circleImageView.setImageBitmap(headList.get(position));
            }
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

    @Override
    public int getItemCount() {
        return friendList.size();
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
}
