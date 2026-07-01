package com.springspartans.shopkart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.cart.domain.CartItem;
import com.springspartans.shopkart.cart.infrastructure.CartItemRepository;
import com.springspartans.shopkart.repository.ProductRepository;
import com.springspartans.shopkart.cart.application.CartItemService;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartRepo;

    @Mock
    private ProductRepository prodRepo;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CartItemService cartItemService;

    private Customer customer;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1);
        customer.setName("John Doe");
        customer.setEmail("john@test.com");

        product = new Product();
        product.setId(1);
        product.setName("Laptop");
        product.setPrice(1500.0);
        product.setDiscount(10.0);
        product.setStock(5);

        cartItem = new CartItem();
        cartItem.setSlno(1);
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }

    @Test
    @DisplayName("Obtener items del carrito del cliente")
    void getAllCartItems() {
        // Arrange
        when(customerService.getCustomer()).thenReturn(customer);
        List<CartItem> expected = new ArrayList<>();
        expected.add(cartItem);
        when(cartRepo.findByCustId(1)).thenReturn(expected);

        // Act
        List<CartItem> result = cartItemService.getAllCartItems();

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Obtener item por número de serie")
    void getBySlno() {
        // Arrange
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        CartItem result = cartItemService.getBySlno(1);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Obtener todos los items de un cliente")
    void getAllCartItemsForCustomer() {
        // Arrange
        List<CartItem> expected = new ArrayList<>();
        expected.add(cartItem);
        when(cartRepo.findByCustId(1)).thenReturn(expected);

        // Act
        List<CartItem> result = cartItemService.getAllCartItemsforCustomer(1);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Incrementar cantidad cuando hay stock disponible")
    void incrementQuantity_WithStock() {
        // Arrange
        product.setStock(5);
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));
        when(cartRepo.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        boolean result = cartItemService.incrementQuantity(1);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("No incrementar cantidad sin stock")
    void incrementQuantity_NoStock() {
        // Arrange
        product.setStock(0);
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        boolean result = cartItemService.incrementQuantity(1);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Decrementar cantidad cuando es mayor a 1")
    void decrementQuantity_MoreThanOne() {
        // Arrange
        cartItem.setQuantity(2);
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        cartItemService.decrementQuantity(1);

        // Assert
        verify(cartRepo, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Eliminar item cuando cantidad es 1 al decrementar")
    void decrementQuantity_LastItem() {
        // Arrange
        cartItem.setQuantity(1);
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        cartItemService.decrementQuantity(1);

        // Assert
        verify(cartRepo, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Eliminar item del carrito")
    void deleteCartItem() {
        // Arrange
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        cartItemService.deleteCartItem(1);

        // Assert
        verify(cartRepo, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Obtener precio de un item del carrito")
    void getCartItemPrice() {
        // Arrange
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        double result = cartItemService.getCartItemPrice(1);

        // Assert
        assertThat(result).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("Calcular precio total del carrito con items")
    void getCartPrice_WithItems() {
        // Arrange
        when(customerService.getCustomer()).thenReturn(customer);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        when(cartRepo.findByCustId(1)).thenReturn(items);

        // Act
        cartItemService.getAllCartItems();
        double result = cartItemService.getCartPrice();

        // Assert
        assertThat(result).isPositive();
    }

    @Test
    @DisplayName("Precio total cero sin cliente")
    void getCartPrice_NoCustomer() {
        // Arrange
        when(customerService.getCustomer()).thenReturn(null);

        // Act
        cartItemService.getAllCartItems();
        double result = cartItemService.getCartPrice();

        // Assert
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Agregar producto al carrito con stock disponible")
    void addToCart_Success() {
        // Arrange
        product.setStock(10);
        when(prodRepo.findById(1)).thenReturn(Optional.of(product));
        when(cartRepo.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        cartItemService.addToCart(1, customer);

        // Assert
        verify(cartRepo, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("No agregar producto inexistente al carrito")
    void addToCart_ProductNotFound() {
        // Arrange
        when(prodRepo.findById(1)).thenReturn(Optional.empty());

        // Act
        cartItemService.addToCart(1, customer);

        // Assert
        verify(cartRepo, times(0)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("No agregar producto sin stock al carrito")
    void addToCart_NoStock() {
        // Arrange
        product.setStock(0);
        when(prodRepo.findById(1)).thenReturn(Optional.of(product));

        // Act
        cartItemService.addToCart(1, customer);

        // Assert
        verify(cartRepo, times(0)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Limpiar el carrito del cliente")
    void clearCart() {
        // Arrange
        when(customerService.getCustomer()).thenReturn(customer);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        when(cartRepo.findByCustId(1)).thenReturn(items);
        when(cartRepo.findById(1)).thenReturn(Optional.of(cartItem));

        // Act
        cartItemService.getAllCartItems();
        cartItemService.clearCart();

        // Assert
        verify(cartRepo, times(1)).deleteById(anyInt());
    }
}
