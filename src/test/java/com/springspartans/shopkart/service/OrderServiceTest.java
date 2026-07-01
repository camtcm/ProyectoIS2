package com.springspartans.shopkart.service;

import com.springspartans.shopkart.cart.domain.CartItem;
import com.springspartans.shopkart.cart.application.CartItemService;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Order;
import com.springspartans.shopkart.model.Order.OrderStatus;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.repository.OrderRepository;
import com.springspartans.shopkart.repository.ProductRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite: OrderService - Pruebas Módulo de Servicio")
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1);
        customer.setName("Juan Perez");

        product = new Product();
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setBrand("Asus");

        order = new Order();
        order.setId(100);
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(2);
        order.setStatus(OrderStatus.Pending);
        order.setTotal_amount(2000.0);
    }

    @AfterEach
    void tearDown() {
        customer = null;
        product = null;
        order = null;
    }

    @Test
    @DisplayName("getOrdersOfLoggedInCustomer - Obtener órdenes del cliente logueado")
    void testGetOrdersOfLoggedInCustomer() {
        when(customerService.getCustomer()).thenReturn(customer);
        List<Order> list = new ArrayList<>();
        list.add(order);
        when(orderRepository.findByCustIdReverse(customer.getId())).thenReturn(list);

        List<Order> result = orderService.getOrdersOfLoggedInCustomer();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
    }

    @Test
    @DisplayName("getOrderById - Obtener orden existente por su ID")
    void testGetOrderByIdExistente() {
        when(orderRepository.findById(100)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(100);

        assertNotNull(result);
        assertEquals(100, result.getId());
    }

    @Test
    @DisplayName("getOrderById - Intentar obtener orden inexistente")
    void testGetOrderByIdInexistente() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(999);

        assertNull(result);
    }

    @Test
    @DisplayName("orderCartItem - Generar orden desde ítem del carrito con stock suficiente")
    void testOrderCartItemConStockSuficiente() {
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(cartItemService.getBySlno(1)).thenReturn(cartItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        int newOrderId = orderService.orderCartItem(1);

        assertEquals(100, newOrderId);
        assertEquals(8, product.getStock());
        verify(cartItemService, times(1)).deleteCartItem(1);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("cancelOrder - Cancelar orden pendiente y reponer stock del producto")
    void testCancelOrderPendiente() {
        when(orderRepository.findById(100)).thenReturn(Optional.of(order));

        orderService.cancelOrder(100);

        assertEquals(OrderStatus.Cancelled, order.getStatus());
        assertEquals(12, product.getStock());
        verify(orderRepository, times(1)).save(order);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("updateStatus - Cambiar estado de Pending a Shipped")
    void testUpdateStatusFromPendingToShipped() {
        order.setStatus(OrderStatus.Pending);
        when(orderRepository.findById(100)).thenReturn(Optional.of(order));

        orderService.updateStatus(100);

        assertEquals(OrderStatus.Shipped, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
