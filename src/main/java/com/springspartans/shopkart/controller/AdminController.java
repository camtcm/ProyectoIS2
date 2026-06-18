package com.springspartans.shopkart.controller;

import com.springspartans.shopkart.exception.InvalidImageUploadException;
import com.springspartans.shopkart.model.Admin;
import com.springspartans.shopkart.model.Customer;
import com.springspartans.shopkart.model.Order;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.model.ProductDetails;
import com.springspartans.shopkart.model.Order.OrderStatus;
import com.springspartans.shopkart.service.AdminService;
import com.springspartans.shopkart.service.CustomerService;
import com.springspartans.shopkart.service.OrderService;
import com.springspartans.shopkart.service.ProductService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // REFACTORING 1: Constructor injection (Issue #40 - SonarLint lineas 37-43)
    // Se reemplazo @Autowired en campos por inyeccion por constructor.
    // Beneficio: dependencias explicitas e inmutables (final),
    // mejor testabilidad y cumplimiento del principio DIP (SOLID).
    private final AdminService adminService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;

    // REFACTORING 2: Constantes para literales repetidos (SonarLint)
    // Se extrajeron strings duplicados como constantes de clase (DRY principle).
    private static final String VIEW_MANAGE_ORDERS = "admin/manageOrders";
    private static final String REDIRECT_PRODUCT = "redirect:/admin/dashboard/product";
    private static final String ATTR_ORDER_COUNT = "orderCountByStatusArr";
    private static final String ATTR_ORDER_LIST = "orderList";

    @Autowired
    public AdminController(AdminService adminService,
                           CustomerService customerService,
                           ProductService productService,
                           OrderService orderService) {
        this.adminService = adminService;
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
    }

    // REFACTORING 3: Extract Method - verificacion de sesion de admin
    // La logica repetida en 9 metodos se extrajo en un unico metodo auxiliar.
    // Beneficio: un solo punto de cambio, menor complejidad cognitiva (DRY).
    private void checkAdminAccess() {
        Admin admin = adminService.getAdmin();
        Customer customer = customerService.getCustomer();
        if (admin == null && customer == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        // REFACTORING 4: Simplificacion de condicional redundante (SonarLint lineas 82,140,etc)
        // Original: admin == null && customer != null (customer != null es siempre true aqui)
        // Corregido: admin == null (condicion simplificada)
        else if (admin == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    // REFACTORING 3: Extract Method - construccion del array de conteo de ordenes
    // REFACTORING 5: Array designators movidos al tipo (SonarLint lineas 87,201,288,310)
    // Original: int orderCountByStatusArr[] = {...}
    // Corregido: int[] arr = {...}
    private int[] buildOrderCountArray() {
        return new int[] {
            orderService.countOrdersByStatus(OrderStatus.Pending),
            orderService.countOrdersByStatus(OrderStatus.Shipped),
            orderService.countOrdersByStatus(OrderStatus.Delivered),
            orderService.countOrdersByStatus(OrderStatus.Cancelled)
        };
    }

    // Session management
    @GetMapping
    public String login() {
        return "admin/admin_login";
    }

    @GetMapping("/login")
    // REFACTORING 6: Renombrar variable local (SonarLint linea 55)
    // Original: security_key (snake_case - estilo C)
    // Corregido: securityKey (camelCase - convencion Java)
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String securityKey) {
        if (email == null || password == null || securityKey == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        boolean success = adminService.login(email, password, securityKey);
        if (success) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/admin?msg=failed";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        adminService.logout();
        return "redirect:/admin?msg=logout";
    }

    // Dashboard methods
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        checkAdminAccess();
        model.addAttribute(ATTR_ORDER_COUNT, buildOrderCountArray());
        double totalSales = orderService.totalSalesLastWeek();
        model.addAttribute("totalSales", totalSales);
        int prodCount = productService.countProducts();
        model.addAttribute("prodCount", prodCount);
        int custCount = customerService.countCustomers();
        model.addAttribute("custCount", custCount);
        List<Integer[]> topSellersId = orderService.getTopSellingProducts(6);
        List<Object[]> topSellers = new ArrayList<>();
        for (Integer[] obj : topSellersId) {
            topSellers.add(new Object[] {productService.getProductById(obj[0]), obj[1]});
        }
        model.addAttribute("topSellers", topSellers);
        List<Double> lastWeekSales = new ArrayList<>();
        List<Timestamp> lastWeek = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            LocalDateTime date = now.minusDays(i);
            Timestamp timestamp = Timestamp.valueOf(date.toLocalDate().atStartOfDay());
            lastWeek.add(timestamp);
            lastWeekSales.add(orderService.getSalesForDate(timestamp));
        }
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("lastWeekSales", lastWeekSales);
        int[] custActivity = {
            customerService.countSignupByDate(new Timestamp(System.currentTimeMillis())),
            customerService.countLoginByDate(new Timestamp(System.currentTimeMillis())),
            orderService.countCustomersWhoPlacedOrderOnDate(new Timestamp(System.currentTimeMillis()))
        };
        model.addAttribute("custActivity", custActivity);
        return "admin/dashboard";
    }

    @GetMapping("/dashboard/siteWidgets")
    public String getSiteWidgets() {
        checkAdminAccess();
        return "admin/siteWidgets";
    }

    @GetMapping("/dashboard/product/add")
    public String addNewProduct() {
        checkAdminAccess();
        return "admin/addNewProduct";
    }

    @GetMapping("/dashboard/product")
    public String manageProductCatalogue(Model model) {
        checkAdminAccess();
        List<Product> productList = productService.getAllProducts();
        model.addAttribute("productList", productList);
        return "admin/manageProductCatalogue";
    }

    @GetMapping("/dashboard/customer")
    public String manageCustomers(Model model) {
        checkAdminAccess();
        List<Customer> customerList = customerService.getAllCustomers();
        model.addAttribute("customerList", customerList);
        return "admin/manageCustomers";
    }

    @GetMapping("/dashboard/order")
    public String manageOrder(Model model) {
        checkAdminAccess();
        List<Order> orderList = orderService.getAllOrders();
        model.addAttribute(ATTR_ORDER_LIST, orderList);
        model.addAttribute(ATTR_ORDER_COUNT, buildOrderCountArray());
        return VIEW_MANAGE_ORDERS;
    }

    @PostMapping("/dashboard/product/add")
    public String addNewProductAction(
            @RequestParam String name, @RequestParam String category,
            @RequestParam String brand, @RequestParam double price,
            @RequestParam int stock, @RequestParam double discount,
            @RequestParam MultipartFile image) {
        try {
            productService.addProduct(0, new ProductDetails(name, category, brand, price, null, stock, discount), image);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidImageUploadException e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard/product/add?msg=failed";
        }
        return REDIRECT_PRODUCT;
    }

    @PostMapping("/dashboard/product/delete/{id}")
    public String manageProductCatalogueDeleteAction(@PathVariable int id) {
        productService.deleteProduct(id);
        return REDIRECT_PRODUCT;
    }

    @GetMapping("/dashboard/product/update/{id}")
    public String manageProductCatalogueUpdate(@PathVariable int id, Model model) {
        checkAdminAccess();
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/addNewProduct";
    }

    @PostMapping("/dashboard/product/update/{id}")
    public String manageProductCatalogueUpdateAction(
            @PathVariable int id, @RequestParam String name,
            @RequestParam String category, @RequestParam String brand,
            @RequestParam double price, @RequestParam int stock,
            @RequestParam double discount, @RequestParam MultipartFile image) {
        try {
            productService.updateProduct(id, new ProductDetails(name, category, brand, price, null, stock, discount), image);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidImageUploadException e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard/product/update/" + id + "?msg=failed";
        }
        return REDIRECT_PRODUCT;
    }

    @PostMapping("/dashboard/customer/delete/{id}")
    public String manageCustomersDeleteAction(@PathVariable int id) {
        customerService.deleteCustomer(id);
        return "redirect:/admin/dashboard/customer";
    }

    @GetMapping("/dashboard/order/status/{status}")
    public String manageOrderFilterByStatusAction(@PathVariable String status, Model model) {
        checkAdminAccess();
        model.addAttribute("status", status);
        List<Order> orderList = orderService.filterByStatus(status);
        model.addAttribute(ATTR_ORDER_LIST, orderList);
        model.addAttribute(ATTR_ORDER_COUNT, buildOrderCountArray());
        return VIEW_MANAGE_ORDERS;
    }

    @GetMapping("/dashboard/order/search")
    public String manageOrderSearchAction(@RequestParam int custId, Model model) {
        checkAdminAccess();
        List<Order> orderList = orderService.getOrdersByCustId(custId);
        model.addAttribute(ATTR_ORDER_LIST, orderList);
        int[] orderCountByStatusArr = {
            orderService.countOrdersByStatusAndCustId(OrderStatus.Pending, custId),
            orderService.countOrdersByStatusAndCustId(OrderStatus.Shipped, custId),
            orderService.countOrdersByStatusAndCustId(OrderStatus.Delivered, custId),
            orderService.countOrdersByStatusAndCustId(OrderStatus.Cancelled, custId)
        };
        model.addAttribute(ATTR_ORDER_COUNT, orderCountByStatusArr);
        return VIEW_MANAGE_ORDERS;
    }

    @PostMapping("/dashboard/order/update/{id}")
    public String manageOrderUpdateAction(@PathVariable int id) {
        orderService.updateStatus(id);
        return "redirect:/admin/dashboard/order";
    }

    @PostMapping("/dashboard/order/cancel/{id}")
    public String manageOrderCancelAction(@PathVariable int id) {
        orderService.cancelOrder(id);
        return "redirect:/admin/dashboard/order";
    }
}