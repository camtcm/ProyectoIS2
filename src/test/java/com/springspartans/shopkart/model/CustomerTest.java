package com.springspartans.shopkart.model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerTest {

    private Customer customer;
    private Timestamp signupDate;
    private Timestamp lastLoginDate;

    @BeforeEach
    void setUp() {
        signupDate = Timestamp.from(Instant.now());
        lastLoginDate = Timestamp.from(Instant.now());

        customer = new Customer(
                1,
                "Juan Perez",
                "juan@gmail.com",
                "123456",
                "Arequipa",
                987654321L,
                "perfil.jpg",
                signupDate,
                null
        );
    }

    @AfterEach
    void tearDown() {
        customer = null;
        signupDate = null;
        lastLoginDate = null;
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        int expectedId = 1;
        String expectedName = "Juan Perez";
        String expectedEmail = "juan@gmail.com";
        String expectedPassword = "123456";
        String expectedAddress = "Arequipa";
        Long expectedPhone = 987654321L;
        String expectedProfilePic = "perfil.jpg";

        Customer result = customer;

        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals(expectedName, result.getName());
        assertEquals(expectedEmail, result.getEmail());
        assertEquals(expectedPassword, result.getPassword());
        assertEquals(expectedAddress, result.getAddress());
        assertEquals(expectedPhone, result.getPhone());
        assertEquals(expectedProfilePic, result.getProfilePic());
        assertEquals(signupDate, result.getSignupDate());
        assertNull(result.getLastLoginDate());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        String expectedMessage = "Name cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, null, "correo@gmail.com", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        String expectedMessage = "Name cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "", "correo@gmail.com", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        String expectedMessage = "Email cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", null, "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        String expectedMessage = "Email cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        String expectedMessage = "Password cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", null, "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        String expectedMessage = "Password cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        String expectedMessage = "Address cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", null,
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddressIsEmpty() {
        String expectedMessage = "Address cannot be null or empty";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        String expectedMessage = "Phone cannot be null or invalid";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    null, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNegative() {
        String expectedMessage = "Phone cannot be null or invalid";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    -987654321L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsZero() {
        String expectedMessage = "Phone cannot be null or invalid";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    0L, "perfil.jpg", signupDate, null);
        });

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldUpdateCustomerDataWithSetters() {
        Customer otherCustomer = new Customer();

        otherCustomer.setId(2);
        otherCustomer.setName("Maria Lopez");
        otherCustomer.setEmail("maria@gmail.com");
        otherCustomer.setPassword("abcdef");
        otherCustomer.setAddress("Lima");
        otherCustomer.setPhone(912345678L);
        otherCustomer.setProfilePic("maria.jpg");
        otherCustomer.setSignupDate(signupDate);
        otherCustomer.setLastLoginDate(lastLoginDate);

        assertEquals(2, otherCustomer.getId());
        assertEquals("Maria Lopez", otherCustomer.getName());
        assertEquals("maria@gmail.com", otherCustomer.getEmail());
        assertEquals("abcdef", otherCustomer.getPassword());
        assertEquals("Lima", otherCustomer.getAddress());
        assertEquals(912345678L, otherCustomer.getPhone());
        assertEquals("maria.jpg", otherCustomer.getProfilePic());
        assertEquals(signupDate, otherCustomer.getSignupDate());
        assertEquals(lastLoginDate, otherCustomer.getLastLoginDate());
    }

    @Test
    void shouldInitializeSignupDateInDefaultConstructor() {
        Customer newCustomer = new Customer();

        assertNotNull(newCustomer.getSignupDate());
    }

    @Test
    void shouldSetCurrentDateWhenSignupDateIsNull() {
        Customer newCustomer = new Customer();

        newCustomer.setSignupDate(null);

        assertNotNull(newCustomer.getSignupDate());
    }

    @Test
    void shouldReturnExpectedToStringFormat() {
        String result = customer.toString();

        assertNotNull(result);
        assertTrue(result.contains("Customer"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name=Juan Perez"));
        assertTrue(result.contains("email=juan@gmail.com"));
        assertTrue(result.contains("address=Arequipa"));
        assertTrue(result.contains("phone=987654321"));
    }
}