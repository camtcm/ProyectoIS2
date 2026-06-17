package com.springspartans.shopkart.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springspartans.shopkart.model.Admin;
import com.springspartans.shopkart.repository.AdminRepository;
import com.springspartans.shopkart.util.PasswordEncoder;
import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(AdminRepository adminRepository,
                        HttpSession httpSession,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.httpSession = httpSession;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String email, String password, String security_key) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()
                && passwordEncoder.matches(password, admin.get().getPassword())
                && security_key.equals(admin.get().getSecurity_key())) {
            httpSession.setAttribute("loggedInAdmin", admin.get());
            return true;
        }
        return false;
    }

    public void logout() {
        httpSession.invalidate();
    }

    public Admin getAdmin() {
        return (Admin) httpSession.getAttribute("loggedInAdmin");
    }
}