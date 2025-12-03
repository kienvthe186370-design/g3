package entity;
import java.math.BigDecimal;
import java.sql.Timestamp; // QUAN TRỌNG: Phải import cái này

public class Booking {
    private String bookingId;
    private int userId;
    private String guestName;
    private String guestPhone;
    private Timestamp checkInDate; // Sửa thành Timestamp
    private Timestamp checkOutDate; // Sửa thành Timestamp
    private BigDecimal totalDeposit;
    private String status;
    private Timestamp createdAt;

    public Booking() {
    }

    // Constructor đầy đủ
    public Booking(String bookingId, int userId, String guestName, String guestPhone, Timestamp checkInDate, Timestamp checkOutDate, BigDecimal totalDeposit, String status, Timestamp createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalDeposit = totalDeposit;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- GETTER & SETTER (Bắt buộc phải có để DAO gọi được) ---

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public Timestamp getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Timestamp checkInDate) { this.checkInDate = checkInDate; }

    public Timestamp getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Timestamp checkOutDate) { this.checkOutDate = checkOutDate; }

    public BigDecimal getTotalDeposit() { return totalDeposit; }
    public void setTotalDeposit(BigDecimal totalDeposit) { this.totalDeposit = totalDeposit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}