/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Booking;
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
