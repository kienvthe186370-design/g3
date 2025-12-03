/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.Booking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class BookingDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // 1. Hàm kiểm tra phòng trống (Check theo Loại Phòng)
    // Trả về true nếu còn đủ phòng
    public boolean checkAvailability(int roomTypeId, int quantity, Timestamp checkIn, Timestamp checkOut) {
        try {
            String query = "SELECT \n"
                    + "  (SELECT COUNT(*) FROM Rooms WHERE roomTypeId = ? AND currentStatusId != 4 AND isDeleted = 0) - \n" // Tổng phòng
                    + "  (SELECT ISNULL(SUM(bd.quantity), 0) \n"
                    + "   FROM BookingDetails bd \n"
                    + "   JOIN Bookings b ON bd.bookingId = b.bookingId \n"
                    + "   WHERE bd.roomTypeId = ? \n"
                    + "   AND b.status NOT IN ('Cancelled', 'CheckedOut') \n"
                    + "   AND (b.checkInDate < ? AND b.checkOutDate > ?))"; // Số phòng đang bận

            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, roomTypeId);
            ps.setInt(2, roomTypeId);
            ps.setTimestamp(3, checkOut); // Logic check trùng lịch
            ps.setTimestamp(4, checkIn);

            rs = ps.executeQuery();
            if (rs.next()) {
                int availableRooms = rs.getInt(1);
                return availableRooms >= quantity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false;
    }
    public boolean createBooking(Booking booking, int roomTypeId, int quantity, BigDecimal pricePerNight) {
    boolean result = false;
    try {
        conn = new DBContext().getConnection();
        
        // TẮT Auto Commit để bắt đầu Transaction
        conn.setAutoCommit(false);

        // Bước 1: Tạo UUID cho Booking ID (Vì SQL để là uniqueidentifier)
        String newBookingId = UUID.randomUUID().toString();
        booking.setBookingId(newBookingId);

        // --- SỬA: XÓA [cite_start] Ở ĐÂY ---
        String sqlBooking = "INSERT INTO Bookings (bookingId, userId, guestName, guestPhone, checkInDate, checkOutDate, totalDeposit, status, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        ps = conn.prepareStatement(sqlBooking);
        ps.setString(1, newBookingId);
        
        if (booking.getUserId() > 0) ps.setInt(2, booking.getUserId()); 
        else ps.setObject(2, null); // Khách vãng lai

        ps.setString(3, booking.getGuestName());
        ps.setString(4, booking.getGuestPhone());
        ps.setTimestamp(5, booking.getCheckInDate());
        ps.setTimestamp(6, booking.getCheckOutDate());
        ps.setBigDecimal(7, booking.getTotalDeposit());
        ps.setString(8, "Confirmed");
        
        ps.executeUpdate();

        // --- SỬA: XÓA [cite_start] Ở ĐÂY ---
        // Đây là bước quan trọng để liên kết Booking với Loại phòng
        String sqlDetail = "INSERT INTO BookingDetails (bookingId, roomTypeId, quantity, pricePerNight) VALUES (?, ?, ?, ?)";
        PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
        psDetail.setString(1, newBookingId);
        psDetail.setInt(2, roomTypeId);
        psDetail.setInt(3, quantity);
        psDetail.setBigDecimal(4, pricePerNight);
        
        psDetail.executeUpdate();

        // Bước 4: Commit Transaction (Lưu tất cả thay đổi)
        conn.commit();
        result = true;

    } catch (Exception e) {
        e.printStackTrace();
        try {
            if (conn != null) conn.rollback(); // Nếu lỗi thì hoàn tác
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } finally {
        try {
            if (conn != null) conn.setAutoCommit(true); // Bật lại auto commit
            closeResources();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return result;
}
    
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
