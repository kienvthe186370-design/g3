package filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        // Allow access to public resources
        boolean isPublicResource = requestURI.endsWith("/login") || 
                                   requestURI.endsWith("/login.html") ||
                                   requestURI.endsWith("/register") ||
                                   requestURI.endsWith("/register.html") ||
                                   requestURI.endsWith("/login.jsp") ||
                                   requestURI.endsWith("/register.jsp") ||
                                   requestURI.endsWith(".html") ||  // Allow all HTML files
                                   requestURI.contains("/css/") ||
                                   requestURI.contains("/js/") ||
                                   requestURI.contains("/img/") ||
                                   requestURI.contains("/fonts/") ||
                                   requestURI.equals(contextPath + "/") ||
                                   requestURI.equals(contextPath + "/index.jsp");
        
        if (isLoggedIn || isPublicResource) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }
    
    @Override
    public void destroy() {
    }
}
