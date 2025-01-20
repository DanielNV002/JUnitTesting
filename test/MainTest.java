import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private static final String DB_URL = "jdbc:sqlite:banco.db";

    @BeforeAll
    static void setUp() {
        Main.crearTablaYUsuarios(); // Crear tabla y añadir datos de prueba
    }

    @AfterEach
    void cleanUp() {
        // Restaurar los datos de prueba después de cada test
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("UPDATE usuarios SET sueldo = 2500.50 WHERE dni = '12345678A'");
            stmt.executeUpdate("UPDATE usuarios SET sueldo = 3000.75 WHERE dni = '87654321B'");
            stmt.executeUpdate("UPDATE usuarios SET sueldo = 1800.25 WHERE dni = '45678912C'");

        } catch (SQLException e) {
            System.out.println("Error al limpiar los datos: " + e.getMessage());
        }
    }

    @Test
    void testActualizarSueldo() {
        Main.actualizarSueldo("12345678A", 4000.00);
        double nuevoSueldo = Main.obtenerSueldo("12345678A");
        assertEquals(4000.00, nuevoSueldo, "El sueldo no se actualizó correctamente");
    }

    @Test
    void testObtenerSueldo() {
        double sueldo = Main.obtenerSueldo("12345678A");
        assertEquals(2500.50, sueldo, "El sueldo inicial no es correcto");
    }

    @Test
    void testVerificarDNIExistente() {
        assertTrue(Main.verificarDNI("12345678A"), "El DNI debería existir");
    }

    @Test
    void testVerificarDNINoExistente() {
        assertFalse(Main.verificarDNI("99999999Z"), "El DNI no debería existir");
    }

    @Test
    void testIngresarDinero() {
        Main.Cuenta cuenta = new Main.Cuenta();
        cuenta.ingresar(500.00, "Ingreso Test", "12345678A");
        double sueldo = Main.obtenerSueldo("12345678A");
        assertEquals(3000.50, sueldo, "El ingreso no se realizó correctamente");
    }

    @Test
    void testGastarDinero() {
        Main.Cuenta cuenta = new Main.Cuenta();
        cuenta.gastar(500.00, "12345678A");
        double sueldo = Main.obtenerSueldo("12345678A");
        assertEquals(2000.50, sueldo, "El gasto no se realizó correctamente");
    }

    @Test
    void testIngresarCantidadNegativa() {
        Main.Cuenta cuenta = new Main.Cuenta();
        cuenta.ingresar(-500.00, "Ingreso Negativo", "12345678A");
        double sueldo = Main.obtenerSueldo("12345678A");
        assertEquals(2500.50, sueldo, "No se debería permitir ingresar una cantidad negativa");
    }

    @Test
    void testGastarCantidadNegativa() {
        Main.Cuenta cuenta = new Main.Cuenta();
        cuenta.gastar(-500.00, "12345678A");
        double sueldo = Main.obtenerSueldo("12345678A");
        assertEquals(2500.50, sueldo, "No se debería permitir gastar una cantidad negativa");
    }
}