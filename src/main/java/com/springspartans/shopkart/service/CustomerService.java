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

        if (!canUpdateCustomer(loggedInCustomer, oldPassword)) {
            return false;
        }

        validateNewPassword(newPassword);

        String encodedPassword = resolvePassword(newPassword, loggedInCustomer);
        String profilePictureName = resolveProfilePictureName(loggedInCustomer, profilePicture);

        Customer updatedCustomer = buildUpdatedCustomer(
                loggedInCustomer,
                newName,
                newPhone,
                newAddress,
                encodedPassword,
                profilePictureName
        );

        customerRepository.save(updatedCustomer);
        httpSession.setAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE, updatedCustomer.getId());

        saveProfilePictureIfPresent(profilePicture, profilePictureName);

        return true;
    }

    private boolean canUpdateCustomer(Customer loggedInCustomer, String oldPassword) {
        return loggedInCustomer != null && passwordEncoder.matches(oldPassword, loggedInCustomer.getPassword());
    }

    private void validateNewPassword(String newPassword) throws InvalidPasswordException {
        if (!isEmpty(newPassword) && !passwordValidator.isValidPassword(newPassword)) {
            throw new InvalidPasswordException("Invalid password entered!");
        }
    }

    private String resolvePassword(String newPassword, Customer loggedInCustomer) {
        if (isEmpty(newPassword)) {
            return loggedInCustomer.getPassword();
        }
        return passwordEncoder.encode(newPassword);
    }

    private String resolveProfilePictureName(Customer loggedInCustomer, MultipartFile profilePicture) {
        if (!hasProfilePicture(profilePicture)) {
            return null;
        }
        return "user" + loggedInCustomer.getId() + ".jpg";
    }

    private Customer buildUpdatedCustomer(
            Customer loggedInCustomer,
            String newName,
            long newPhone,
            String newAddress,
            String encodedPassword,
            String profilePictureName
    ) {
        return Customer.builder()
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
    }

    private void saveProfilePictureIfPresent(MultipartFile profilePicture, String profilePictureName)
            throws IOException, InvalidImageUploadException {
        if (!hasProfilePicture(profilePicture)) {
            return;
        }

        validateProfilePicture(profilePicture);

        File destination = getCustomerUploadDirectory();
        File fileToSave = new File(destination, profilePictureName);

        profilePicture.transferTo(fileToSave);
        LOGGER.info("Saved file: {}", fileToSave.getAbsolutePath());
    }

    private void validateProfilePicture(MultipartFile profilePicture) throws InvalidImageUploadException {
        if (!imageUploadValidator.isValidImage(profilePicture)) {
            throw new InvalidImageUploadException("Improper file format!");
        }
    }

    private File getCustomerUploadDirectory() throws IOException {
        File destination = new File(uploadPath + "/customer");

        createDirectoryIfNeeded(destination);
        makeDirectoryWritable(destination);

        return destination;
    }

    private void createDirectoryIfNeeded(File destination) throws IOException {
        if (!destination.exists() && !destination.mkdirs()) {
            throw new IOException("Could not create upload directory: " + destination.getAbsolutePath());
        }
    }

    private void makeDirectoryWritable(File destination) {
        boolean writable = destination.setWritable(true);
        if (!writable) {
            LOGGER.warn("Could not set writable permission for directory: {}", destination.getAbsolutePath());
        }
    }

    private boolean hasProfilePicture(MultipartFile profilePicture) {
        return profilePicture != null && !profilePicture.isEmpty();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
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