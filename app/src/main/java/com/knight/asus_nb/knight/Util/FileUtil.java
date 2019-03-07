package com.knight.asus_nb.knight.Util;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileUtil {

    private static FileUtil insatace;
    private static String rootPath;   //根目录

    private FileUtil(){

    }
    public static FileUtil getInstance(String path){
        rootPath =  Environment.getExternalStorageDirectory().getPath() + path;
        Log.e("Path",rootPath);
        if (insatace == null){
            insatace = new FileUtil();
        }
        checkFile();
        return insatace;
    }

    public static String getRootPath() {
        return rootPath;
    }

    public  Uri createImg(Bitmap bitmap) throws IOException {
        File mFile = new File(rootPath + "/mximg/" + System.currentTimeMillis() + "img.png");                        //将要保存的图片文件
        if (mFile.exists()) {
            mFile.delete();
        }
        FileOutputStream outputStream = new FileOutputStream(mFile);     //构建输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);  //compress到输出outputStream
        Uri uri = Uri.fromFile(mFile);                                  //获得图片的uri
        outputStream.close();
        return uri;
    }

    private void createLib(){

    }

    private static void checkFile(){
        String filePath = rootPath + "/mximg";
        File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
    }
}
