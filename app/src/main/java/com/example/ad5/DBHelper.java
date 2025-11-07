package com.example.ad5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cinema_db.db";
    private static final int DB_VERSION = 5;
    // Đảm bảo bạn có các hằng số này:
    private static final String TABLE_MOVIE = "Movie";
    private static final String KEY_MOVIE_ID = "movie_id";
    public static final String STATUS_DA_HUY = "Đã hủy"; // Hằn

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ===== BẢNG NGƯỜI DÙNG =====
        db.execSQL("CREATE TABLE Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL, " +
                "address TEXT, " +
                "role TEXT DEFAULT 'user', " + // vì SQLite không có ENUM
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "status INTEGER DEFAULT)");

        // ===== BẢNG PHÒNG CHIẾU =====
        db.execSQL("CREATE TABLE Room (" +
                "room_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_name TEXT NOT NULL, " +
                "quantity_seat INTEGER NOT NULL)");

        // ===== BẢNG GHẾ =====
        db.execSQL("CREATE TABLE Seat (" +
                "seat_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_id INTEGER, " +
                "seat_name TEXT NOT NULL, " +
                "status INTEGER DEFAULT 1, " +
                "FOREIGN KEY(room_id) REFERENCES Room(room_id))");

        // ===== BẢNG PHIM =====
        // Sửa định nghĩa bảng Movie trong phương thức onCreate() của DBHelper
        db.execSQL("CREATE TABLE Movie (" +
                "movie_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "movie_name TEXT NOT NULL, " +
                "movie_type TEXT, " +
                "description TEXT, " +
                "image TEXT, " +
                "duration INTEGER, " +
                "language TEXT, " +
                "release_date TEXT, " +
                "point REAL DEFAULT 0, " +
                "status TEXT DEFAULT 'Sắp chiếu')"); // Đổi mặc định thành 'Sắp chiếu'

        // ===== Thêm 10 bộ phim hot gần đây =====
        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, duration,language, release_date, point, status) VALUES " +
                "('Inside Out 2', 'Hoạt hình, Gia đình', 'Tiếp nối hành trình cảm xúc của cô bé Riley với nhiều cảm xúc mới.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340504/inside2_a5etr8.png', 90,'English', '2024-06-14', 8.8, 'Đang chiếu')," +
                "('Deadpool & Wolverine', 'Hành động, Hài hước', 'Hai dị nhân Deadpool và Wolverine cùng hợp tác trong một nhiệm vụ bất ngờ.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340762/phim2_vqlfjn.webp',80, 'English', '2024-07-26', 8.5, 'Sắp chiếu')," +
                "('Dune: Part Two', 'Khoa học viễn tưởng, Phiêu lưu', 'Paul Atreides hợp tác với người Fremen để báo thù cho gia đình.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340826/phim3_wzhyhf.webp', 100,'English', '2024-03-01', 8.6, 'Đã hủy')");



        // ===== BẢNG SUẤT CHIẾU =====
        db.execSQL("CREATE TABLE Showtime (" +
                "showtime_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "movie_id INTEGER, " +
                "room_id INTEGER, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "price REAL, " +
                "show_date TEXT, " +
                "FOREIGN KEY(movie_id) REFERENCES Movie(movie_id), " +
                "FOREIGN KEY(room_id) REFERENCES Room(room_id))");

        // ===== BẢNG VÉ =====
        db.execSQL("CREATE TABLE Ticket (" +
                "ticket_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "showtime_id INTEGER, " +
                "booking_time TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "status TEXT DEFAULT 'booked', " +
                "total_money REAL, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY(showtime_id) REFERENCES Showtime(showtime_id))");

        // ===== BẢNG TRUNG GIAN GIỮA VÉ VÀ GHẾ =====
        db.execSQL("CREATE TABLE Ticket_Seat (" +
                "ticket_seat_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ticket_id INTEGER, " +
                "seat_id INTEGER, " +
                "FOREIGN KEY(ticket_id) REFERENCES Ticket(ticket_id), " +
                "FOREIGN KEY(seat_id) REFERENCES Seat(seat_id))");

        // ===== BẢNG PHƯƠNG THỨC THANH TOÁN =====
        db.execSQL("CREATE TABLE PaymentMethod (" +
                "method_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "method_name TEXT NOT NULL, " +
                "description TEXT, " +
                "status INTEGER DEFAULT 1)");

        // ===== BẢNG THANH TOÁN =====
        db.execSQL("CREATE TABLE Payment (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ticket_id INTEGER UNIQUE, " +
                "user_id INTEGER, " +
                "total_money REAL, " +
                "method_id INTEGER, " +
                "status TEXT DEFAULT 'pending', " +
                "payment_time TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(ticket_id) REFERENCES Ticket(ticket_id), " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY(method_id) REFERENCES PaymentMethod(method_id))");

        // ===== BẢNG ĐÁNH GIÁ PHIM =====
        db.execSQL("CREATE TABLE Review (" +
                "review_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "movie_id INTEGER, " +
                "point REAL, " +
                "comment TEXT, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY(movie_id) REFERENCES Movie(movie_id))");

        // ===== Thêm admin mặc định =====
        db.execSQL("INSERT INTO Users (username, email, phone, password, address, role, status) VALUES " +
                "('Admin', 'admin@cinema.com', '0123456789', 'admin123', 'System', 'admin', 1)");

        // ===== Thêm phương thức thanh toán mẫu =====
        db.execSQL("INSERT INTO PaymentMethod (method_name, description) VALUES " +
                "('Tiền mặt', 'Thanh toán trực tiếp tại quầy'), " +
                "('Ví điện tử', 'Thanh toán qua Momo, ZaloPay...'), " +
                "('Thẻ ngân hàng', 'Thanh toán qua thẻ ATM hoặc VISA')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Review");
        db.execSQL("DROP TABLE IF EXISTS Payment");
        db.execSQL("DROP TABLE IF EXISTS PaymentMethod");
        db.execSQL("DROP TABLE IF EXISTS Ticket_Seat");
        db.execSQL("DROP TABLE IF EXISTS Ticket");
        db.execSQL("DROP TABLE IF EXISTS Showtime");
        db.execSQL("DROP TABLE IF EXISTS Movie");
        db.execSQL("DROP TABLE IF EXISTS Seat");
        db.execSQL("DROP TABLE IF EXISTS Room");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }
    // 🧩 Thêm người dùng mới (đăng ký)
    public boolean addUser(String username, String email, String phone, String address, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("phone", phone);
        cv.put("address", address);
        cv.put("password", password);
        cv.put("role", "user");
        cv.put("status", 1);

        long id = -1;
        try {
            id = db.insertOrThrow("Users", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id != -1;
    }

    // 🧩 Xác thực đăng nhập (login)
    public User authenticate(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Users WHERE email=? AND password=?", new String[]{email, password});

        if (c != null && c.moveToFirst()) {
            User u = new User();
            u.setUser_id(c.getInt(c.getColumnIndexOrThrow("user_id")));
            u.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
            u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
            u.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
            u.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
            u.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
            u.setRole(c.getString(c.getColumnIndexOrThrow("role")));
            u.setStatus(c.getInt(c.getColumnIndexOrThrow("status")));
            c.close();
            return u;
        }
        if (c != null) c.close();
        return null;
    }
    // 🧩 Lấy toàn bộ danh sách người dùng
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Users ORDER BY user_id DESC", null);

        if (c != null && c.moveToFirst()) {
            do {
                User u = new User();
                u.setUser_id(c.getInt(c.getColumnIndexOrThrow("user_id")));
                u.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
                u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                u.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
                u.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                u.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
                u.setRole(c.getString(c.getColumnIndexOrThrow("role")));
                u.setStatus(c.getInt(c.getColumnIndexOrThrow("status")));

                userList.add(u);
            } while (c.moveToNext());
            c.close();
        }

        return userList;
    }
    // 🧩 Xóa user theo ID
    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Users", "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // 🧩 Cập nhật role cho user
    public void updateUserRole(int userId, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", newRole);
        db.update("Users", values, "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }
    // 🧩 Lấy toàn bộ danh sách phim
    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<>();

        // 1. Khai báo hằng số tên bảng và tên cột (Nên được định nghĩa ở đầu lớp DBHelper)
        final String TABLE_MOVIE = "Movie"; // Tên bảng
        final String KEY_MOVIE_ID = "movie_id"; // Hằng số cho cột movie_id
        final String KEY_MOVIE_NAME = "movie_name";
        final String KEY_MOVIE_TYPE = "movie_type";
        final String KEY_DESCRIPTION = "description";
        final String KEY_IMAGE = "image";
        final String KEY_DURATION = "duration"; // Cột mới đã được thêm
        final String KEY_LANGUAGE = "language";
        final String KEY_RELEASE_DATE = "release_date";
        final String KEY_POINT = "point";
        final String KEY_STATUS = "status";

        // Sửa lỗi cú pháp: Dùng hằng số tên bảng và tên cột
        String selectQuery = "SELECT * FROM " + TABLE_MOVIE + " ORDER BY " + KEY_MOVIE_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Lặp qua tất cả các hàng và thêm vào danh sách
        if (cursor.moveToFirst()) {
            do {
                // Đảm bảo bạn đang sử dụng lớp Movie đã được sửa đổi
                Movie movie = new Movie();

                // Ánh xạ dữ liệu từ Cursor vào đối tượng Movie
                movie.setMovie_id(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MOVIE_ID)));
                movie.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_NAME)));
                movie.setMovie_type(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_TYPE)));
                movie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                movie.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));

                // Lấy cột DURATION đã thêm
                movie.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)));

                movie.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LANGUAGE)));
                movie.setRelease_date(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RELEASE_DATE)));
                movie.setPoint(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_POINT)));
                movie.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));

                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return movieList;
    }
    public int markMovieAsCanceled(int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Chỉ cập nhật cột 'status'
        values.put("status", STATUS_DA_HUY);

        int rowsAffected = 0;
        try {
            // Thực hiện lệnh UPDATE: UPDATE Movie SET status = 'Đã hủy' WHERE movie_id = movieId
            rowsAffected = db.update(
                    TABLE_MOVIE,
                    values,
                    KEY_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(movieId)}
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return rowsAffected;
    }









}
