package com.springspartans.shopkart.repository;
 
import com.springspartans.shopkart.model.Admin;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
 
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Suite: AdminRepository - Pruebas Módulo de Repositorio")
public class AdminRepositoryTest {
    //SetUp/TearDown
    @Autowired
    private AdminRepository adminRepository;
    private Admin adminGuardado;
 
    @BeforeEach
    void setUp() { //internamente, el id se genera automáticamente por H2 IDENTITY
        Admin admin = new Admin(0, "user", "abc123", "admin@mail.com", "key123");
        adminGuardado = adminRepository.save(admin);
    }
    @AfterEach
    void tearDown() {//se limpia la BD en memoria para que cada test parta de un estado limpio
        adminRepository.deleteAll();
        adminGuardado = null;
    }

    //TESTS save(Admin)
    @Test
    @DisplayName("save - Guardar Admin con datos válidos")
    void testSaveAdminValido() {
        // Inicializar (adminGuardado ya fue persistido en setUp)
        // Ejecutar (el save ya se ejecutó en setUp, aquí solo verificamos el resultado)
        // Verificar
        assertNotNull(adminGuardado);
        assertTrue(adminGuardado.getId() > 0);
        assertEquals("user", adminGuardado.getUsername());
        assertEquals("abc123", adminGuardado.getPassword());
        assertEquals("admin@mail.com", adminGuardado.getEmail());
        assertEquals("key123", adminGuardado.getSecurity_key());
    }

    //TESTS findByEmail(String)
    @Test
    @DisplayName("findByEmail - Buscar Admin con email existente")
    void testFindByEmailExistente() {
        // Inicializar
        String emailExistente = "admin@mail.com";
        // Ejecutar
        Optional<Admin> resultado = adminRepository.findByEmail(emailExistente);
        // Verificar
        assertTrue(resultado.isPresent());
        assertEquals(emailExistente, resultado.get().getEmail());
    }
 
    @Test
    @DisplayName("findByEmail - Buscar Admin con email inexistente")
    void testFindByEmailInexistente() {
        // Inicializar
        String emailInexistente = "noexiste@mail.com";
        // Ejecutar
        Optional<Admin> resultado = adminRepository.findByEmail(emailInexistente);
        // Verificar
        assertTrue(resultado.isEmpty());
    }

    //TESTS findById(Integer)
    @Test
    @DisplayName("findById - Buscar Admin con id existente")
    void testFindByIdExistente() {
        // Inicializar
        int idExistente = adminGuardado.getId();
        // Ejecutar
        Optional<Admin> resultado = adminRepository.findById(idExistente);
        // Verificar
        assertTrue(resultado.isPresent());
        assertEquals(idExistente, resultado.get().getId());
    }
 
    @Test
    @DisplayName("findById - Buscar Admin con id inexistente")
    void testFindByIdInexistente() {
        // Inicializar
        int idInexistente = 999;
        // Ejecutar
        Optional<Admin> resultado = adminRepository.findById(idInexistente);
        // Verificar
        assertTrue(resultado.isEmpty());
    }

    //TESTS deleteByAll(Integer)
    @Test
    @DisplayName("deleteById - Eliminar Admin existente por id")
    void testDeleteById() {
        // Inicializar
        int idAEliminar = adminGuardado.getId();
        // Ejecutar
        adminRepository.deleteById(idAEliminar);
        // Verificar
        Optional<Admin> resultado = adminRepository.findById(idAEliminar);
        assertTrue(resultado.isEmpty());
    }
}