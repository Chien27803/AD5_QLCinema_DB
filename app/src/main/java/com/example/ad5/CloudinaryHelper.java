package com.example.ad5;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;



public class CloudinaryHelper {
    private static Cloudinary cloudinary;

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", BuildConfig.CLOUD_NAME,
                    "api_key", BuildConfig.API_KEY,
                    "api_secret", BuildConfig.API_SECRET
            ));
        }
        return cloudinary;
    }
}
