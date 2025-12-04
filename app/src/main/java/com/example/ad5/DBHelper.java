package com.example.ad5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cinema_db.db";
    private static final int DB_VERSION = 15;

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

        // ===== BẢNG TIN TỨC (NEW) =====
        db.execSQL("CREATE TABLE News (" +
                "news_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "summary TEXT, " +
                "content TEXT, " +
                "image TEXT, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");

        // -----------------------------------------------------
        // ===== THÊM DỮ LIỆU CƠ BẢN VÀ DỮ LIỆU MỚI CHO CHỨC NĂNG ĐẶT VÉ =====
        // -----------------------------------------------------

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

        // ===== Thêm 10 bộ phim hot gần đây (Đã bổ sung đủ trường) =====
        db.execSQL("INSERT INTO Movie (movie_id, movie_name, movie_type, description, image, duration, language, release_date, point, status) VALUES " +
                "(1, 'Inside Out 2', 'Hoạt hình, Gia đình', 'Tiếp nối hành trình cảm xúc của cô bé Riley với nhiều cảm xúc mới.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340504/inside2_a5etr8.png', 90,'English', '2024-06-14', 8.8, 'Đang chiếu')," +
                "(2, 'Deadpool & Wolverine', 'Hành động, Hài hước', 'Hai dị nhân Deadpool và Wolverine cùng hợp tác trong một nhiệm vụ bất ngờ.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340762/phim2_vqlfjn.webp',80, 'English', '2024-07-26', 8.5, 'Sắp chiếu')," +
                "(3, 'Dune: Part Two', 'Khoa học viễn tưởng, Phiêu lưu', 'Paul Atreides hợp tác với người Fremen để báo thù cho gia đình.', 'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340826/phim3_wzhyhf.webp', 100,'English', '2024-03-01', 8.6, 'Đang chiếu')," +
                "(4, 'Avengers: Endgame', 'Hành động, Khoa học viễn tưởng', 'Sau sự kiện hủy diệt của Thanos, các siêu anh hùng tập hợp lần cuối để đảo ngược mọi thứ.', 'https://m.media-amazon.com/images/I/71niXI3lxlL._AC_UF894,1000_QL80_.jpg', 181, 'Tiếng Anh - Phụ đề Việt', '2024-01-15', 9.2, 'Đang chiếu')," +
                "(5, 'Spider-Man: No Way Home', 'Hành động, Phiêu lưu', 'Peter Parker phải đối mặt với hậu quả khi danh tính Spider-Man bị tiết lộ.', 'https://resizing.flixster.com/8PNiwC2bpe9OecfYZSOVkvYC5vk=/ems.cHJkLWVtcy1hc3NldHMvbW92aWVzL2U5NGM0Y2Q1LTAyYTItNGFjNC1hNWZhLWMzYjJjOTdjMTFhOS5qcGc=', 148, 'Tiếng Anh - Phụ đề Việt', '2024-02-10', 8.9, 'Đang chiếu')," +
                "(6, 'Mai', 'Tâm lý, Tình cảm', 'Câu chuyện về hành trình tìm lại ký ức của một cô gái trẻ.', 'https://upload.wikimedia.org/wikipedia/vi/3/36/Mai_2024_poster.jpg', 131, 'Tiếng Việt', '2024-02-20', 8.5, 'Đang chiếu')," +
                "(7, 'The Batman', 'Hành động, Tội phạm', 'Batman phơi bày những âm mưu tham nhũng ở Gotham City.', 'https://m.media-amazon.com/images/I/91KkWf50SoL._AC_UF894,1000_QL80_.jpg', 176, 'Tiếng Anh - Phụ đề Việt', '2024-03-05', 8.7, 'Đang chiếu')," +
                "(8, 'Doraemon: Nobita và Vùng Đất Lý Tưởng', 'Hoạt hình, Gia đình', 'Nobita và nhóm bạn khám phá một thế giới hoàn hảo trong trí tưởng tượng.', 'https://cdn.galaxycine.vn/media/2023/5/15/doraemon-utopia-2_1684121814838.jpg', 107, 'Tiếng Việt lồng tiếng', '2024-05-20', 7.8, 'Sắp chiếu')," +
                "(9, 'Godzilla x Kong: The New Empire', 'Hành động, Phiêu lưu', 'Hai gã khổng lồ huyền thoại đối đầu với mối đe dọa mới ẩn náu trong thế giới.', 'https://upload.wikimedia.org/wikipedia/vi/4/41/Godzilla_x_Kong%2C_%C4%91%E1%BA%BF_ch%E1%BA%BF_m%E1%BB%9Bi.jpg', 115, 'Tiếng Anh - Phụ đề Việt', '2024-06-01', 8.3, 'Sắp chiếu')," +
                "(10, 'Lật Mặt 7: Một Điều Ước', 'Hài, Tâm lý', 'Câu chuyện cảm động về gia đình và những điều ước giản đơn nhưng đầy ý nghĩa.', 'https://www.cgv.vn/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/l/a/lat-mat-7.jpg', 130, 'Tiếng Việt', '2024-07-15', 8.1, 'Sắp chiếu')," +
                "(11, 'Oppenheimer', 'Tiểu sử, Lịch sử', 'Cuộc đời của J. Robert Oppenheimer, cha đẻ của bom nguyên tử.', 'https://m.media-amazon.com/images/I/71lqDylcvGL.jpg', 180, 'Tiếng Anh - Phụ đề Việt', '2023-11-20', 9.0, 'Ngừng chiếu')");


        // ===== Thêm dữ liệu Phòng chiếu (Room) và Ghế (Seat) =====
        // Room 1: 8 rows (A-H) x 10 seats (1-10) = 80 seats
        db.execSQL("INSERT INTO Room (room_name, quantity_seat) VALUES ('Room 1', 80)");
        long room1Id = 1;

        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int seatsPerRow = 10;
        for (String rowLabel : rowLabels) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                String seatName = rowLabel + seatNum;
                db.execSQL("INSERT INTO Seat (room_id, seat_name) VALUES (?, ?)",
                        new String[]{String.valueOf(room1Id), seatName});
            }
        }

        // ===== Thêm dữ liệu Suất chiếu (Showtime) =====
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String todayDate = fullDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowDate = fullDateFormat.format(calendar.getTime());

        // Movie IDs (dựa trên dữ liệu phim phía trên)
        int movieInsideOut2Id = 1; // Inside Out 2 (Đang chiếu)
        int movieDunePartTwoId = 3; // Dune: Part Two (Đang chiếu)
        int movieAvengersEndgameId = 4; // Avengers: Endgame (Đang chiếu)
        int movieSpiderManId = 5; // Spider-Man: No Way Home (Đang chiếu)

        // Suất chiếu hôm nay (todayDate)
        db.execSQL("INSERT INTO Showtime (movie_id, room_id, start_time, end_time, price, show_date) VALUES " +
                        "(?, ?, '10:00', '11:30', 100000.0, ?)," + // Movie 1, Room 1, 10:00
                        "(?, ?, '13:30', '15:10', 100000.0, ?)," + // Movie 3, Room 1, 13:30
                        "(?, ?, '17:00', '20:01', 120000.0, ?)," + // Movie 4, Room 1, 17:00
                        "(?, ?, '20:30', '23:00', 100000.0, ?)",   // Movie 5, Room 1, 20:30
                new String[]{
                        String.valueOf(movieInsideOut2Id), String.valueOf(room1Id), todayDate,
                        String.valueOf(movieDunePartTwoId), String.valueOf(room1Id), todayDate,
                        String.valueOf(movieAvengersEndgameId), String.valueOf(room1Id), todayDate,
                        String.valueOf(movieSpiderManId), String.valueOf(room1Id), todayDate
                });

        // Suất chiếu ngày mai (tomorrowDate)
        db.execSQL("INSERT INTO Showtime (movie_id, room_id, start_time, end_time, price, show_date) VALUES " +
                        "(?, ?, '10:30', '12:00', 100000.0, ?)," + // Movie 1, Room 1, 10:30
                        "(?, ?, '14:00', '17:01', 120000.0, ?)," + // Movie 4, Room 1, 14:00
                        "(?, ?, '19:30', '21:10', 100000.0, ?)",   // Movie 3, Room 1, 19:30
                new String[]{
                        String.valueOf(movieInsideOut2Id), String.valueOf(room1Id), tomorrowDate,
                        String.valueOf(movieAvengersEndgameId), String.valueOf(room1Id), tomorrowDate,
                        String.valueOf(movieDunePartTwoId), String.valueOf(room1Id), tomorrowDate
                });

        // ===== Thêm dữ liệu Tin tức (News) (NEW) =====
        db.execSQL("INSERT INTO News (title, summary, content, image) VALUES " +
                "('Phim bom tấn Inside Out 2 lập kỷ lục doanh thu', " +
                "'Bộ phim hoạt hình của Pixar đã vượt mốc 500 triệu USD toàn cầu chỉ sau 1 tuần ra mắt.', " +
                "'Inside Out 2 đang chứng tỏ sức hút khủng khiếp, mang lại làn gió mới cho phòng vé mùa hè. Đây là bộ phim hoạt hình có tốc độ đạt 500 triệu USD nhanh nhất lịch sử.', " +
                "'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340504/inside2_a5etr8.png')");

        db.execSQL("INSERT INTO News (title, summary, content, image) VALUES " +
                "('Deadpool & Wolverine tung trailer cuối cùng', " +
                "'Người hâm mộ được chiêm ngưỡng thêm nhiều pha hành động mãn nhãn giữa hai dị nhân.', " +
                "'Trailer mới nhất hé lộ mối quan hệ phức tạp và những màn đối đầu gay cấn, hứa hẹn sẽ là bộ phim siêu anh hùng được mong chờ nhất năm 2024.', " +
                "'https://res.cloudinary.com/dq4guha5o/image/upload/v1762340762/phim2_vqlfjn.webp')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tất cả bảng để tạo lại DB mới (do cấu trúc ban đầu đã làm như vậy)
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
        db.execSQL("DROP TABLE IF EXISTS News");
        db.execSQL("DROP TABLE IF EXISTS Booking_Details"); // Đã tạo trong BookingActivity.java
        onCreate(db);
    }

    // -----------------------------------------------------
    // ===== PHƯƠNG THỨC HỖ TRỢ CHỨC NĂNG ĐẶT VÉ MỚI =====
    // -----------------------------------------------------

    public List<Showtime> getShowtimesByDateAndMovie(String date, int movieId) {
        List<Showtime> showtimeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT showtime_id, movie_id, room_id, start_time, end_time, price, show_date " +
                "FROM Showtime WHERE show_date=? AND movie_id=? ORDER BY start_time ASC";
        Cursor c = db.rawQuery(query, new String[]{date, String.valueOf(movieId)});

        if (c != null && c.moveToFirst()) {
            do {
                Showtime showtime = new Showtime();
                showtime.setShowtime_id(c.getInt(0));
                showtime.setMovie_id(c.getInt(1));
                showtime.setRoom_id(c.getInt(2));
                showtime.setStart_time(c.getString(3));
                showtime.setEnd_time(c.getString(4));
                showtime.setPrice(c.getDouble(5));
                showtime.setShow_date(c.getString(6));
                showtimeList.add(showtime);
            } while (c.moveToNext());
            c.close();
        }
        return showtimeList;
    }

    // Lấy tất cả ghế trong phòng chiếu để vẽ layout
    public List<Seat> getSeatsByRoomId(int roomId) {
        List<Seat> seatList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Sắp xếp theo tên ghế để việc vẽ layout dễ dàng hơn (A1, A2, ..., B1, B2, ...)
        String query = "SELECT seat_id, room_id, seat_name, status FROM Seat WHERE room_id=? ORDER BY seat_name ASC";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(roomId)});

        if (c != null && c.moveToFirst()) {
            do {
                Seat seat = new Seat();
                seat.setSeat_id(c.getInt(0));
                seat.setRoom_id(c.getInt(1));
                seat.setSeat_name(c.getString(2));
                seat.setStatus(c.getInt(3));
                seatList.add(seat);
            } while (c.moveToNext());
            c.close();
        }
        return seatList;
    }

    // Lấy danh sách tên ghế đã được đặt cho một suất chiếu
    public List<String> getBookedSeatNames(int showtimeId) {
        List<String> bookedSeatNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Lấy tất cả ticket_id đã đặt cho showtime này
        String ticketQuery = "SELECT ticket_id FROM Ticket WHERE showtime_id=? AND status='booked'";
        Cursor ticketCursor = db.rawQuery(ticketQuery, new String[]{String.valueOf(showtimeId)});

        List<Integer> ticketIds = new ArrayList<>();
        if (ticketCursor != null && ticketCursor.moveToFirst()) {
            do {
                ticketIds.add(ticketCursor.getInt(0));
            } while (ticketCursor.moveToNext());
            ticketCursor.close();
        }

        if (ticketIds.isEmpty()) return bookedSeatNames;

        // 2. Lấy seat_name từ Ticket_Seat và Seat
        String ticketIdsString = android.text.TextUtils.join(",", ticketIds);
        String seatQuery = "SELECT S.seat_name FROM Ticket_Seat TS " +
                "JOIN Seat S ON TS.seat_id = S.seat_id " +
                "WHERE TS.ticket_id IN (" + ticketIdsString + ")";

        Cursor seatCursor = db.rawQuery(seatQuery, null);

        if (seatCursor != null && seatCursor.moveToFirst()) {
            do {
                bookedSeatNames.add(seatCursor.getString(0));
            } while (seatCursor.moveToNext());
            seatCursor.close();
        }

        return bookedSeatNames;
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

    public List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT news_id, title, summary, content, image, created_at FROM News ORDER BY created_at DESC", null);

        if (c != null && c.moveToFirst()) {
            do {
                News news = new News();
                news.setNews_id(c.getInt(0));
                news.setTitle(c.getString(1));
                news.setSummary(c.getString(2));
                news.setContent(c.getString(3));
                news.setImage(c.getString(4));
                news.setCreated_at(c.getString(5));
                newsList.add(news);
            } while (c.moveToNext());
            c.close();
        }
        return newsList;
    }
}