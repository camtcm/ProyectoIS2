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

    private static final String LOGGED_IN_CUSTOMER_ID_ATTRIBUTE = "loggedInCustomerId";

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
        passwordEncoder = new PasswordEncoder();
        passwordValidator = new PasswordValidator();
        imageUploadValidator = new ImageUploadValidator();

        customerService = new CustomerService(
                "/tmp/test",
                customerRepository,
                httpSession,
                passwordEncoder,
                passwordValidator,
                imageUploadValidator
        );

        String encodedPassword = passwordEncoder.encode("Password123!");

        customer = Customer.builder()
                .id(1)
                .name("Juan Perez")
                .email("juan@gmail.com")
                .password(encodedPassword)
                .address("Arequipa")
                .phone(987654321L)
                .profilePic("perfil.jpg")
                .signupDate(Timestamp.from(Instant.now()))
                .lastLoginDate(null)
                .build();
    }

    @Test
    void shouldLoginSuccessfully() {
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        boolean result = customerService.login("juan@gmail.com", "Password123!");

        assertTrue(result);
        assertNotNull(customer.getLastLoginDate());
        verify(customerRepository).save(customer);
        verify(httpSession).setAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE, 1);
    }

    @Test
    void shouldReturnFalseWhenLoginEmailDoesNotExist() {
        when(customerRepository.findByEmail("noexiste@gmail.com"))
                .thenReturn(Optional.empty());

        boolean result = customerService.login("noexiste@gmail.com", "Password123!");

        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    void shouldReturnFalseWhenLoginPasswordIsWrong() {
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        boolean result = customerService.login("juan@gmail.com", "WrongPassword!");

        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    @Test
    void shouldSignupSuccessfully() throws InvalidPasswordException {
        Customer newCustomer = Customer.builder()
                .id(2)
                .name("Maria Lopez")
                .email("maria@gmail.com")
                .password("Password123!")
                .address("Lima")
                .phone(912345678L)
                .profilePic("maria.jpg")
                .signupDate(Timestamp.from(Instant.now()))
                .lastLoginDate(null)
                .build();

        when(customerRepository.findByEmail("maria@gmail.com"))
                .thenReturn(Optional.empty());

        boolean result = customerService.signup(newCustomer);

        assertTrue(result);
        assertNotEquals("Password123!", newCustomer.getPassword());
        assertNotNull(newCustomer.getSignupDate());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    void shouldReturnFalseWhenSignupEmailAlreadyExists() throws InvalidPasswordException {
        when(customerRepository.findByEmail("juan@gmail.com"))
                .thenReturn(Optional.of(customer));

        boolean result = customerService.signup(customer);

        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenSignupPasswordIsInvalid() {
        Customer newCustomer = Customer.builder()
                .id(3)
                .name("Carlos Ramos")
                .email("carlos@gmail.com")
                .password("123")
                .address("Cusco")
                .phone(923456789L)
                .profilePic("carlos.jpg")
                .signupDate(Timestamp.from(Instant.now()))
                .lastLoginDate(null)
                .build();

        when(customerRepository.findByEmail("carlos@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidPasswordException.class, () -> {
            customerService.signup(newCustomer);
        });

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCustomerFromSession() {
        when(httpSession.getAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE))
                .thenReturn(1);
        when(customerRepository.findById(1))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomer();

        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@gmail.com", result.getEmail());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        when(httpSession.getAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE))
                .thenReturn(1);
        when(customerRepository.findById(1))
                .thenReturn(Optional.of(customer));

        boolean result = customerService.updateCustomer(
                "Juan Actualizado", 999999999L, "Lima",
                "Password123!", "Password123!", null
        );

        assertTrue(result);
        verify(customerRepository).save(any(Customer.class));
        verify(httpSession).setAttribute(eq(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE), eq(1));
    }

    @Test
    void shouldReturnFalseWhenUpdateOldPasswordIsWrong() throws Exception {
        when(httpSession.getAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE))
                .thenReturn(1);
        when(customerRepository.findById(1))
                .thenReturn(Optional.of(customer));

        boolean result = customerService.updateCustomer(
                "Juan", 999999999L, "Lima",
                "", "ContrasenaIncorrecta", null
        );

        assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateNewPasswordIsInvalid() {
        when(httpSession.getAttribute(LOGGED_IN_CUSTOMER_ID_ATTRIBUTE))
                .thenReturn(1);
        when(customerRepository.findById(1))
                .thenReturn(Optional.of(customer));

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
        when(customerRepository.findAll())
                .thenReturn(List.of(customer));

        var result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
    }

    @Test
    void shouldDeleteCustomer() {
        customerService.deleteCustomer(1);

        verify(customerRepository).deleteById(1);
    }

    @Test
    void shouldCountCustomers() {
        when(customerRepository.count()).thenReturn(5L);

        int total = customerService.countCustomers();

        assertEquals(5, total);
    }

    @Test
    void shouldCountSignupByDate() {
        Timestamp date = Timestamp.from(Instant.now());
        when(customerRepository.countBySignupDateBetween(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(3);

        int result = customerService.countSignupByDate(date);

        assertEquals(3, result);
    }

    @Test
    void shouldCountLoginByDate() {
        Timestamp date = Timestamp.from(Instant.now());
        when(customerRepository.countByLastLoginDateBetween(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(2);

        int result = customerService.countLoginByDate(date);

        assertEquals(2, result);
    }

    @Test
    void shouldLogoutSuccessfully() {
        customerService.logout();

        verify(httpSession).invalidate();
    }
}