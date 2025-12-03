package controller;

import DAO.UserDAO;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        
        // Validate input
        StringBuilder errors = new StringBuilder();
        
        if (username == null || username.trim().isEmpty()) {
            errors.append("Tên đăng nhập không được để trống. ");
        } else if (username.length() < 4 || username.length() > 50) {
            errors.append("Tên đăng nhập phải từ 4-50 ký tự. ");
        }
        
        if (password == null || password.isEmpty()) {
            errors.append("Mật khẩu không được để trống. ");
        } else if (password.length() < 6) {
            errors.append("Mật khẩu phải có ít nhất 6 ký tự. ");
        }
        
        if (!password.equals(confirmPassword)) {
            errors.append("Mật khẩu xác nhận không khớp. ");
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.append("Họ tên không được để trống. ");
        }
        
        if (email == null || email.trim().isEmpty()) {
            errors.append("Email không được để trống. ");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("Email không hợp lệ. ");
        }
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!phoneNumber.matches("^[0-9]{10,11}$")) {
                errors.append("Số điện thoại không hợp lệ. ");
            }
        }
        
        // Check if username or email already exists
        if (errors.length() == 0) {
            if (userDAO.isUsernameExists(username.trim())) {
                errors.append("Tên đăng nhập đã tồn tại. ");
            }
            if (userDAO.isEmailExists(email.trim())) {
                errors.append("Email đã được sử dụng. ");
            }
        }
        
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phoneNumber", phoneNumber);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(password); // Will be hashed in DAO
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
        user.setRoleId(3); // Default role: Customer
        
        boolean success = userDAO.register(user);
        
        if (success) {
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Đăng ký thất bại. Vui lòng thử lại.");
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phoneNumber", phoneNumber);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
