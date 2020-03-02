package com.example.a2learn.com.exmaple.a2learn.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public final class CompressImageHelper {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static byte[] compressImage(Context context, Uri uri){
        Bitmap bitmap = null;
        ImageDecoder.Source source = ImageDecoder.createSource(Objects.requireNonNull(context).getContentResolver(), uri);
        try {
            bitmap = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }
}
