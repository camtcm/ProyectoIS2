package com.springspartans.shopkart.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import com.springspartans.shopkart.exception.InvalidImageUploadException;
import com.springspartans.shopkart.exception.InvalidPasswordException;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.repository.CustomerRepository;
import com.springspartans.shopkart.util.ImageUploadValidator;
import com.springspartans.shopkart.util.PasswordEncoder;
import com.springspartans.shopkart.util.PasswordValidator;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private static final String LOGGED_IN_CUSTOMER_ID_ATTRIBUTE = "loggedInCustomerId";

    private final String uploadPath;
    private final CustomerRepository customerRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final ImageUploadValidator imageUploadValidator;

    public CustomerService(
            String uploadPath,
            CustomerRepository customerRepository,
            HttpSession httpSession,
            PasswordEncoder passwordEncoder,
            PasswordValidator passwordValidator,
            ImageUploadValidator imageUploadValidator
    ) {
        this.uploadPath = uploadPath;
        this.customerRepository = customerRepository;
        this.httpSession = httpSession;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.imageUploadValidator = imageUploadValidator;
    }

    public boolean login(String email, String password) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getPassword())) {
            Customer loggedInCustomer = customer.get();
            loggedInCustomer.setLastLoginDate(Timestamp.from(Instant.now()));
            customerRepository.save(loggedInCustomer);
            httpSession.setAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE, loggedInCustomer.getId());
            return true;
        }
        return false;
    }

    public boolean signup(Customer customer) throws InvalidPasswordException {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            return false;
        }
        if (!passwordValidator.isValidPassword(customer.getPassword())) {
            throw new InvalidPasswordException("Invalid password entered!");
        }
        customer.setSignupDate(Timestamp.from(Instant.now()));
        LOGGER.info("Signup Date: {}", customer.getSignupDate());
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        return true;
    }

    public Customer getCustomer() {
        Integer customerId = (Integer) httpSession.getAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE);
        if (customerId == null) {
            return null;
        }
        return customerRepository.findById(customerId).orElse(null);
    }

    public boolean updateCustomer(
            String newName, long newPhone, String newAddress,
            String newPassword, String oldPassword, MultipartFile profilePicture
    ) throws IOException, InvalidPasswordException, InvalidImageUploadException {
        Customer loggedInCustomer = getCustomer();

        if (loggedInCustomer != null && passwordEncoder.matches(oldPassword, loggedInCustomer.getPassword())) {
            if (!newPassword.isEmpty() && !passwordValidator.isValidPassword(newPassword)) {
                throw new InvalidPasswordException("Invalid password entered!");
            }

            String encodedPassword = newPassword.isEmpty()
                    ? loggedInCustomer.getPassword()
                    : passwordEncoder.encode(newPassword);

            String profilePictureName = null;
            if (profilePicture != null && !profilePicture.isEmpty()) {
                profilePictureName = "user" + loggedInCustomer.getId() + ".jpg";
            }

            Customer updatedCustomer = Customer.builder()
                    .id(loggedInCustomer.getId())
                    .name(newName)
                    .email(loggedInCustomer.getEmail())
                    .password(encodedPassword)
                    .address(newAddress)
                    .phone(newPhone)
                    .profilePic(profilePictureName)
                    .signupDate(loggedInCustomer.getSignupDate())
                    .lastLoginDate(loggedInCustomer.getLastLoginDate())
                    .build();

            customerRepository.save(updatedCustomer);
            httpSession.setAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE, updatedCustomer.getId());

            if (profilePicture != null && !profilePicture.isEmpty()) {
                if (!imageUploadValidator.isValidImage(profilePicture)) {
                    throw new InvalidImageUploadException("Improper file format!");
                }

                String customerUploadPath = uploadPath + "/customer";
                File destination = new File(customerUploadPath);

                if (!destination.exists() && !destination.mkdirs()) {
                    throw new IOException("Could not create upload directory: " + destination.getAbsolutePath());
                }

                boolean writable = destination.setWritable(true);
                if (!writable) {
                    LOGGER.warn("Could not set writable permission for directory: {}", destination.getAbsolutePath());
                }

                File fileToSave = new File(destination, profilePictureName);
                profilePicture.transferTo(fileToSave);
                LOGGER.info("Saved file: {}", fileToSave.getAbsolutePath());
            }

            return true;
        }

        return false;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(int customerId) {
        customerRepository.deleteById(customerId);
    }

    public int countCustomers() {
        return (int) customerRepository.count();
    }

    public int countSignupByDate(Timestamp date) {
        try {
            LocalDate localDate = date.toLocalDateTime().toLocalDate();
            Timestamp startTimestamp = Timestamp.valueOf(localDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(localDate.atTime(23, 59, 59));
            return customerRepository.countBySignupDateBetween(startTimestamp, endTimestamp);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    public int countLoginByDate(Timestamp date) {
        try {
            LocalDate localDate = date.toLocalDateTime().toLocalDate();
            Timestamp startTimestamp = Timestamp.valueOf(localDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(localDate.atTime(23, 59, 59));
            return customerRepository.countByLastLoginDateBetween(startTimestamp, endTimestamp);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    public void logout() {
        httpSession.invalidate();
    }
}