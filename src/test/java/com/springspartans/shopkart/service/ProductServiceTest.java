package com.springspartans.shopkart.service;

import com.springspartans.shopkart.exception.InvalidImageUploadException;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.repository.ProductRepository;
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
        product1 = new Product(1, "Laptop", "Electronics", "Dell", 999.99, "laptop.jpg", 10, 5.0);
        product2 = new Product(2, "Smartphone", "Electronics", "Apple", 799.99, "phone.jpg", 20, 10.0);

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

        List<Product> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getProductById - returns the matching product")
    void getProductById_existingId_returnsProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        Product result = productService.getProductById(1);

        assertThat(result).isEqualTo(product1);
    }

    @Test
    @DisplayName("getProductById - returns null for unknown id")
    void getProductById_nonExistingId_returnsNull() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        Product result = productService.getProductById(99);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getAllCategories - prepends 'All' before the repository categories")
    void getAllCategories_prependsAllEntry() {
        when(productRepository.findAllCategories())
                .thenReturn(Arrays.asList("Electronics", "Clothing"));

        List<String> result = productService.getAllCategories();

        assertThat(result).containsExactly("All", "Electronics", "Clothing");
    }

    @Test
    @DisplayName("getAllCategories - returns only 'All' when no categories exist")
    void getAllCategories_noCategories_returnsOnlyAll() {
        when(productRepository.findAllCategories()).thenReturn(Collections.emptyList());

        List<String> result = productService.getAllCategories();

        assertThat(result).containsExactly("All");
    }

    @Test
    @DisplayName("getProductsByCategory - 'All' delegates to getAllProducts")
    void getProductsByCategory_all_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getProductsByCategory("All");

        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
        verify(productRepository, never()).findByCategory(any());
    }

    @Test
    @DisplayName("getProductsByCategory - specific category delegates to findByCategory")
    void getProductsByCategory_specificCategory_returnsFilteredProducts() {
        when(productRepository.findByCategory("Electronics"))
                .thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getProductsByCategory("Electronics");

        assertThat(result).containsExactly(product1, product2);
        verify(productRepository).findByCategory("Electronics");
    }

    @Test
    @DisplayName("getProductsByCategory - unknown category returns empty list")
    void getProductsByCategory_unknownCategory_returnsEmpty() {
        when(productRepository.findByCategory("Unknown")).thenReturn(Collections.emptyList());

        List<Product> result = productService.getProductsByCategory("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getProductsByStartName - returns products matching the prefix")
    void getProductsByStartName_existingPrefix_returnsMatchingProducts() {
        when(productRepository.findByStartName("Lap")).thenReturn(List.of(product1));

        List<Product> result = productService.getProductsByStartName("Lap");

        assertThat(result).containsExactly(product1);
    }

    @Test
    @DisplayName("getProductsByStartName - returns empty list for unmatched prefix")
    void getProductsByStartName_noMatch_returnsEmptyList() {
        when(productRepository.findByStartName("XYZ")).thenReturn(Collections.emptyList());

        List<Product> result = productService.getProductsByStartName("XYZ");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("addProduct - persists product and saves image when valid image provided")
    void addProduct_validImage_savesProductAndImage() throws Exception {
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(true);
        when(mockImage.getOriginalFilename()).thenReturn("laptop.jpg");

        productService.addProduct(1, "Laptop", "Electronics", "Dell", 999.99, mockImage, 10, 5.0);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getImage()).isEqualTo("laptop.jpg");
        verify(mockImage).transferTo(any(java.io.File.class));
    }

    @Test
    @DisplayName("addProduct - persists product without image when image is null")
    void addProduct_nullImage_savesProductWithoutImage() throws Exception {
        productService.addProduct(1, "Laptop", "Electronics", "Dell", 999.99, null, 10, 5.0);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getImage()).isNull();
        verify(mockImage, never()).transferTo(any(java.io.File.class));
    }

    @Test
    @DisplayName("addProduct - persists product without image when image is empty")
    void addProduct_emptyImage_savesProductWithoutImage() throws Exception {
        when(mockImage.isEmpty()).thenReturn(true);

        productService.addProduct(1, "Laptop", "Electronics", "Dell", 999.99, mockImage, 10, 5.0);

        verify(productRepository).save(any(Product.class));
        verify(imageUploadValidator, never()).isValidImage(any());
    }

    @Test
    @DisplayName("addProduct - throws InvalidImageUploadException for invalid image format")
    void addProduct_invalidImageFormat_throwsException() {
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(false);

        assertThatThrownBy(() ->
            productService.addProduct(1, "Laptop", "Electronics", "Dell", 999.99, mockImage, 10, 5.0)
        ).isInstanceOf(InvalidImageUploadException.class)
         .hasMessage("Improper file format!");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct - updates all fields and replaces image when valid image provided")
    void updateProduct_validImage_updatesAllFields() throws Exception {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(true);
        when(mockImage.getOriginalFilename()).thenReturn("new-laptop.jpg");

        productService.updateProduct(1, "Laptop Pro", "Electronics", "Dell", 1199.99, mockImage, 5, 8.0);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Laptop Pro");
        assertThat(saved.getPrice()).isEqualTo(1199.99);
        assertThat(saved.getImage()).isEqualTo("new-laptop.jpg");
        assertThat(saved.getDiscount()).isEqualTo(8.0);
    }

    @Test
    @DisplayName("updateProduct - updates fields without changing image when no image provided")
    void updateProduct_nullImage_updatesFieldsOnlyWithoutChangingImage() throws Exception {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        productService.updateProduct(1, "Laptop V2", "Electronics", "Dell", 1099.99, null, 8, 3.0);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Laptop V2");
        assertThat(captor.getValue().getImage()).isEqualTo("laptop.jpg");
    }

    @Test
    @DisplayName("updateProduct - throws RuntimeException when product not found")
    void updateProduct_nonExistingId_throwsRuntimeException() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            productService.updateProduct(99, "Ghost", "None", "None", 0, null, 0, 0)
        ).isInstanceOf(RuntimeException.class)
         .hasMessage("Product not found");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct - throws InvalidImageUploadException for invalid image format")
    void updateProduct_invalidImageFormat_throwsException() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageUploadValidator.isValidImage(mockImage)).thenReturn(false);

        assertThatThrownBy(() ->
            productService.updateProduct(1, "Laptop", "Electronics", "Dell", 999.99, mockImage, 10, 5.0)
        ).isInstanceOf(InvalidImageUploadException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct - keeps original image when provided image is empty")
    void updateProduct_emptyImage_keepsOriginalImage() throws Exception {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(mockImage.isEmpty()).thenReturn(true);

        productService.updateProduct(1, "Laptop", "Electronics", "Dell", 999.99, mockImage, 10, 5.0);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getImage()).isEqualTo("laptop.jpg");
    }

    @Test
    @DisplayName("deleteProduct - delegates deletion to repository with correct id")
    void deleteProduct_existingId_callsRepositoryDelete() {
        doNothing().when(productRepository).deleteById(1);

        productService.deleteProduct(1);

        verify(productRepository).deleteById(1);
    }

    @Test
    @DisplayName("countProducts - returns repository count as int")
    void countProducts_returnsCorrectCount() {
        when(productRepository.count()).thenReturn(5L);

        int result = productService.countProducts();

        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("countProducts - returns 0 when repository is empty")
    void countProducts_emptyRepository_returnsZero() {
        when(productRepository.count()).thenReturn(0L);

        int result = productService.countProducts();

        assertThat(result).isZero();
    }

    // Injects the upload path into the private field via reflection
    private void injectUploadPath(String path) throws Exception {
        Field field = ProductService.class.getDeclaredField("uploadPath");
        field.setAccessible(true);
        field.set(productService, path);
    }
}