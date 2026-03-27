package com.example.movie.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // ========================================================================
    // 1xxx: GLOBAL / SYSTEM ERRORS
    // ========================================================================
    UNCATEGORIZED_EXCEPTION(9999, "Đã có lỗi hệ thống xảy ra, vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Dữ liệu không hợp lệ.", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER(1002, "Thiếu tham số bắt buộc.", HttpStatus.BAD_REQUEST),
    DATA_INVALID(1003, "Dữ liệu không hợp lệ hoặc bị trùng lặp.", HttpStatus.BAD_REQUEST),

    // ========================================================================
    // 2xxx: AUTHENTICATION & USER
    // ========================================================================
    UNAUTHENTICATED(2001, "Chưa đăng nhập hoặc phiên đăng nhập đã hết hạn.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Bạn không có quyền truy cập tính năng này.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(2003, "Không tìm thấy thông tin tài khoản.", HttpStatus.NOT_FOUND),
    EXISTED_EMAIL(2004, "Địa chỉ email này đã được sử dụng, vui lòng chọn email khác.", HttpStatus.CONFLICT),
    ROLE_NOT_FOUND(2005, "Không tìm thấy vai trò (Role) này trên hệ thống.", HttpStatus.NOT_FOUND),
    STAFF_ROLE_CHANGE_DENIED(2006, "Không thể thay đổi vai trò hiện tại để tránh xung đột quyền hạn.",  HttpStatus.CONFLICT),
    INVALID_PASSWORD(2007, "Mật khẩu không chính xác.", HttpStatus.UNAUTHORIZED),
    USER_LOCKED(2008, "Tài khoản của bạn đã bị khóa hoặc vô hiệu hóa.", HttpStatus.FORBIDDEN),
    INVALID_INFORMATION(2009, "Sai tên đăng nhập hoặc mật khẩu.", HttpStatus.UNAUTHORIZED),

    // ========================================================================
    // 3xxx: MOVIE
    // ========================================================================
    MOVIE_NOT_FOUND(3001, "Không tìm thấy phim.", HttpStatus.BAD_REQUEST),

    // ========================================================================
    // 4xxx: SHOWTIME
    // ========================================================================

    // ========================================================================
    // 5xxx: CINEMA
    // ========================================================================
    CINEMA_NOT_FOUND(5001, "Không tìm thấy rạp.", HttpStatus.BAD_REQUEST),
    // ========================================================================
    // 6xxx: BOOKING
    // ========================================================================
    SHOWTIME_NOT_FOUND(6001, "Không tìm thấy suất chiếu.", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_SEATS(6002, "Không đủ ghế.", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(6003, "Không tìm thấy đơn đặt vé.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
