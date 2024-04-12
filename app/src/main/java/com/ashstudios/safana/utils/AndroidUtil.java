package com.ashstudios.safana.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashstudios.safana.models.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class AndroidUtil {

   public static  void showToast(Context context,String message){
       Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }


    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
