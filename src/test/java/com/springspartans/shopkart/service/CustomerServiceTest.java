package com.springspartans.shopkart.service;

import com.springspartans.shopkart.exception.InvalidPasswordException;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.repository.CustomerRepository;
import com.springspartans.shopkart.util.ImageUploadValidator;
import com.springspartans.shopkart.util.PasswordEncoder;
import com.springspartans.shopkart.util.PasswordValidator;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private HttpSession httpSession;

    private CustomerService customerService;
    private PasswordEncoder passwordEncoder;
    private PasswordValidator passwordValidator;
    private ImageUploadValidator imageUploadValidator;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();

        passwordEncoder = new PasswordEncoder();
        passwordValidator = new PasswordValidator();
        imageUploadValidator = new ImageUploadValidator();

        ReflectionTestUtils.setField(customerService, "customerRepository", customerRepository);
        ReflectionTestUtils.setField(customerService, "httpSession", httpSession);
        ReflectionTestUtils.setField(customerService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(customerService, "passwordValidator", passwordValidator);
        ReflectionTestUtils.setField(customerService, "imageUploadValidator", imageUploadValidator);
        ReflectionTestUtils.setField(customerService, "uploadPath", "/tmp/test");

        String encodedPassword = passwordEncoder.encode("Password123!");

        customer = new Customer(
                1,
                "Juan Perez",
                "juan@gmail.com",
                encodedPassword,
                "Arequipa",
                987654321L,
                "perfil.jpg",
                Timestamp.from(Instant.now()),
                null
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        // Act
        boolean result = customerService.login("juan@gmail.com", "Password123!");

        // Assert
        assertTrue(result);
        assertNotNull(customer.getLast_login_date());
        verify(customerRepository).save(customer);
        verify(httpSession).setAttribute("loggedInCustomer", customer);
    }

    @Test
    void shouldReturnFalseWhenLoginEmailDoesNotExist() {
        // Arrange
        when(customerRepository.findByEmail("noexiste@gmail.com"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = customerService.login("noexiste@gmail.com", "Password123!");

        // Assert
        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    void shouldReturnFalseWhenLoginPasswordIsWrong() {
        // Arrange
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        // Act
        boolean result = customerService.login("juan@gmail.com", "WrongPassword!");

        // Assert
        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    void shouldSignupSuccessfully() throws InvalidPasswordException {
        // Arrange
        Customer newCustomer = new Customer(
                2,
                "Maria Lopez",
                "maria@gmail.com",
                "Password123!",
                "Lima",
                912345678L,
                "maria.jpg",
                Timestamp.from(Instant.now()),
                null
        );

        when(customerRepository.findByEmail("maria@gmail.com"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = customerService.signup(newCustomer);

        // Assert
        assertTrue(result);
        assertNotEquals("Password123!", newCustomer.getPassword());
        assertNotNull(newCustomer.getSignup_date());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    void shouldReturnFalseWhenSignupEmailAlreadyExists() throws InvalidPasswordException {
        // Arrange
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        // Act
        boolean result = customerService.signup(customer);

        // Assert
        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenSignupPasswordIsInvalid() {
        // Arrange
        Customer newCustomer = new Customer(
                3,
                "Carlos Ramos",
                "carlos@gmail.com",
                "123",
                "Cusco",
                923456789L,
                "carlos.jpg",
                Timestamp.from(Instant.now()),
                null
        );

        when(customerRepository.findByEmail("carlos@gmail.com"))
                .thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(InvalidPasswordException.class, () -> {
            customerService.signup(newCustomer);
        });

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCustomerFromSession() {
        // Arrange
        when(httpSession.getAttribute("loggedInCustomer"))
                .thenReturn(customer);

        // Act
        Customer result = customerService.getCustomer();

        // Assert
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@gmail.com", result.getEmail());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        // Arrange
        when(httpSession.getAttribute("loggedInCustomer"))
                .thenReturn(customer);

        // Act
        boolean result = customerService.updateCustomer(
                "Juan Actualizado", 999999999L, "Lima",
                "Password123!", "Password123!", null
        );

        // Assert
        assertTrue(result);
        verify(customerRepository).save(any(Customer.class));
        verify(httpSession).setAttribute(eq("loggedInCustomer"), any(Customer.class));
    }

    @Test
    void shouldReturnFalseWhenUpdateOldPasswordIsWrong() throws Exception {
        // Arrange
        when(httpSession.getAttribute("loggedInCustomer"))
                .thenReturn(customer);

        // Act
        boolean result = customerService.updateCustomer(
                "Juan", 999999999L, "Lima",
                "", "ContrasenaIncorrecta", null
        );

        // Assert
        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateNewPasswordIsInvalid() {
        // Arrange
        when(httpSession.getAttribute("loggedInCustomer"))
                .thenReturn(customer);

        // Act y Assert
        assertThrows(InvalidPasswordException.class, () -> {
            customerService.updateCustomer(
                    "Juan", 999999999L, "Lima",
                    "123", "Password123!", null
            );
        });

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetAllCustomers() {
        // Arrange
        when(customerRepository.findAll())
                .thenReturn(List.of(customer));

        // Act
        var result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
    }

    @Test
    void shouldDeleteCustomer() {
        // Act
        customerService.deleteCustomer(1);

        // Assert
        verify(customerRepository).deleteById(1);
    }

    @Test
    void shouldCountCustomers() {
        // Arrange
        when(customerRepository.count()).thenReturn(5L);

        // Act
        int total = customerService.countCustomers();

        // Assert
        assertEquals(5, total);
    }

    @Test
    void shouldCountSignupByDate() {
        // Arrange
        Timestamp date = Timestamp.from(Instant.now());
        when(customerRepository.countBySignup_dateBetween(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(3);

        // Act
        int result = customerService.countSignupByDate(date);

        // Assert
        assertEquals(3, result);
    }

    @Test
    void shouldCountLoginByDate() {
        // Arrange
        Timestamp date = Timestamp.from(Instant.now());
        when(customerRepository.countByLast_login_dateBetween(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(2);

        // Act
        int result = customerService.countLoginByDate(date);

        // Assert
        assertEquals(2, result);
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Act
        customerService.logout();

        // Assert
        verify(httpSession).invalidate();
    }
}