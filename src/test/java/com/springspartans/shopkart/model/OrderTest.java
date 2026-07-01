package com.springspartans.shopkart.model;

import com.springspartans.shopkart.catalog.domain.Product;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer();
        Product product = new Product();

        Timestamp now = new Timestamp(System.currentTimeMillis());

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
        Timestamp now = new Timestamp(System.currentTimeMillis());

        order.setId(5);
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(10);
        order.setOrder_date(now);
        order.setDelivered_date(now);
        order.setStatus(Order.OrderStatus.Delivered);
        order.setTotal_amount(250.5);

        assertEquals(5, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(product, order.getProduct());
        assertEquals(10, order.getQuantity());
        assertEquals(now, order.getOrder_date());
        assertEquals(now, order.getDelivered_date());
        assertEquals(Order.OrderStatus.Delivered, order.getStatus());
        assertEquals(250.5, order.getTotal_amount());
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

        assertTrue(result.contains("Order"));
    }

    @Test
    void testStatusEnumValues() {
        assertEquals("Pending", Order.OrderStatus.Pending.name());
        assertEquals("Shipped", Order.OrderStatus.Shipped.name());
        assertEquals("Delivered", Order.OrderStatus.Delivered.name());
        assertEquals("Cancelled", Order.OrderStatus.Cancelled.name());
    }
}
