package com.example.yzn.activity.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GetBitmap extends Thread {

    private List<BillJson> billJsonArrayList;
    private int startPosition;
    private int endPosition;

    public GetBitmap(List<BillJson> billArrayList, int startPosition, int endPosition){
        this.billJsonArrayList=billArrayList;
        this.startPosition=startPosition;
        this.endPosition=endPosition;

    }

    @Override
    public void run() {
        int i=0;
        Bitmap bitmap=null;
        for(i=startPosition;i<endPosition;i++){
            BillJson billJson=billJsonArrayList.get(i);
            String base64Str=billJson.getBase64Str();
            if(base64Str!=null) {
                //压缩图片
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inPreferredConfig = Bitmap.Config.RGB_565;
                //options.inSampleSize = 5;
                bitmap = base64ToBitmap(base64Str);
                //AfterLoginActivity.bitmaps[i]=compressQualit3(bitmap);
            }else{
                //AfterLoginActivity.bitmaps[i]=null;
            }
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
}