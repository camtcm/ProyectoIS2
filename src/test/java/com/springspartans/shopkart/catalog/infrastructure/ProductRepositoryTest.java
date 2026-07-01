package com.springspartans.shopkart.catalog.infrastructure;

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

import com.springspartans.shopkart.catalog.domain.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.show-sql=false"
})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product p1;
    private Product p2;
    private Product p3;

    @BeforeEach
    void setUp() {
        p1 = new Product();
        p1.setName("Laptop");
        p1.setCategory("Electrónica");
        p1.setBrand("TechBrand");
        p1.setPrice(1500.00);
        p1.setStock(10);
        entityManager.persist(p1);

        p2 = new Product();
        p2.setName("Lampara LED");
        p2.setCategory("Hogar");
        p2.setBrand("HomeLight");
        p2.setPrice(25.00);
        p2.setStock(50);
        entityManager.persist(p2);

        p3 = new Product();
        p3.setName("Mouse Gamer");
        p3.setCategory("Electrónica");
        p3.setBrand("LogiTech");
        p3.setPrice(70.00);
        p3.setStock(100);
        entityManager.persist(p3);

        entityManager.flush();
    }

    @Test
    void shouldFindProductsByCategory() {
        List<Product> result = productRepository.findByCategory("Electrónica");
        assertThat(result).hasSize(2).extracting(Product::getCategory).containsOnly("Electrónica");
    }

    @Test
    void shouldReturnEmptyWhenCategoryDoesNotExist() {
        assertThat(productRepository.findByCategory("Inexistente")).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenCategoryIsNull() {
        assertThat(productRepository.findByCategory(null)).isEmpty();
    }

    @Test
    void shouldFindProductsByStartName() {
        List<Product> result = productRepository.findByStartName("La");
        assertThat(result).isNotEmpty().extracting(Product::getName).allMatch(name -> name.startsWith("La"));
    }

    @Test
    void shouldReturnSingleProductByPrefix() {
        assertThat(productRepository.findByStartName("Lap")).hasSize(1);
    }

    @Test
    void shouldReturnAllProductsWhenPrefixIsEmpty() {
        assertThat(productRepository.findByStartName("")).hasSize(3);
    }

    @Test
    void shouldReturnDistinctCategories() {
        List<String> result = productRepository.findAllCategories();
        assertThat(result).contains("Electrónica", "Hogar").doesNotHaveDuplicates();
    }

    @Test
    void shouldNotReturnNullCategories() {
        assertThat(productRepository.findAllCategories()).doesNotContainNull();
    }

    @AfterEach
    void tearDown() {
        p1 = null;
        p2 = null;
        p3 = null;
    }
}
