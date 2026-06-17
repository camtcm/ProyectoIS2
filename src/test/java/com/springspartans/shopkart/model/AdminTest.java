package com.springspartans.shopkart.model;
 
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Suite: Admin - Pruebas Modulo de Dominio")

public class AdminTest {
    //SetUp / TearDown
    private Admin admin;
    @BeforeEach
    void setUp() { //instancia válida reutilizable antes de cada test
        admin = new Admin(1, "user", "abc123", "admin@mail.com", "key123");
    }
    @AfterEach
    void tearDown() {//campos nulos
        admin = null;
    }


    //TESTS Admin() Constructor
    @Test
    @DisplayName("Admin() - Instancia de Admin con constructor vacío")
    void testConstructorVacio() {
        // Inicializar
        Admin adminVacio;
        // Ejecutar
        adminVacio = new Admin();
        // Verificar
        assertNotNull(adminVacio);
        assertNull(adminVacio.getUsername());
        assertNull(adminVacio.getPassword());
        assertNull(adminVacio.getEmail());
        assertNull(adminVacio.getSecurity_key());
    }

    @Test
    @DisplayName("Admin(params) - Instancia de Admin con valores válidos")
    void testConstructorValido() {
        // Inicializar
        int id = 1;
        String username = "user";
        String password = "abc123";
        String email = "admin@mail.com";
        String security_key = "key123";
        // Ejecutar
        Admin adminValido = new Admin(id, username, password, email, security_key);
        // Verificar
        assertEquals(id, adminValido.getId());
        assertEquals(username, adminValido.getUsername());
        assertEquals(password, adminValido.getPassword());
        assertEquals(email, adminValido.getEmail());
        assertEquals(security_key, adminValido.getSecurity_key());
    }

    @Test
    @DisplayName("Admin(params) - Instancia de Admin con username nulo en constructor")
    void testConstructorUsernameNulo() {
        // Inicializar
        int id = 1;
        String username = null;
        String password = "abc123";
        String email = "admin@mail.com";
        String security_key = "key123";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            new Admin(id, username, password, email, security_key)
        );
    }

    @Test
    @DisplayName("Admin(params) - Instancia de Admin con email nulo en constructor")
    void testConstructorEmailNulo() {
        // Inicializar
        int id = 1;
        String username = "user";
        String password = "abc123";
        String email = null;
        String security_key = "key123";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            new Admin(id, username, password, email, security_key)
        );
    }

    //TESTS setUsername()
    @Test
    @DisplayName("setUsername - Asignar username válido")
    void testSetUsernameValido() {
        // Inicializar
        String nuevoUsername = "user";
        // Ejecutar
        admin.setUsername(nuevoUsername);
        // Verificar
        assertEquals(nuevoUsername, admin.getUsername());
    }
    
    @Test
    @DisplayName("setUsername - Asignar username nulo")
    void testSetUsernameNulo() {
        // Inicializar
        String nuevoUsername = null;
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setUsername(nuevoUsername)
        );
    }
    
    @Test
    @DisplayName("setUsername - Asignar username vacío")
    void testSetUsernameVacio() {
        // Inicializar
        String nuevoUsername = "";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setUsername(nuevoUsername)
        );
    }
    
    @Test
    @DisplayName("setUsername - Asignar username de sólo espacios")
    void testSetUsernameSoloEspacios() {
        // Inicializar
        String nuevoUsername = "     ";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setUsername(nuevoUsername)
        );
    }

    //TESTS setPassword()
    @Test
    @DisplayName("setPassword - Asignar password válido")
    void testSetPasswordValido() {
        // Inicializar
        String nuevoPassword = "pass_seguro"; 
        // Ejecutar
        admin.setPassword(nuevoPassword);
        // Verificar
        assertEquals(nuevoPassword, admin.getPassword());
    }
    
    @Test
    @DisplayName("setPassword - Asignar password nulo")
    void testSetPasswordNulo() {
        // Inicializar
        String nuevoPassword = null;
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setPassword(nuevoPassword)
        );
    }
    
    @Test
    @DisplayName("setPassword - Asignar password vacío")
    void testSetPasswordVacio() {
        // Inicializar
        String nuevoPassword = "";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setPassword(nuevoPassword)
        );
    }

    //TESTS setEmail()
    @Test
    @DisplayName("setEmail - Asignar email válido")
    void testSetEmailValido() {
        // Inicializar
        String nuevoEmail = "admin@mail.com";
        // Ejecutar
        admin.setEmail(nuevoEmail);
        // Verificar
        assertEquals(nuevoEmail, admin.getEmail());
    }
    
    @Test
    @DisplayName("setEmail - Asignar email nulo")
    void testSetEmailNulo() {
        // Inicializar
        String nuevoEmail = null;
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setEmail(nuevoEmail)
        );
    }
    
    @Test
    @DisplayName("setEmail - Asignar email vacío")
    void testSetEmailVacio() {
        // Inicializar
        String nuevoEmail = "";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setEmail(nuevoEmail)
        );
    }

    //TESTS setSecurity_key()
    @Test
    @DisplayName("setSecurity_key - Asignar security_key válida")
    void testSetSecurityKeyValida() {
        // Inicializar
        String nuevaKey = "key_segura123";
        // Ejecutar
        admin.setSecurity_key(nuevaKey);
        // Verificar
        assertEquals(nuevaKey, admin.getSecurity_key());
    }
 
    @Test
    @DisplayName("setSecurity_key - Asignar security_key nula")
    void testSetSecurityKeyNula() {
        // Inicializar
        String nuevaKey = null;
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setSecurity_key(nuevaKey)
        );
    }
 
    @Test
    @DisplayName("setSecurity_key - Asignar security_key vacía")
    void testSetSecurityKeyVacia() {
        // Inicializar
        String nuevaKey = "";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setSecurity_key(nuevaKey)
        );
    }
 
    @Test
    @DisplayName("setSecurity_key - Asignar security_key de sólo espacios")
    void testSetSecurityKeySoloEspacios() {
        // Inicializar
        String nuevaKey = "     ";
        // Ejecutar y Verificar
        assertThrows(IllegalArgumentException.class, () ->
            admin.setSecurity_key(nuevaKey)
        );
    }

    //TESTS toString()
    @Test
    @DisplayName("toString() - Verifica el estado de los campos de la instancia de Admin")
    void testToString() {
        // Inicializar
        // (admin ya fue construido en setUp con id=1, username="user",
        //  password="abc123", email="admin@mail.com", security_key="key123")
        String resultadoEsperado =
            "Admin [id=1, username=user, password=abc123, email=admin@mail.com, security_key=key123]";
        // Ejecutar
        String resultadoObtenido = admin.toString(); 
        // Verificar
        assertEquals(resultadoEsperado, resultadoObtenido);
    }
}