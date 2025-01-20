import java.sql.*;
import java.util.Scanner;

public class Main {

    static void crearTablaYUsuarios() {
        String url = "jdbc:sqlite:banco.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            String tabla = """
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        sueldo REAL NOT NULL UNIQUE,
                        dni TEXT NOT NULL UNIQUE
                    );
                    """;
            Statement stmt = conn.createStatement();
            stmt.execute(tabla);

            String sqlInsert = "INSERT INTO usuarios (nombre, sueldo, dni) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

                pstmt.setString(1, "Juan Pérez");
                pstmt.setDouble(2, 2500.50);
                pstmt.setString(3, "12345678A");
                pstmt.executeUpdate();

                pstmt.setString(1, "María López");
                pstmt.setDouble(2, 3000.75);
                pstmt.setString(3, "87654321B");
                pstmt.executeUpdate();

                pstmt.setString(1, "Pedro Gómez");
                pstmt.setDouble(2, 1800.25);
                pstmt.setString(3, "45678912C");
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
        }
    }

    static void actualizarSueldo(String dni, double nuevoSueldo) {
        String url = "jdbc:sqlite:banco.db";
        String sql = "UPDATE usuarios SET sueldo = ? WHERE dni = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, nuevoSueldo);
            pstmt.setString(2, dni);

            int filasActualizadas = pstmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("¡Sueldo actualizado con éxito para el DNI: " + dni + "!");
            } else {
                System.out.println("No se encontró ningún usuario con el DNI: " + dni);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar el sueldo: " + e.getMessage());
        }
    }

    static double obtenerSueldo(String dni) {
        String url = "jdbc:sqlite:banco.db";
        String sql = "SELECT sueldo FROM usuarios WHERE dni = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("sueldo");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el sueldo: " + e.getMessage());
        }

        return -1;
    }

    static boolean verificarDNI(String dni) {
        String upperCase = dni.toUpperCase();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:banco.db");
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuarios WHERE dni = ?")) {

            ps.setString(1, upperCase);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error al verificar el DNI: " + e.getMessage());
            return false;
        }
    }

    static class Cuenta {

        public void ingresar(double cantidad, String concepto, String dni) {
            if (cantidad <= 0) {
                System.out.println("No se puede ingresar una cantidad negativa");
                return;
            }

            double sueldo = obtenerSueldo(dni);
            if (sueldo == -1) {
                System.out.println("No se encontró el usuario con el DNI: " + dni);
                return;
            }

            double nuevoSueldo = sueldo + cantidad;
            System.out.println(nuevoSueldo);
            actualizarSueldo(dni, nuevoSueldo);

            System.out.println("Su saldo final es " + nuevoSueldo + " y se ha realizado este ingreso en " + concepto);
        }

        public void gastar(double cantidad, String dni) {
            if (cantidad <= 0) {
                System.out.println("No se puede gastar una cantidad negativa");
                return;
            }

            double sueldo = obtenerSueldo(dni);
            if (sueldo == -1) {
                System.out.println("No se encontró el usuario con el DNI: " + dni);
                return;
            }

            double nuevoSueldo = sueldo - cantidad;
            actualizarSueldo(dni, nuevoSueldo);

            System.out.println("Su saldo final es " + nuevoSueldo + " y se ha realizado este gasto");
        }

        public void iniciarCuenta() {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Ingrese su DNI: ");
            String dni = scanner.nextLine();

            if (verificarDNI(dni)) {
                System.out.println("DNI válido");
                while (true) {
                    System.out.println("¿Qué desea hacer?");
                    System.out.println("1. Ingresar dinero");
                    System.out.println("2. Gastar dinero");
                    System.out.println("3. Salir");
                    int opcion = scanner.nextInt();

                    if (opcion == 1) {
                        System.out.println("¿Cuánto dinero desea ingresar?");
                        double cantidad = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.println("¿Qué concepto?");
                        String concepto = scanner.nextLine();
                        ingresar(cantidad, concepto, dni);
                    } else if (opcion == 2) {
                        System.out.println("¿Cuánto dinero desea gastar?");
                        double cantidad = scanner.nextDouble();
                        scanner.nextLine();
                        gastar(cantidad, dni);
                    } else if (opcion == 3) {
                        break;
                    }
                }
            } else {
                System.out.println("DNI no válido");
            }
        }
    }

    public static void main(String[] args) {
        crearTablaYUsuarios();

        Cuenta cuenta = new Cuenta();
        cuenta.iniciarCuenta();
    }
}