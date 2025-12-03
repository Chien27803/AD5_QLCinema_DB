package com.example.ad5;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    // Tên file SharedPreferences
    private static final String PREF_NAME = "UserSession";
    // Key để lưu ID người dùng
    private static final String KEY_USER_ID = "USER_ID_KEY";
    // Giá trị mặc định khi không tìm thấy ID (Unregistered/Guest)
    private static final int DEFAULT_USER_ID = -1;

    /**
     * LƯU ID người dùng vào SharedPreferences sau khi đăng nhập thành công.
     */
    public static void saveLoggedInUserId(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, userId);
        editor.apply(); // Lưu bất đồng bộ
    }

    /**
     * TRUY XUẤT ID người dùng từ SharedPreferences.
     * * @param context Context của ứng dụng/Activity
     * @return userId đã lưu, hoặc -1 nếu không có ID nào được lưu.
     */
    public static int getLoggedInUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Lấy giá trị từ KEY_USER_ID, nếu không có sẽ trả về DEFAULT_USER_ID (-1)
        return sharedPreferences.getInt(KEY_USER_ID, DEFAULT_USER_ID);
    }

    /**
     * XÓA ID người dùng khỏi SharedPreferences (Đăng xuất).
     */
    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(KEY_USER_ID).apply();
    }
}