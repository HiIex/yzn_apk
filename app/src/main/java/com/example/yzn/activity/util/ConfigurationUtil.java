package com.example.yzn.activity.util;

import android.os.Environment;

import java.io.File;

public class ConfigurationUtil {
    public static final int SUCCESSCODE = 1;//成功标识
    public static final int FAILURECODE = 0;//失败标识
    public static final int ERRORCODE = 2;//网络请求异常标识

    public static final String SDFILE = Environment.getExternalStoragePublicDirectory("") + File.separator + "hnlx/gt/";

    public static final String FILE_PATH = ConfigurationUtil.SDFILE + "camera" + File.separator;
    public static final String APK_PATH = "hnlx/gt/"+ "apk" + File.separator;
    public static final String APK_PATH_ABSOULT = ConfigurationUtil.SDFILE+ "apk" + File.separator;
    public static final String RECORD_PATH_ABSOULT = ConfigurationUtil.SDFILE+ "record" + File.separator;
}
