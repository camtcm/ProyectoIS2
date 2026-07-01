package com.springspartans.shopkart.cart.presentation;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.springspartans.shopkart.model.*;
import com.springspartans.shopkart.cart.application.CartItemService;
import com.springspartans.shopkart.cart.domain.CartItem;
import com.springspartans.shopkart.service.CustomerService;
import com.springspartans.shopkart.service.ProductService;

@RestController
@RequestMapping("/cartitem")
public class CartItemController {

    @Autowired
    private CartItemService cartservice;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getAllCartItems() {
        Customer customer = customerService.getCustomer();
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        List<CartItem> cart = cartservice.getAllCartItems();
        double totalPrice = cartservice.getCartPrice();
        List<String> categoryList = productService.getAllCategories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("totalPrice", totalPrice);
        response.put("customer", customer);
        response.put("categoryList", categoryList);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/increase/{slno}")
    public ResponseEntity<String> incrementQuantity(@PathVariable int slno) {
        if (cartservice.getBySlno(slno) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boolean incrementFlag = cartservice.incrementQuantity(slno);
        if (!incrementFlag) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No more stock available for this item");
        }
        return ResponseEntity.ok("Item quantity increased");
    }

    @PostMapping("/decrease/{slno}")
    public ResponseEntity<String> decrementQuantity(@PathVariable int slno) {
        if (cartservice.getBySlno(slno) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cartservice.decrementQuantity(slno);
        return ResponseEntity.ok("Item quantity decreased");
    }

    @PostMapping("/delete/{slno}")
    public ResponseEntity<String> deleteCartItem(@PathVariable int slno) {
        if (cartservice.getBySlno(slno) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cartservice.deleteCartItem(slno);
        return ResponseEntity.ok("Item deleted from cart");
    }

    @PostMapping("/add/{prod_id}")
    public ResponseEntity<String> addToCart(@PathVariable("prod_id") int id) {
        Customer customer = customerService.getCustomer();
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (productService.getProductById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cartservice.addToCart(id, customer);
        return ResponseEntity.ok("Item added to cart");
    }
}
