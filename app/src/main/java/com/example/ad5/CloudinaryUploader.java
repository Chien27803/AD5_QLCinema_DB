package com.example.ad5;

import android.net.Uri;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class CloudinaryUploader {

    // Lấy đường dẫn thật từ Uri (quan trọng)
    private static String getRealPathFromURI(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) result = uri.getPath();
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // Hàm upload ảnh
    public static String uploadImage(Context context, Uri imageUri) {
        try {
            Cloudinary cloudinary = CloudinaryHelper.getCloudinary();

            String filePath = getRealPathFromURI(context, imageUri);
            File file = new File(filePath);

            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

            // Lấy link trả về
            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
