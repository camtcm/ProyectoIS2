package com.springspartans.shopkart.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.Instant;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer();
        Product product = new Product();
        Timestamp now = Timestamp.from(Instant.now());

        Order order = new Order(
                1,
                customer,
                product,
                2,
                now,
                null,
                Order.OrderStatus.Pending,
                100.0
        );

        assertEquals(1, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(product, order.getProduct());
        assertEquals(2, order.getQuantity());
        assertEquals(now, order.getOrder_date());
        assertNull(order.getDelivered_date());
        assertEquals(Order.OrderStatus.Pending, order.getStatus());
        assertEquals(100.0, order.getTotal_amount());
    }

    @Test
    void testSetters() {
        Order order = new Order();
        Customer customer = new Customer();
        Product product = new Product();

        order.setId(5);
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(3);
        order.setTotal_amount(200.0);
        order.setStatus(Order.OrderStatus.Shipped);

        assertEquals(5, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(product, order.getProduct());
        assertEquals(3, order.getQuantity());
        assertEquals(200.0, order.getTotal_amount());
        assertEquals(Order.OrderStatus.Shipped, order.getStatus());
    }

    @Test
    void testDefaultValues() {
        Order order = new Order();

        assertNotNull(order.getOrder_date()); // se inicializa con Instant.now()
        assertEquals(Order.OrderStatus.Pending, order.getStatus());
    }

    @Test
    void testToString() {
        Order order = new Order();
        String result = order.toString();

        assertNotNull(result);
        assertTrue(result.contains("Order"));
    }
}
