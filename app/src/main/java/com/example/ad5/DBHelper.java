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
    private static final int DB_VERSION = 5; // Tăng version để tạo lại DB với dữ liệu mới

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
                "role TEXT DEFAULT 'user', " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "status INTEGER DEFAULT 1)");

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
//        db.execSQL("CREATE TABLE Movie (" +
//                "movie_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "movie_name TEXT NOT NULL, " +
//                "movie_type TEXT, " +
//                "description TEXT, " +
//                "image TEXT, " +
//                "language TEXT, " +
//                "release_date TEXT, " +
//                "point REAL DEFAULT 0, " +
//                "status TEXT DEFAULT 'Đang chiếu')");

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
                "status TEXT DEFAULT 'Đang chiếu')");

        // ===== Thêm 10 bộ phim hot gần đây =====
        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, duration,language, release_date, point, status) VALUES " +
                "('Inside Out 2', 'Hoạt hình, Gia đình', 'Tiếp nối hành trình cảm xúc của cô bé Riley với nhiều cảm xúc mới.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340504/inside2_a5etr8.png', 90,'English', '2024-06-14', 8.8, 'Đang chiếu')," +
                "('Deadpool & Wolverine', 'Hành động, Hài hước', 'Hai dị nhân Deadpool và Wolverine cùng hợp tác trong một nhiệm vụ bất ngờ.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340762/phim2_vqlfjn.webp',80, 'English', '2024-07-26', 8.5, 'Sắp chiếu')," +
                "('Dune: Part Two', 'Khoa học viễn tưởng, Phiêu lưu', 'Paul Atreides hợp tác với người Fremen để báo thù cho gia đình.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340826/phim3_wzhyhf.webp', 100,'English', '2024-03-01', 8.6, 'Đang chiếu')");

        // ===== Thêm admin mặc định =====
        db.execSQL("INSERT INTO Users (username, email, phone, password, address, role, status) VALUES " +
                "('Admin', 'admin@cinema.com', '0123456789', 'admin123', 'System', 'admin', 1)");

        // ===== Thêm user test =====
        db.execSQL("INSERT INTO Users (username, email, phone, password, address, role, status) VALUES " +
                "('Nguyen Van A', 'user@test.com', '0987654321', '123456', 'Ha Noi', 'user', 1)");

        // ===== Thêm phương thức thanh toán mẫu =====
        db.execSQL("INSERT INTO PaymentMethod (method_name, description) VALUES " +
                "('Tiền mặt', 'Thanh toán trực tiếp tại quầy'), " +
                "('Ví điện tử', 'Thanh toán qua Momo, ZaloPay...'), " +
                "('Thẻ ngân hàng', 'Thanh toán qua thẻ ATM hoặc VISA')");

        // ===== Thêm phim mẫu =====
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Avengers: Endgame', 'Hành động, Khoa học viễn tưởng', " +
//                "'Sau sự kiện hủy diệt của Thanos, các siêu anh hùng tập hợp lần cuối để đảo ngược mọi thứ.', " +
//                "'https://m.media-amazon.com/images/I/71niXI3lxlL._AC_UF894,1000_QL80_.jpg', " +
//                "'Tiếng Anh - Phụ đề Việt', '2024-01-15', 9.2, 'Đang chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Spider-Man: No Way Home', 'Hành động, Phiêu lưu', " +
//                "'Peter Parker phải đối mặt với hậu quả khi danh tính Spider-Man bị tiết lộ.', " +
//                "'https://m.media-amazon.com/images/I/91g5aJC8V3L.jpg', " +
//                "'Tiếng Anh - Phụ đề Việt', '2024-02-10', 8.9, 'Đang chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Mai', 'Tâm lý, Tình cảm', " +
//                "'Câu chuyện về hành trình tìm lại ký ức của một cô gái trẻ.', " +
//                "'https://cdn.galaxycine.vn/media/2024/1/29/mai-500_1706508032488.jpg', " +
//                "'Tiếng Việt', '2024-02-20', 8.5, 'Đang chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('The Batman', 'Hành động, Tội phạm', " +
//                "'Batman phơi bày những âm mưu tham nhũng ở Gotham City.', " +
//                "'https://m.media-amazon.com/images/I/91KkWf50SoL._AC_UF894,1000_QL80_.jpg', " +
//                "'Tiếng Anh - Phụ đề Việt', '2024-03-05', 8.7, 'Đang chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Doraemon: Nobita và Vùng Đất Lý Tưởng', 'Hoạt hình, Gia đình', " +
//                "'Nobita và nhóm bạn khám phá một thế giới hoàn hảo trong trí tưởng tượng.', " +
//                "'https://cdn.galaxycine.vn/media/2023/5/26/doraemon-500_1685097050737.jpg', " +
//                "'Tiếng Việt lồng tiếng', '2024-05-20', 7.8, 'Sắp chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Godzilla x Kong: The New Empire', 'Hành động, Phiêu lưu', " +
//                "'Hai gã khổng lồ huyền thoại đối đầu với mối đe dọa mới ẩn náu trong thế giới.', " +
//                "'https://m.media-amazon.com/images/I/81ue9l9fCaL._AC_UF894,1000_QL80_.jpg', " +
//                "'Tiếng Anh - Phụ đề Việt', '2024-06-01', 8.3, 'Sắp chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Lật Mặt 7: Một Điều Ước', 'Hài, Tâm lý', " +
//                "'Câu chuyện cảm động về gia đình và những điều ước giản đơn nhưng đầy ý nghĩa.', " +
//                "'https://cdn.galaxycine.vn/media/2023/12/12/lat-mat-7-500_1702368516788.jpg', " +
//                "'Tiếng Việt', '2024-07-15', 8.1, 'Sắp chiếu')");
//
//        db.execSQL("INSERT INTO Movie (movie_name, movie_type, description, image, language, release_date, point, status) VALUES " +
//                "('Oppenheimer', 'Tiểu sử, Lịch sử', " +
//                "'Cuộc đời của J. Robert Oppenheimer, cha đẻ của bom nguyên tử.', " +
//                "'https://m.media-amazon.com/images/I/71lqDylcvGL.jpg', " +
//                "'Tiếng Anh - Phụ đề Việt', '2023-11-20', 9.0, 'Ngừng chiếu')");
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

    // 🧩 Thêm phim mới
    public boolean addMovie(String movie_name, String movie_type, String description,
                            String image, String language, String release_date,
                            double point, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("movie_name", movie_name);
        cv.put("movie_type", movie_type);
        cv.put("description", description);
        cv.put("image", image);
        cv.put("language", language);
        cv.put("release_date", release_date);
        cv.put("point", point);
        cv.put("status", status);

        long id = -1;
        try {
            id = db.insertOrThrow("Movie", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id != -1;
    }

    // 🧩 Lấy danh sách phim theo trạng thái
    public List<Movie> getMoviesByStatus(String status) {
        List<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Movie WHERE status=? ORDER BY point DESC",
                new String[]{status});

        if (c != null && c.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setMovie_id(c.getInt(c.getColumnIndexOrThrow("movie_id")));
                movie.setMovie_name(c.getString(c.getColumnIndexOrThrow("movie_name")));
                movie.setMovie_type(c.getString(c.getColumnIndexOrThrow("movie_type")));
                movie.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
                movie.setImage(c.getString(c.getColumnIndexOrThrow("image")));
                movie.setLanguage(c.getString(c.getColumnIndexOrThrow("language")));
                movie.setRelease_date(c.getString(c.getColumnIndexOrThrow("release_date")));
                movie.setPoint(c.getDouble(c.getColumnIndexOrThrow("point")));
                movie.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                movieList.add(movie);
            } while (c.moveToNext());
            c.close();
        }
        return movieList;
    }

    // 🧩 Lấy tất cả phim
    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Movie ORDER BY point DESC", null);

        if (c != null && c.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setMovie_id(c.getInt(c.getColumnIndexOrThrow("movie_id")));
                movie.setMovie_name(c.getString(c.getColumnIndexOrThrow("movie_name")));
                movie.setMovie_type(c.getString(c.getColumnIndexOrThrow("movie_type")));
                movie.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
                movie.setImage(c.getString(c.getColumnIndexOrThrow("image")));
                movie.setLanguage(c.getString(c.getColumnIndexOrThrow("language")));
                movie.setRelease_date(c.getString(c.getColumnIndexOrThrow("release_date")));
                movie.setPoint(c.getDouble(c.getColumnIndexOrThrow("point")));
                movie.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                movieList.add(movie);
            } while (c.moveToNext());
            c.close();
        }
        return movieList;
    }
}
