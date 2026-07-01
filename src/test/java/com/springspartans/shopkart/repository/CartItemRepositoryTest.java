package com.springspartans.shopkart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.cart.domain.CartItem;
import com.springspartans.shopkart.cart.infrastructure.CartItemRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false"
})
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("Laptop");
        product.setCategory("Electrónica");
        product.setBrand("TechBrand");
        product.setPrice(1500.00);
        product.setStock(10);
        entityManager.persist(product);

        customer = new Customer();
        customer.setName("Cliente Principal");
        customer.setEmail("cliente@test.com");
        customer.setPassword("password123");
        customer.setAddress("Avenida Central 123");
        customer.setPhone(987654321L);
        entityManager.persist(customer);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCustomer(customer);
        item2.setProduct(product);
        item2.setQuantity(1);

        cartItemRepository.save(item1);
        cartItemRepository.save(item2);
    }

    @Test
    @DisplayName("Debe retornar exactamente la cantidad que tenga el cliente")
    void findByCustId_ExactCartItems() {
        // Arrange
        int targetCustId = customer.getId();

        // Act
        List<CartItem> result = cartItemRepository.findByCustId(targetCustId);

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe retornar únicamente los que pertenezcan al cliente")
    void findByCustId_ItemsBelongingToCustomer() {
        // Arrange
        int targetCustId = customer.getId();

        // Act
        List<CartItem> result = cartItemRepository.findByCustId(targetCustId);

        // Assert
        assertThat(result)
                .extracting(item -> item.getCustomer().getId())
                .containsOnly(targetCustId);
    }

    @Test
    @DisplayName("Debe retornar una lista vacía si el cliente no tiene items")
    void findByCustId_EmptyList() {
        // Act
        List<CartItem> result = cartItemRepository.findByCustId(9999);

        // Assert
        assertThat(result).isEmpty();
    }

    @AfterEach
    void tearDown() {
        customer = null;
        product = null;
    }
}