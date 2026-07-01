package com.springspartans.shopkart.catalog.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.springspartans.shopkart.catalog.application.ProductService;
import com.springspartans.shopkart.catalog.domain.Product;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return product;
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        if (products == null || products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return products;
    }

    @GetMapping("/search")
    public List<Product> getProductsByStartName(@RequestParam String prefix) {
        return productService.getProductsByStartName(prefix);
    }
}
