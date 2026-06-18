package com.springspartans.shopkart.controller;

import com.springspartans.shopkart.service.AdminService;
import com.springspartans.shopkart.service.CustomerService;
import com.springspartans.shopkart.service.OrderService;
import com.springspartans.shopkart.service.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite: AdminController - Pruebas de Capa de Controlador")
public class AdminControllerTest {

    // -------------------------------------------------------------------------
    // Mocks de dependencias
    // -------------------------------------------------------------------------

    @Mock
    private AdminService adminService;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    // Mockito instancia AdminController con los 4 mocks via constructor injection
    @InjectMocks
    private AdminController adminController;

    // -------------------------------------------------------------------------
    // SetUp / TearDown
    // -------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        // No se requiere configuración adicional — Mockito maneja los mocks
    }

    @AfterEach
    void tearDown() {
        // Mockito resetea los mocks automáticamente con MockitoExtension
    }

    // =========================================================================
    // login() — GET /admin
    // =========================================================================

    @Test
    @DisplayName("login() - Cargar página de login")
    void testLoginPage() {
        // Inicializar (no se requieren valores de prueba)

        // Ejecutar
        String vista = adminController.login();

        // Verificar
        assertEquals("admin/admin_login", vista);
    }

    // =========================================================================
    // login(email, password, security_key) — GET /admin/login
    // =========================================================================

    @Test
    @DisplayName("login(params) - Login con credenciales válidas")
    void testLoginCredencialesValidas() {
        // Inicializar
        String email       = "admin@mail.com";
        String password    = "abc123";
        String securityKey = "key123";
        when(adminService.login(email, password, securityKey)).thenReturn(true);

        // Ejecutar
        String vista = adminController.login(email, password, securityKey);

        // Verificar
        assertEquals("redirect:/admin/dashboard", vista);
        verify(adminService, times(1)).login(email, password, securityKey);
    }

    @Test
    @DisplayName("login(params) - Login con credenciales inválidas")
    void testLoginCredencialesInvalidas() {
        // Inicializar
        String email       = "admin@mail.com";
        String password    = "wrongpass";
        String securityKey = "wrongkey";
        when(adminService.login(email, password, securityKey)).thenReturn(false);

        // Ejecutar
        String vista = adminController.login(email, password, securityKey);

        // Verificar
        assertEquals("redirect:/admin?msg=failed", vista);
        verify(adminService, times(1)).login(email, password, securityKey);
    }

    // =========================================================================
    // logout() — GET /admin/logout
    // =========================================================================

    @Test
    @DisplayName("logout() - Cerrar sesión activa")
    void testLogout() {
        // Inicializar (no se requieren valores de prueba)

        // Ejecutar
        String vista = adminController.logout();

        // Verificar
        assertEquals("redirect:/admin?msg=logout", vista);
        verify(adminService, times(1)).logout();
    }
}