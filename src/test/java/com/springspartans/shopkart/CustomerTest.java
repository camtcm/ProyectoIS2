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
        // Arrange
        int expectedId = 1;
        String expectedName = "Juan Perez";
        String expectedEmail = "juan@gmail.com";
        String expectedPassword = "123456";
        String expectedAddress = "Arequipa";
        Long expectedPhone = 987654321L;
        String expectedProfilePic = "perfil.jpg";

        // Act
        Customer result = customer;

        // Assert
        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals(expectedName, result.getName());
        assertEquals(expectedEmail, result.getEmail());
        assertEquals(expectedPassword, result.getPassword());
        assertEquals(expectedAddress, result.getAddress());
        assertEquals(expectedPhone, result.getPhone());
        assertEquals(expectedProfilePic, result.getProfilePic());
        assertEquals(signupDate, result.getSignup_date());
        assertNull(result.getLast_login_date());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        String expectedMessage = "Name cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, null, "correo@gmail.com", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Arrange
        String expectedMessage = "Name cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "", "correo@gmail.com", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Arrange
        String expectedMessage = "Email cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", null, "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Arrange
        String expectedMessage = "Email cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "", "123456", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Arrange
        String expectedMessage = "Password cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", null, "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        // Arrange
        String expectedMessage = "Password cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "", "Arequipa",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        // Arrange
        String expectedMessage = "Address cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", null,
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddressIsEmpty() {
        // Arrange
        String expectedMessage = "Address cannot be null or empty";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "",
                    987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        // Arrange
        String expectedMessage = "Phone cannot be null or invalid";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    null, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNegative() {
        // Arrange
        String expectedMessage = "Phone cannot be null or invalid";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    -987654321L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsZero() {
        // Arrange
        String expectedMessage = "Phone cannot be null or invalid";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Customer(1, "Juan Perez", "correo@gmail.com", "123456", "Arequipa",
                    0L, "perfil.jpg", signupDate, null);
        });

        // Assert
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldUpdateCustomerDataWithSetters() {
        // Arrange
        Customer otherCustomer = new Customer();

        // Act
        otherCustomer.setId(2);
        otherCustomer.setName("Maria Lopez");
        otherCustomer.setEmail("maria@gmail.com");
        otherCustomer.setPassword("abcdef");
        otherCustomer.setAddress("Lima");
        otherCustomer.setPhone(912345678L);
        otherCustomer.setProfilePic("maria.jpg");
        otherCustomer.setSignup_date(signupDate);
        otherCustomer.setLast_login_date(lastLoginDate);

        // Assert
        assertEquals(2, otherCustomer.getId());
        assertEquals("Maria Lopez", otherCustomer.getName());
        assertEquals("maria@gmail.com", otherCustomer.getEmail());
        assertEquals("abcdef", otherCustomer.getPassword());
        assertEquals("Lima", otherCustomer.getAddress());
        assertEquals(912345678L, otherCustomer.getPhone());
        assertEquals("maria.jpg", otherCustomer.getProfilePic());
        assertEquals(signupDate, otherCustomer.getSignup_date());
        assertEquals(lastLoginDate, otherCustomer.getLast_login_date());
    }

    @Test
    void shouldInitializeSignupDateInDefaultConstructor() {
        // Arrange y Act
        Customer newCustomer = new Customer();

        // Assert
        assertNotNull(newCustomer.getSignup_date());
    }

    @Test
    void shouldSetCurrentDateWhenSignupDateIsNull() {
        // Arrange
        Customer newCustomer = new Customer();

        // Act
        newCustomer.setSignup_date(null);

        // Assert
        assertNotNull(newCustomer.getSignup_date());
    }

    @Test
    void shouldReturnExpectedToStringFormat() {
        // Act
        String result = customer.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Customer"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name=Juan Perez"));
        assertTrue(result.contains("email=juan@gmail.com"));
        assertTrue(result.contains("address=Arequipa"));
        assertTrue(result.contains("phone=987654321"));
    }
}