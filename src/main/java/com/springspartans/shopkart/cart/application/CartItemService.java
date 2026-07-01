package com.springspartans.shopkart.cart.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springspartans.shopkart.cart.domain.CartItem;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.cart.infrastructure.CartItemRepository;
import com.springspartans.shopkart.repository.ProductRepository;
import com.springspartans.shopkart.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartItemService {
	private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);

	@Autowired
	private CartItemRepository cartRepo;
	@Autowired
	private ProductRepository prodRepo;
	@Autowired
	private CustomerService customerService;

	private int custId;

	public List<CartItem> getAllCartItems() {
		Customer customer = customerService.getCustomer();
		this.custId = (customer == null) ? 0 : customer.getId();
		return getAllCartItemsforCustomer(custId);
	}

	public CartItem getBySlno(int itemId) {
		return cartRepo.findById(itemId).orElse(null);
	}

	public List<CartItem> getAllCartItemsforCustomer(int custId) {
		return cartRepo.findByCustId(custId);
	}

	public boolean incrementQuantity(int itemId) {
		CartItem item = cartRepo.findById(itemId).orElse(null);
		if (item != null && item.getProduct().getStock() > 0) {
			item.setQuantity(item.getQuantity() + 1);
			item.getProduct().setStock(item.getProduct().getStock() - 1);
			cartRepo.save(item);
			return true;
		}
		return false;
	}

	public void decrementQuantity(int itemId) {
		CartItem item = cartRepo.findById(itemId).orElse(null);
		if (item != null) {
			if (item.getQuantity() == 1) {
				deleteCartItem(itemId);
			} else {
				item.setQuantity(item.getQuantity() - 1);
				item.getProduct().setStock(item.getProduct().getStock() + 1);
				cartRepo.save(item);
			}
		}
	}

	public void deleteCartItem(int itemId) {
		CartItem item = cartRepo.findById(itemId).orElse(null);
		if (item != null) {
			item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity());
			cartRepo.deleteById(itemId);
		}
	}

	public double getCartItemPrice(int itemId) {
		CartItem item = cartRepo.findById(itemId).orElse(null);
		if (item != null) {
			Product prod = item.getProduct();
			return prod.getPrice();
		}
		return 0;
	}

	public double getCartPrice() {
		if (custId == 0)
			return 0;
		List<CartItem> list = getAllCartItemsforCustomer(custId);
		double total = 0;
		Product prod;
		for (CartItem item : list) {
			prod = item.getProduct();
			total += prod.getPrice() * (100 - prod.getDiscount()) / 100.0 * item.getQuantity();
		}
		return total;
	}

	public void addToCart(int itemId, Customer cust) {
		Product prod = prodRepo.findById(itemId).orElse(null);
		if (prod == null) {
			logger.warn("Product not found for id: {}", itemId);
			return;
		}
		if (prod.getStock() > 0) {
			prod.setStock(prod.getStock() - 1);
			CartItem item = new CartItem();
			item.setCustomer(cust);
			item.setProduct(prod);
			item.setQuantity(1);
			cartRepo.save(item);
		}
	}

	public void clearCart() {
		List<CartItem> items = cartRepo.findByCustId(custId);
		for (CartItem item : items)
			deleteCartItem(item.getSlno());
	}

}
