package com.springspartans.shopkart.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    
    @Column(nullable = false, length = 72)
    private String password;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Long phone;
    
    @Column(length = 50)
    private String profilePic;
    
    @Column(name = "signup_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp signupDate = Timestamp.from(Instant.now());
    
    @Column(name = "last_login_date", columnDefinition = "TIMESTAMP DEFAULT NULL")
    private Timestamp lastLoginDate;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Order> orders = new ArrayList<>();
    
    public Customer() {
    }

    public Customer(int id, String name, String email, String password, String address, Long phone, String profilePic,
            Timestamp signupDate, Timestamp lastLoginDate) {
        this.id = id;
        setName(name);
        setEmail(email);
        setPassword(password);
        setAddress(address);
        setPhone(phone);
        setProfilePic(profilePic);
        setSignupDate(signupDate);
        this.lastLoginDate = lastLoginDate;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.address = address;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        if (phone == null || phone <= 0) {
            throw new IllegalArgumentException("Phone cannot be null or invalid");
        }
        this.phone = phone;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Timestamp getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Timestamp signupDate) {
        if (signupDate == null) {
            this.signupDate = Timestamp.from(Instant.now());
        } else {
            this.signupDate = signupDate;
        }
    }

    public Timestamp getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Timestamp lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", address="
                + address + ", phone=" + phone + ", profilePic=" + profilePic + ", signupDate=" + signupDate
                + ", lastLoginDate=" + lastLoginDate + "]";
    }
}