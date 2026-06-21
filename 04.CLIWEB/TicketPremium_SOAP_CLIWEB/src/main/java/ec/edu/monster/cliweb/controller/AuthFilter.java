package ec.edu.monster.cliweb.controller;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/comprar", "/checkout", "/paises", "/reporte", "/masup", "/index.jsp", "/views/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        String loginURI = req.getContextPath() + "/login";

        boolean loggedIn = session != null && session.getAttribute("usuarioLogueado") != null;
        boolean loginRequest = req.getRequestURI().equals(loginURI) || req.getRequestURI().contains("login.jsp");

        if (loggedIn || loginRequest) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
    }
}
