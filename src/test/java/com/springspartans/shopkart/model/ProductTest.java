package com.springspartans.shopkart.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product Entity - Domain Invariant Tests")
class ProductTest {

    @Test
    @DisplayName("Should create a product when all values are valid")
    void shouldCreateProductWhenValuesAreValid() {

        // Arrange & Act
        Product product = new Product(1, new ProductDetails("Mouse", "Electronics", "Logitech", 25.5, "img", 10, 0.0));

        // Assert
        assertNotNull(product);
        assertEquals("Mouse", product.getName());
        assertEquals(25.5, product.getPrice());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(1, new ProductDetails(null, "Electronics", "Logitech", 25.5, "img", 10, 0.0));
        });
    }

    @Test
    @DisplayName("Should throw exception when category is null")
    void shouldThrowExceptionWhenCategoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(1, new ProductDetails("Mouse", null, "Logitech", 25.5, "img", 10, 0.0));
        });
    }

    @Test
    @DisplayName("Should throw exception when brand is null")
    void shouldThrowExceptionWhenBrandIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(1, new ProductDetails("Mouse", "Electronics", null, 25.5, "img", 10, 0.0));
        });
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(1, new ProductDetails("Mouse", "Electronics", "Logitech", -50.0, "img", 10, 0.0));
        });
    }

    @Test
    @DisplayName("Should throw exception when stock is negative")
    void shouldThrowExceptionWhenStockIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(1, new ProductDetails("Mouse", "Electronics", "Logitech", 25.5, "img", -10, 0.0));
        });
    }

    @Test
    @DisplayName("Should return non-null string representation")
    void shouldReturnNonNullStringWhenToStringIsCalled() {

        // Arrange
        Product product = new Product(1, new ProductDetails("Mouse", "Electronics", "Logitech", 25.5, "img", 10, 0.0));

        // Act
        String result = product.toString();

        // Assert
        assertNotNull(result, "toString should not return null.");
        assertTrue(result.contains("Mouse"));
    }
}
