package com.example.ad5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cinema_db.db";
    private static final int DB_VERSION = 9;
    // Đảm bảo bạn có các hằng số này:
    private static final String TABLE_MOVIE = "Movie";
    private static final String KEY_MOVIE_ID = "movie_id";
    public static final String STATUS_DA_HUY = "Đã hủy"; // Hằn

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ... (CREATE TABLE và INSERT logic) ...

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
                "status INTEGER DEFAULT 1)");

        // ===== BẢNG PHÒNG CHIẾU =====
        db.execSQL("CREATE TABLE Room (" +
                "room_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_name TEXT NOT NULL, " +
                "quantity_seat INTEGER NOT NULL)");

        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Phòng 2D Standard', 50)");
        // room_id 2: Phòng VIP
        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Phòng VIP 3D', 40)");
        // room_id 3: Phòng IMAX
        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Phòng IMAX', 80)");
        // room_id 4: Phòng Thường 2
        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Phòng 2D Standard 2', 50)");
        // room_id 5: Phòng Phổ thông
        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Phòng Phổ thông', 60)");

        // ===== BẢNG GHẾ =====
        db.execSQL("CREATE TABLE Seat (" +
                "seat_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_id INTEGER, " +
                "seat_name TEXT NOT NULL, " +
                "status INTEGER DEFAULT 1, " +
                "FOREIGN KEY(room_id) REFERENCES Room(room_id))");

        db.execSQL("INSERT INTO Seat (room_id, seat_name) VALUES (1, 'A1'), (1, 'A2'), (1, 'B1')");
        db.execSQL("INSERT INTO Seat (room_id, seat_name) VALUES (2, 'VIP-A1'), (2, 'VIP-A2')");

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

        // 3a. Suất chiếu cho Inside Out 2 (Movie ID = 1) - Phòng 2D Standard (Room ID = 1)
        db.execSQL("INSERT INTO Showtime (movie_id, room_id, start_time, end_time, price, show_date) VALUES " +
                "(1, 1, '18:00', '19:30', 120000.00, '2025-11-21')," +
                "(1, 4, '20:00', '21:30', 110000.00, '2025-11-21')");

        // 3b. Suất chiếu cho Deadpool & Wolverine (Movie ID = 2) - Phòng VIP (Room ID = 2) và IMAX (Room ID = 3)
        db.execSQL("INSERT INTO Showtime (movie_id, room_id, start_time, end_time, price, show_date) VALUES " +
                "(2, 2, '19:30', '21:00', 180000.00, '2025-11-21')," + // VIP 3D
                "(2, 3, '22:00', '23:30', 150000.00, '2025-11-21')"); // IMAX

        // 3c. Suất chiếu cho Dune: Part Two (Movie ID = 3) - Phòng Phổ thông (Room ID = 5)
        db.execSQL("INSERT INTO Showtime (movie_id, room_id, start_time, end_time, price, show_date) VALUES " +
                "(3, 5, '17:00', '19:00', 90000.00, '2025-11-22')"); // Ngày hôm sau

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
        // XÓA db.close()
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
        // XÓA db.close()
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

        // XÓA db.close()
        return userList;
    }
    // 🧩 Cập nhật trạng thái user thành "đã hủy" thay vì xóa hẳn
    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 0); // 0 = Đã hủy, 1 = Đang hoạt động

        db.update("Users", values, "user_id=?", new String[]{String.valueOf(userId)});
        // XÓA db.close()
    }

    // 🧩 Cập nhật role cho user
    public void updateUserRole(int userId, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", newRole);
        db.update("Users", values, "user_id=?", new String[]{String.valueOf(userId)});
        // XÓA db.close()
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
        // XÓA db.close()
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
        }
        // XÓA db.close()
        return rowsAffected;
    }
    // 🧩 Cập nhật thông tin một bộ phim
    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Gán các giá trị cần cập nhật
        // Truyền thẳng tên cột
        values.put("movie_name", movie.getMovie_name());
        values.put("movie_type", movie.getMovie_type());
        values.put("description", movie.getDescription());
        values.put("image", movie.getImage());
        values.put("duration", movie.getDuration());
        values.put("language", movie.getLanguage());
        values.put("release_date", movie.getRelease_date());
        values.put("point", movie.getPoint());
        values.put("status", movie.getStatus());

        int rowsAffected = 0;
        try {
            // UPDATE Movie SET ... WHERE movie_id = ?
            rowsAffected = db.update(
                    TABLE_MOVIE,
                    values,
                    KEY_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(movie.getMovie_id())}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        // XÓA db.close()
        return rowsAffected;
    }
    public long addMovie(String name, String type, int duration,
                         String description, String status, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("movie_name", name);
        values.put("movie_type", type);
        values.put("duration", duration);
        values.put("description", description);
        values.put("status", status);
        values.put("image", imageUrl);

        long id = -1;
        try {
            id = db.insert("Movie", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // XÓA db.close()
        return id;
    }
    // 🧩 Lấy toàn bộ danh sách phòng chiếu
    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Room ORDER BY room_id DESC", null);

        if (c != null && c.moveToFirst()) {
            do {
                Room r = new Room();
                r.setRoom_id(c.getInt(c.getColumnIndexOrThrow("room_id")));
                r.setRoom_name(c.getString(c.getColumnIndexOrThrow("room_name")));
                r.setQuantity_seat(c.getInt(c.getColumnIndexOrThrow("quantity_seat")));
                roomList.add(r);
            } while (c.moveToNext());
            c.close();
        }
        // XÓA db.close()
        return roomList;
    }
    // 🧩 Thêm phòng chiếu mới
    public boolean addRoom(String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("room_name", name);
        cv.put("quantity_seat", quantity);

        long result = db.insert("Room", null, cv);
        // XÓA db.close()
        return result != -1;
    }

    // 🧩 Cập nhật thông tin phòng chiếu
    public boolean updateRoom(int roomId, String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("room_name", name);
        cv.put("quantity_seat", quantity);

        int rows = db.update("Room", cv, "room_id=?", new String[]{String.valueOf(roomId)});
        // XÓA db.close()
        return rows > 0;
    }

    // 🧩 Xóa phòng chiếu
    public boolean deleteRoom(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Room", "room_id=?", new String[]{String.valueOf(roomId)});
        // XÓA db.close()
        return rows > 0;
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

    // Trong lớp DBHelper.java

    public List<Showtime> getShowtime_id(int movieId) {
        List<Showtime> showtimeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Tên các bảng và cột (cần đảm bảo khớp với schema thực tế của bạn)
        String SHOWTIME_TABLE = "Showtime";
        String MOVIE_TABLE = "Movie";
        String ROOM_TABLE = "Room";

        String query = "SELECT " +
                "S.showtime_id, S.start_time, S.end_time, S.price, S.show_date, " +
                "R.room_name, " +
                "M.movie_name, M.image " +
                "FROM " + SHOWTIME_TABLE + " S " +
                "INNER JOIN " + MOVIE_TABLE + " M ON S.movie_id = M.movie_id " +
                "INNER JOIN " + ROOM_TABLE + " R ON S.room_id = R.room_id " +
                "WHERE S.movie_id = ?";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(movieId)});

            if (cursor.moveToFirst()) {
                do {
                    Showtime showtime = new Showtime();

                    // Thuộc tính chính
                    showtime.setShowtime_id(cursor.getInt(cursor.getColumnIndexOrThrow("showtime_id")));
                    showtime.setStart_time(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                    showtime.setEnd_time(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
                    showtime.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    showtime.setShow_date(cursor.getString(cursor.getColumnIndexOrThrow("show_date")));

                    // Thuộc tính JOIN
                    showtime.setRoom_name(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
                    showtime.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow("movie_name")));
                    showtime.setMovie_image(cursor.getString(cursor.getColumnIndexOrThrow("image")));

                    showtimeList.add(showtime);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while trying to get showtimes", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return showtimeList;
    }

    // Trong lớp DBHelper.java

    public String getMovieTitleById(int movieId) {
        String movieTitle = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String MOVIE_TABLE = "Movie"; // Tên bảng phim
        String MOVIE_NAME_COL = "movie_name"; // Tên cột tên phim

        try {
            cursor = db.query(
                    MOVIE_TABLE,
                    new String[]{MOVIE_NAME_COL},
                    "movie_id = ?",
                    new String[]{String.valueOf(movieId)},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                movieTitle = cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_NAME_COL));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while trying to get movie title", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return movieTitle;
    }

    // Trong lớp DBHelper.java

    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Tên các bảng (Giả định khớp với schema của bạn)
        String SHOWTIME_TABLE = "Showtime";
        String MOVIE_TABLE = "Movie";
        String ROOM_TABLE = "Room";

        // Truy vấn JOIN: Lấy TẤT CẢ suất chiếu (không có điều kiện WHERE)
        String query = "SELECT " +
                "S.showtime_id, S.start_time, S.end_time, S.price, S.show_date, " +
                "R.room_name, " +
                "M.movie_name, M.image AS image " + // M.image là tên cột DB, ánh xạ thành movie_image
                "FROM " + SHOWTIME_TABLE + " S " +
                "INNER JOIN " + MOVIE_TABLE + " M ON S.movie_id = M.movie_id " +
                "INNER JOIN " + ROOM_TABLE + " R ON S.room_id = R.room_id " +
                "ORDER BY S.show_date ASC, S.start_time ASC"; // Sắp xếp theo ngày và giờ chiếu

        try {
            // Thực thi truy vấn, không có đối số WHERE
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Showtime showtime = new Showtime();

                    // Thuộc tính chính
                    showtime.setShowtime_id(cursor.getInt(cursor.getColumnIndexOrThrow("showtime_id")));
                    showtime.setStart_time(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                    showtime.setEnd_time(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
                    showtime.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    showtime.setShow_date(cursor.getString(cursor.getColumnIndexOrThrow("show_date")));

                    // Thuộc tính JOIN (movie_image khớp với alias 'AS movie_image')
                    showtime.setRoom_name(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
                    showtime.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow("movie_name")));
                    showtime.setMovie_image(cursor.getString(cursor.getColumnIndexOrThrow("image")));

                    showtimeList.add(showtime);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while trying to get all showtimes", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return showtimeList;
    }


    public long addShowtime(int movieId, int roomId, String startTime, String endTime, double price, String showDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 1. Ánh xạ dữ liệu vào ContentValues (phải khớp với tên cột DB)
        values.put("movie_id", movieId);
        values.put("room_id", roomId);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("price", price);
        values.put("show_date", showDate); // YYYY-MM-DD

        long result = -1;
        try {
            // 2. Chèn dữ liệu vào bảng Showtime
            // Trả về row ID của bản ghi mới nếu thành công, -1 nếu thất bại
            result = db.insert("Showtime", null, values);
        } catch (Exception e) {
            // Ghi lại lỗi nếu có vấn đề về ràng buộc (ví dụ: FOREIGN KEY) hoặc định dạng
            Log.e("DBHelper", "Lỗi khi thêm suất chiếu: " + e.getMessage());
            e.printStackTrace();
        }
        // XÓA db.close()
        return result;
    }

    public Showtime getShowtimeDetailsById(int showtimeId) {
        Showtime showtime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String SHOWTIME_TABLE = "Showtime";
        String MOVIE_TABLE = "Movie";
        String ROOM_TABLE = "Room";

        String query = "SELECT " +
                // Các cột từ Showtime (S) - bao gồm cả movie_id và room_id
                "S.showtime_id, S.movie_id, S.room_id, S.start_time, S.end_time, S.price, S.show_date, " +
                // Các cột JOIN (R, M)
                "R.room_name, " +
                "M.movie_name, M.image AS image " + // Dùng M.image AS movie_image để khớp với data model
                "FROM " + SHOWTIME_TABLE + " S " +
                "INNER JOIN " + MOVIE_TABLE + " M ON S.movie_id = M.movie_id " +
                "INNER JOIN " + ROOM_TABLE + " R ON S.room_id = R.room_id " +
                "WHERE S.showtime_id = ?"; // Lọc theo Showtime ID

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(showtimeId)});

            if (cursor.moveToFirst()) {
                showtime = new Showtime();

                // Thuộc tính chính
                showtime.setShowtime_id(cursor.getInt(cursor.getColumnIndexOrThrow("showtime_id")));
                // Lấy ID Phim và ID Phòng (Quan trọng cho SeatSelectionActivity)
                showtime.setMovie_id(cursor.getInt(cursor.getColumnIndexOrThrow("movie_id")));
                showtime.setRoom_id(cursor.getInt(cursor.getColumnIndexOrThrow("room_id")));
                showtime.setStart_time(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                showtime.setEnd_time(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
                showtime.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                showtime.setShow_date(cursor.getString(cursor.getColumnIndexOrThrow("show_date")));

                // Thuộc tính JOIN (movie_image khớp với alias/tên cột M.image)
                showtime.setRoom_name(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
                showtime.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow("movie_name")));
                // Dùng 'movie_image' vì đã sử dụng alias AS movie_image trong truy vấn (hoặc dùng 'image' nếu không dùng alias)
                showtime.setMovie_image(cursor.getString(cursor.getColumnIndexOrThrow("image")));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while trying to get showtime details by ID: " + showtimeId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // KHÔNG ĐÓNG DB.close() ở đây để tránh lỗi đa luồng
        }
        return showtime;
    }

    public List<Seat> getSeatsForShowtime(int roomId, int showtimeId) {
        List<Seat> seatList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Tên các bảng và cột
        String SEAT_TABLE = "Seat";
        String TICKET_SEAT_TABLE = "Ticket_Seat";
        String TICKET_TABLE = "Ticket";

        // Câu truy vấn: Sử dụng LEFT JOIN để lấy tất cả ghế (S) và kiểm tra xem
        // chúng có được đặt (TS) qua Ticket (T) cho suất chiếu này hay không.
        // Nếu TS.seat_id là NULL, ghế chưa được đặt.
        String query = "SELECT " +
                "S.seat_id, S.room_id, S.seat_name, " +
                "CASE WHEN T.status = 'booked' THEN 0 ELSE 1 END AS status_available " + // 0: Đã đặt, 1: Còn trống
                "FROM " + SEAT_TABLE + " S " +
                // LEFT JOIN với Ticket_Seat (TS)
                "LEFT JOIN " + TICKET_SEAT_TABLE + " TS ON S.seat_id = TS.seat_id " +
                // LEFT JOIN với Ticket (T) để kiểm tra suất chiếu và trạng thái vé
                "LEFT JOIN " + TICKET_TABLE + " T ON TS.ticket_id = T.ticket_id AND T.showtime_id = ? " +
                // Chỉ lấy ghế thuộc phòng này
                "WHERE S.room_id = ? " +
                // Sắp xếp theo tên ghế (ví dụ: A1, A2, B1, B2)
                "ORDER BY S.seat_name ASC";

        // Đối số cho WHERE: 1. showtimeId (cho T.showtime_id), 2. roomId (cho S.room_id)
        String[] selectionArgs = {String.valueOf(showtimeId), String.valueOf(roomId)};

        try {
            cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    Seat seat = new Seat();

                    seat.setSeat_id(cursor.getInt(cursor.getColumnIndexOrThrow("seat_id")));
                    seat.setRoom_id(cursor.getInt(cursor.getColumnIndexOrThrow("room_id")));
                    seat.setSeat_name(cursor.getString(cursor.getColumnIndexOrThrow("seat_name")));

                    // 💡 Lấy trạng thái từ CASE WHEN:
                    // 0 nếu đã được đặt cho suất chiếu này, 1 nếu còn trống.
                    seat.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status_available")));

                    seatList.add(seat);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while trying to get seats for showtime " + showtimeId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Giữ nguyên: KHÔNG ĐÓNG db.close()
        }
        return seatList;
    }

    public long createTicketAndSeats(int userId, int showtimeId, double totalPrice,
                                     ArrayList<Integer> seatIds, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        long ticketId = -1;

        // Bắt đầu giao dịch
        db.beginTransaction();
        try {
            // 1. CHÈN VÉ VÀO BẢNG TICKET
            ContentValues ticketValues = new ContentValues();
            ticketValues.put("user_id", userId);
            ticketValues.put("showtime_id", showtimeId);
            ticketValues.put("total_money", totalPrice);
            ticketValues.put("status", status); // 'pending'

            ticketId = db.insert("Ticket", null, ticketValues);

            if (ticketId > 0) {
                // 2. CHÈN GHẾ VÀO BẢNG TRUNG GIAN (TICKET_SEAT)
                for (int seatId : seatIds) {
                    ContentValues tsValues = new ContentValues();
                    tsValues.put("ticket_id", ticketId);
                    tsValues.put("seat_id", seatId);

                    long tsResult = db.insert("Ticket_Seat", null, tsValues);

                    // Nếu bất kỳ ghế nào không chèn được (ví dụ: lỗi FK), hủy giao dịch
                    if (tsResult == -1) {
                        throw new Exception("Thêm ghế vào vé thất bại.");
                    }
                }

                // 3. COMMIT GIAO DỊCH nếu tất cả thành công
                db.setTransactionSuccessful();

            } else {
                // Chèn Ticket thất bại
                ticketId = -1;
            }

        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi giao dịch đặt vé: " + e.getMessage());
            ticketId = -1; // Đảm bảo trả về -1 nếu có lỗi
        } finally {
            // Kết thúc giao dịch (commit nếu setTransactionSuccessful được gọi, rollback nếu không)
            db.endTransaction();
            // KHÔNG CẦN db.close()
        }

        return ticketId;
    }

    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> ticketList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Truy vấn JOIN phức tạp để lấy thông tin chi tiết suất chiếu và phòng
        String query = "SELECT " +
                "T.ticket_id, T.total_money, T.booking_time, T.status, T.user_id, T.showtime_id, " +
                // Các trường JOIN
                "U.username, M.movie_name, R.room_name, S.show_date, S.start_time, M.image AS image " +
                "FROM Ticket T " +
                "INNER JOIN Users U ON T.user_id = U.user_id " +
                "INNER JOIN Showtime S ON T.showtime_id = S.showtime_id " +
                "INNER JOIN Movie M ON S.movie_id = M.movie_id " +
                "INNER JOIN Room R ON S.room_id = R.room_id " +
                "WHERE T.user_id = ? " + // 🎯 Lọc theo User ID
                "ORDER BY T.booking_time DESC";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Ticket ticket = new Ticket();

                    // Thuộc tính chính
                    ticket.setTicket_id(cursor.getInt(cursor.getColumnIndexOrThrow("ticket_id")));
                    ticket.setTotal_money(cursor.getDouble(cursor.getColumnIndexOrThrow("total_money")));
                    ticket.setBooking_time(cursor.getString(cursor.getColumnIndexOrThrow("booking_time")));
                    ticket.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                    // Thuộc tính JOIN (movie_image khớp với alias 'AS movie_image')
                    ticket.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                    ticket.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow("movie_name")));
                    ticket.setRoom_name(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
                    ticket.setShowtimeDate(cursor.getString(cursor.getColumnIndexOrThrow("show_date")));
                    ticket.setShowtimeStart(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));

                    // 💡 Lưu ý: Hàm này không lấy seats. Cần thêm hàm lấy ghế riêng nếu cần.

                    ticketList.add(ticket);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error loading tickets by user ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Giữ nguyên: KHÔNG ĐÓNG db.close()
        }
        return ticketList;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> ticketList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Truy vấn JOIN tương tự getTicketsByUserId nhưng BỎ ĐIỀU KIỆN WHERE T.user_id = ?
        String query = "SELECT " +
                "T.ticket_id, T.total_money, T.booking_time, T.status, T.user_id, T.showtime_id, " +
                "U.username, M.movie_name, R.room_name, S.show_date, S.start_time, M.image AS image " +
                "FROM Ticket T " +
                "INNER JOIN Users U ON T.user_id = U.user_id " +
                "INNER JOIN Showtime S ON T.showtime_id = S.showtime_id " +
                "INNER JOIN Movie M ON S.movie_id = M.movie_id " +
                "INNER JOIN Room R ON S.room_id = R.room_id " +
                "ORDER BY T.booking_time DESC";

        try {
            // Không có đối số WHERE
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Ticket ticket = new Ticket();

                    // Ánh xạ dữ liệu vào Ticket Model (giống hệt logic getTicketsByUserId)
                    ticket.setTicket_id(cursor.getInt(cursor.getColumnIndexOrThrow("ticket_id")));
                    ticket.setTotal_money(cursor.getDouble(cursor.getColumnIndexOrThrow("total_money")));
                    ticket.setBooking_time(cursor.getString(cursor.getColumnIndexOrThrow("booking_time")));
                    ticket.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                    // Thuộc tính JOIN
                    ticket.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                    ticket.setMovie_name(cursor.getString(cursor.getColumnIndexOrThrow("movie_name")));
                    ticket.setRoom_name(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
                    ticket.setShowtimeDate(cursor.getString(cursor.getColumnIndexOrThrow("show_date")));
                    ticket.setShowtimeStart(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));

                    ticketList.add(ticket);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error loading all tickets: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ticketList;
    }

    public User getUserById(int userId) {
        // Đảm bảo rằng bạn có class User.java trong project
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;

        try {
            // Truy vấn: SELECT * FROM Users WHERE user_id = ?
            c = db.rawQuery("SELECT * FROM Users WHERE user_id=?", new String[]{String.valueOf(userId)});

            if (c != null && c.moveToFirst()) {
                user = new User();
                // Ánh xạ dữ liệu từ Cursor vào đối tượng User
                user.setUser_id(c.getInt(c.getColumnIndexOrThrow("user_id")));
                user.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
                user.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                user.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
                user.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                user.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
                user.setRole(c.getString(c.getColumnIndexOrThrow("role")));
                user.setStatus(c.getInt(c.getColumnIndexOrThrow("status")));
            }
        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error getting user by ID: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            // Giữ nguyên: KHÔNG ĐÓNG db.close() để tránh lỗi
        }

        return user;
    }


//ảnh mưa đỏ
//Sắp chiếu









}
