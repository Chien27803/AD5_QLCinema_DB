package com.example.ad5;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

public class CloudinaryUploader {

    // Upload ảnh từ Uri bằng InputStream (hoạt động tốt trên Android mới)
    public static String uploadImage(Context context, Uri imageUri) {
        InputStream inputStream = null;
        try {
            Cloudinary cloudinary = CloudinaryHelper.getCloudinary();

            // Mở stream từ Uri
            inputStream = context.getContentResolver().openInputStream(imageUri);

            if (inputStream == null) return null;

            Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());

            // Lấy link trả về
            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();   // xem logcat để debug nếu cần
            return null;
        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (Exception ignored) {}
            }
        }
    }
}
