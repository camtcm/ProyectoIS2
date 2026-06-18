package com.springspartans.shopkart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Order;
import com.springspartans.shopkart.model.Order.OrderStatus;
import com.springspartans.shopkart.service.CustomerService;
import com.springspartans.shopkart.service.OrderService;
import com.springspartans.shopkart.service.ProductService;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	// 1. SOLUCIÓN A FLECHAS ROJAS: Constantes para literales duplicados
	private static final String ATTR_CUSTOMER = "customer";
	private static final String ATTR_CATEGORY_LIST = "categoryList";
	private static final String VIEW_HISTORY = "order/history";
	private static final String REDIRECT_BASE = "redirect:/order";
	private static final String REDIRECT_DETAIL = "redirect:/order/";
	
	private final OrderService orderService;
	private final ProductService productService;
	private final CustomerService customerService;
	
	// 2. SOLUCIÓN A FLECHAS NARANJAS: Inyección por Constructor (Principio SOLID - DIP)
	public OrderController(OrderService orderService, ProductService productService, CustomerService customerService) {
		this.orderService = orderService;
		this.productService = productService;
		this.customerService = customerService;
	}
	
	// 3. SOLUCIÓN A FLECHAS AMARILLAS Y LÓGICA REPETIDA: Extract Method unificando estilo Java int[]
	private void populateCommonModelAttributes(Model model, Customer customer) {
		List<String> categoryList = productService.getAllCategories();
		model.addAttribute(ATTR_CATEGORY_LIST, categoryList);
		model.addAttribute(ATTR_CUSTOMER, customer);
		
		// Corchetes corregidos al tipo: int[] en lugar de int arreglo[]
		int[] orderCountByStatusArr = {
			orderService.countOrdersByStatusForCustId(OrderStatus.Pending),
			orderService.countOrdersByStatusForCustId(OrderStatus.Shipped),
			orderService.countOrdersByStatusForCustId(OrderStatus.Delivered),
			orderService.countOrdersByStatusForCustId(OrderStatus.Cancelled)
		};
		model.addAttribute("orderCountByStatusArr", orderCountByStatusArr);
	}
	
	private Customer getValidatedCustomer() {
		Customer customer = customerService.getCustomer();
		if (customer == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return customer;
	}
	
	@GetMapping
	public String getOrdersOfLoggedInCustomer(Model model) {
		Customer customer = getValidatedCustomer();
		List<Order> orderList = orderService.getOrdersOfLoggedInCustomer();
		model.addAttribute("orderList", orderList);
		
		populateCommonModelAttributes(model, customer);
		return VIEW_HISTORY;
	}
	
	@GetMapping("/{id}")
	public String getOrderById(@PathVariable int id, Model model) {
		List<String> categoryList = productService.getAllCategories();
		model.addAttribute(ATTR_CATEGORY_LIST, categoryList);
		Customer customer = getValidatedCustomer();
		model.addAttribute(ATTR_CUSTOMER, customer);
		
		Order order = orderService.getOrderById(id);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		model.addAttribute("order", order);
		return "order/summary";
	}
	
	@PostMapping
	public String orderAll() {
		orderService.orderAll();
		return REDIRECT_BASE;
	}
	
	@PostMapping("/{slno}")
	public String orderCartItem(@PathVariable int slno) {
		int orderId = orderService.orderCartItem(slno);
		if (orderId == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return REDIRECT_DETAIL + orderId;
	}
	
	@PostMapping("/again/{id}")
	public String orderAgain(@PathVariable("id") int orderId) {
		int newOrderId = orderService.orderAgain(orderId);
		if (newOrderId == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return REDIRECT_DETAIL + newOrderId;
	}
	
	@PostMapping("/cancel/{id}")
	public String cancelOrder(@PathVariable("id") int orderId) {
		orderService.cancelOrder(orderId);
		return REDIRECT_BASE;
	}
	
	@GetMapping("/status/{status}")
	public String filterByStatusForCustId(@PathVariable String status, Model model) {
		Customer customer = getValidatedCustomer();
		if (status == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		List<Order> orderList = orderService.filterByStatusForCustId(status);
		model.addAttribute("orderList", orderList);
		
		populateCommonModelAttributes(model, customer);
		return VIEW_HISTORY;
	}
}
