package com.springspartans.shopkart.service;
 
import com.springspartans.shopkart.model.Admin;
import com.springspartans.shopkart.repository.AdminRepository;
import com.springspartans.shopkart.util.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite: AdminService - Pruebas Módulo de Servicio")
public class AdminServiceTest {
    // Mocks de dependencias
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private HttpSession httpSession;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks //Mockito instancia AdminService (Admin Mock)
    private AdminService adminService;

    //SetUp/TearDown
    private Admin adminStub;
    @BeforeEach
    void setUp() { //Admin stub reutilizable
        adminStub = new Admin(1, "user", "abc123", "admin@mail.com", "key123");
    }
    @AfterEach
    void tearDown() {
        adminStub = null;
    }

    //TESTS login(String, String, String)
    @Test
    @DisplayName("login - Login con credenciales válidas")
    void testLoginCredencialesValidas() {
        // Inicializar
        String email = "admin@mail.com";
        String password = "abc123";
        String securityKey = "key123";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminStub));
        when(passwordEncoder.matches(password, adminStub.getPassword())).thenReturn(true);
        // Ejecutar
        boolean resultado = adminService.login(email, password, securityKey);
        // Verificar
        assertTrue(resultado);
        verify(httpSession, times(1)).setAttribute("loggedInAdmin", adminStub);
    }
 
    @Test
    @DisplayName("login - Login con email inexistente")
    void testLoginEmailInexistente() {
        // Inicializar
        String email = "noexiste@mail.com";
        String password = "abc123";
        String securityKey = "key123";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Ejecutar
        boolean resultado = adminService.login(email, password, securityKey);
        // Verificar
        assertFalse(resultado);
        verify(httpSession, never()).setAttribute(anyString(), any());
    }
 
    @Test
    @DisplayName("login - Login con password incorrecta")
    void testLoginPasswordIncorrecta() {
        // Inicializar
        String email = "admin@mail.com";
        String password = "wrongpass";
        String securityKey = "key123";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminStub));
        when(passwordEncoder.matches(password, adminStub.getPassword())).thenReturn(false);
        // Ejecutar
        boolean resultado = adminService.login(email, password, securityKey);
        // Verificar
        assertFalse(resultado);
        verify(httpSession, never()).setAttribute(anyString(), any());
    }
 
    @Test
    @DisplayName("login - Login con security_key incorrecta")
    void testLoginSecurityKeyIncorrecta() {
        // Inicializar
        String email = "admin@mail.com";
        String password = "abc123";
        String securityKey = "wrongkey";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(adminStub));
        when(passwordEncoder.matches(password, adminStub.getPassword())).thenReturn(true);
        // Ejecutar
        boolean resultado = adminService.login(email, password, securityKey);
        // Verificar
        assertFalse(resultado);
        verify(httpSession, never()).setAttribute(anyString(), any());
    }

    //TESTS logout()
    @Test
    @DisplayName("logout - Cerrar sesión activa")
    void testLogout() {
        // Inicializar (no requiere configuración previa de mocks)
        // Ejecutar
        adminService.logout();
        // Verificar
        verify(httpSession, times(1)).invalidate();
    }

    //TESTS getAdmin()
    @Test
    @DisplayName("getAdmin - Obtener Admin con sesión activa")
    void testGetAdminConSesionActiva() {
        // Inicializar
        when(httpSession.getAttribute("loggedInAdmin")).thenReturn(adminStub);
        // Ejecutar
        Admin resultado = adminService.getAdmin();
        // Verificar
        assertNotNull(resultado);
        assertEquals(adminStub.getEmail(), resultado.getEmail());
    }
 
    @Test
    @DisplayName("getAdmin - Obtener Admin sin sesión activa")
    void testGetAdminSinSesionActiva() {
        // Inicializar
        when(httpSession.getAttribute("loggedInAdmin")).thenReturn(null);
        // Ejecutar
        Admin resultado = adminService.getAdmin();
        // Verificar
        assertNull(resultado);
    }
}