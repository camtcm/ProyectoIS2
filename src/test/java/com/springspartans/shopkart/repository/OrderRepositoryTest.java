package com.springspartans.shopkart.repository;

import com.springspartans.shopkart.model.Order;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Suite: OrderRepository - Pruebas Módulo de Repositorio")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;

    private Order orderGuardado;

    @BeforeEach
    void setUp() { 
        Customer customer = new Customer();
        customer.setName("Juan Perez");
        customer.setEmail("juan@mail.com");
        customer.setPassword("pass123");
        customer.setAddress("Calle 123");
        customer.setPhone(987654321L);
        customer = customerRepository.save(customer);

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setBrand("Asus"); 
        product.setCategory("Electrónica");
        product.setStock(10);
        
        product = productRepository.save(product);

        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        
        Order order = new Order(
                0, 
                customer, 
                product, 
                2, 
                fechaActual, 
                null, 
                Order.OrderStatus.Pending, 
                2000.0
        );
        
        orderGuardado = orderRepository.save(order);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        orderGuardado = null;
    }

    @Test
    @DisplayName("save - Guardar Orden con datos válidos")
    void testSaveOrderValido() {
        assertNotNull(orderGuardado);
        assertTrue(orderGuardado.getId() > 0);
        assertEquals(2, orderGuardado.getQuantity());
        assertEquals(Order.OrderStatus.Pending, orderGuardado.getStatus());
        assertEquals(2000.0, orderGuardado.getTotal_amount());
    }

    @Test
    @DisplayName("findById - Buscar Orden con id existente")
    void testFindByIdExistente() {
        int idExistente = orderGuardado.getId();
        Optional<Order> resultado = orderRepository.findById(idExistente);
        assertTrue(resultado.isPresent());
        assertEquals(idExistente, resultado.get().getId());
    }

    @Test
    @DisplayName("findById - Buscar Orden con id inexistente")
    void testFindByIdInexistente() {
        int idInexistente = 999;
        Optional<Order> resultado = orderRepository.findById(idInexistente);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("deleteById - Eliminar Orden existente por id")
    void testDeleteById() {
        int idAEliminar = orderGuardado.getId();
        orderRepository.deleteById(idAEliminar);
        Optional<Order> resultado = orderRepository.findById(idAEliminar);
        assertTrue(resultado.isEmpty());
    }
}
