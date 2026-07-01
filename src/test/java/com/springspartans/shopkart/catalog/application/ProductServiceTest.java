package com.springspartans.shopkart.catalog.application;

import com.springspartans.shopkart.catalog.domain.Product;
import com.springspartans.shopkart.catalog.domain.ProductDetails;
import com.springspartans.shopkart.catalog.infrastructure.ProductRepository;
import com.springspartans.shopkart.exception.InvalidImageUploadException;
import com.springspartans.shopkart.util.ImageUploadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ImageUploadValidator imageUploadValidator;
    @Mock private MultipartFile mockImage;

    @InjectMocks
    private ProductService productService;

    @TempDir Path tempDir;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() throws Exception {
        product1 = new Product(1, new ProductDetails("Laptop", "Electronics", "Dell", 999.99, "laptop.jpg", 10, 5.0));
        product2 = new Product(2, new ProductDetails("Smartphone", "Electronics", "Apple", 799.99, "phone.jpg", 20, 10.0));
        injectUploadPath(tempDir.toString());
    }

    @Test
    @DisplayName("getAllProducts - returns every product from the repository")
    void getAllProducts_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        List<Product> result = productService.getAllProducts();
        assertThat(result).hasSize(2).containsExactly(product1, product2);
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("getAllProducts - returns empty list when repository is empty")
    void getAllProducts_emptyRepository_returnsEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(productService.getAllProducts()).isEmpty();
    }

    @Test
    @DisplayName("getProductById - returns the matching product")
    void getProductById_existingId_returnsProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        assertThat(productService.getProductById(1)).isEqualTo(product1);
    }

    @Test
    @DisplayName("getProductById - returns null for unknown id")
    void getProductById_nonExistingId_returnsNull() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        assertThat(productService.getProductById(99)).isNull();
    }

    @Test
    @DisplayName("getAllCategories - prepends 'All' before the repository categories")
    void getAllCategories_prependsAllEntry() {
        when(productRepository.findAllCategories()).thenReturn(Arrays.asList("Electronics", "Clothing"));
        assertThat(productService.getAllCategories()).containsExactly("All", "Electronics", "Clothing");
    }

    @Test
    @DisplayName("getAllCategories - returns only 'All' when no categories exist")
    void getAllCategories_noCategories_returnsOnlyAll() {
        when(productRepository.findAllCategories()).thenReturn(Collections.emptyList());
        assertThat(productService.getAllCategories()).containsExactly("All");
    }

    @Test
    @DisplayName("getProductsByCategory - 'All' delegates to getAllProducts")
    void getProductsByCategory_all_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        assertThat(productService.getProductsByCategory("All")).hasSize(2);
        verify(productRepository, never()).findByCategory(any());
    }

    @Test
    @DisplayName("getProductsByCategory - specific category delegates to findByCategory")
    void getProductsByCategory_specificCategory_returnsFilteredProducts() {
        when(productRepository.findByCategory("Electronics")).thenReturn(Arrays.asList(product1, product2));
        assertThat(productService.getProductsByCategory("Electronics")).containsExactly(product1, product2);
    }

    @Test
    @DisplayName("getProductsByStartName - returns products matching the prefix")
    void getProductsByStartName_existingPrefix_returnsMatchingProducts() {
        when(productRepository.findByStartName("Lap")).thenReturn(List.of(product1));
        assertThat(productService.getProductsByStartName("Lap")).containsExactly(product1);
    }

    @Test
    @DisplayName("addProduct - persists product and saves image when valid image provided")
    void addProduct_validImage_savesProductAndImage() throws Exception {
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(true);
        when(mockImage.getOriginalFilename()).thenReturn("laptop.jpg");

        productService.addProduct(1, new ProductDetails("Laptop", "Electronics", "Dell", 999.99, null, 10, 5.0), mockImage);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getImage()).isEqualTo("laptop.jpg");
        verify(mockImage).transferTo(any(java.io.File.class));
    }

    @Test
    @DisplayName("addProduct - throws InvalidImageUploadException for invalid image format")
    void addProduct_invalidImageFormat_throwsException() {
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(false);

        assertThatThrownBy(() ->
            productService.addProduct(1, new ProductDetails("Laptop", "Electronics", "Dell", 999.99, null, 10, 5.0), mockImage)
        ).isInstanceOf(InvalidImageUploadException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct - updates all fields when valid image provided")
    void updateProduct_validImage_updatesAllFields() throws Exception {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(true);
        when(mockImage.getOriginalFilename()).thenReturn("new-laptop.jpg");

        productService.updateProduct(1, new ProductDetails("Laptop Pro", "Electronics", "Dell", 1199.99, null, 5, 8.0), mockImage);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Laptop Pro");
        assertThat(captor.getValue().getImage()).isEqualTo("new-laptop.jpg");
    }

    @Test
    @DisplayName("updateProduct - throws RuntimeException when product not found")
    void updateProduct_nonExistingId_throwsRuntimeException() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
            productService.updateProduct(99, new ProductDetails("Ghost", "None", "None", 1.0, null, 0, 0), null)
        ).isInstanceOf(RuntimeException.class).hasMessage("Product not found");
    }

    @Test
    @DisplayName("deleteProduct - delegates deletion to repository")
    void deleteProduct_existingId_callsRepositoryDelete() {
        doNothing().when(productRepository).deleteById(1);
        productService.deleteProduct(1);
        verify(productRepository).deleteById(1);
    }

    @Test
    @DisplayName("countProducts - returns repository count as int")
    void countProducts_returnsCorrectCount() {
        when(productRepository.count()).thenReturn(5L);
        assertThat(productService.countProducts()).isEqualTo(5);
    }

    private void injectUploadPath(String path) throws Exception {
        Field field = ProductService.class.getDeclaredField("uploadPath");
        field.setAccessible(true);
        field.set(productService, path);
    }
}
