package com.springspartans.shopkart.model;

import com.springspartans.shopkart.cart.domain.CartItem;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.Instant;

class CartItemTest {

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        product = new Product();
    }

    @AfterEach
    void tearDown() {
        customer = null;
        product = null;
    }

    @DisplayName("Successfully create CartItem")
    @Test
    void testCreateSuccess() {
        // Arrange
        int quantity = 2;

        // Act
        CartItem item = new CartItem(customer, product, quantity);

        // Assert
        assertNotNull(item);
        assertEquals(quantity, item.getQuantity());
        assertNotNull(item.getAddedDate());
    }

    @DisplayName("Throw exception when customer is null")
    @Test
    void testNullCustomer() {
        // Arrange
        int quantity = 1;
        String message = "Customer cannot be null";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new CartItem(null, product, quantity);
        });

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @DisplayName("Throw exception when product is null")
    @Test
    void testNullProduct() {
        // Arrange
        int quantity = 1;
        String message = "Product cannot be null";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new CartItem(customer, null, quantity);
        });

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @DisplayName("Throw exception when quantity is zero")
    @Test
    void testZeroQuantity() {
        // Arrange
        int quantity = 0;
        String message = "Quantity  must be positive";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new CartItem(customer, product, quantity);
        });

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @DisplayName("Throw exception when quantity is negative")
    @Test
    void testNegativeQuantity() {
        // Arrange
        int quantity = -5;
        String message = "Quantity  must be positive";

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new CartItem(customer, product, quantity);
        });

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @DisplayName("Validate toString method format")
    @Test
    void testToString() {
        // Arrange
        CartItem cartItem = new CartItem();
        Timestamp now = Timestamp.from(Instant.now());
        int slno = 1;
        int quantity = 5;

        cartItem.setSlno(slno);
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setAddedDate(now);
        String expectedString = "CartItem [slno=" + slno + ", customer=" + customer.toString() +
                ", product=" + product.toString() +
                ", quantity=" + quantity + ", addedDate=" + now.toString() + "]";

        // Act
        String actualString = cartItem.toString();

        // Assert
        assertEquals(expectedString, actualString);
    }

}