package proyecto.modelo;

public class Socio {
      private static final String URL = "jdbc:sqlite:biblioteca.db";
      private static final Scanner scanner = new Scanner(System.in);

      public static void main(String[] args) {
        crearTabla();
        while (true) {
            System.out.println("\nMenú:");
            System.out.println("0) Salir del programa.");
            System.out.println("1) Insertar un socio en la base de datos.");
            System.out.println("2) Eliminar un socio por código.");
            System.out.println("3) Consultar todos los socios.");
            System.out.println("4) Consultar socios por localidad.");
            System.out.print("Seleccione una opción: ");
            
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 0:
                    return;
                case 1:
                    insertarSocio();
                    break;
                case 2:
                    eliminarSocio();
                    break;
                case 3:
                    consultarSocios();
                    break;
                case 4:
                    consultarSociosPorLocalidad();
                    break;
                default:
                    System.out.println("La opción de menú debe estar comprendida entre 0 y 6.");
            }
        }
    }

    private static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS socio (" +
                     "codigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "dni TEXT NOT NULL UNIQUE, " +
                     "nombre TEXT NOT NULL, " +
                     "domicilio TEXT NOT NULL, " +
                     "telefono TEXT NOT NULL, " +
                     "correo TEXT NOT NULL");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    private static void insertarSocio() {
        System.out.print("Ingrese DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Ingrese Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese Domicilio: ");
        String domicilio = scanner.nextLine();
        System.out.print("Ingrese Teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("Ingrese Correo: ");
        String correo = scanner.nextLine();

        String sql = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            pstmt.setString(2, nombre);
            pstmt.setString(3, domicilio);
            pstmt.setString(4, telefono);
            pstmt.setString(5, correo);
            pstmt.executeUpdate();
            System.out.println("Se ha insertado un socio en la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarSocio() {
        System.out.print("Ingrese el código del socio a eliminar: ");
        int codigo = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM socio WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, codigo);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Se ha eliminado un socio de la base de datos.");
            } else {
                System.out.println("No existe ningún socio con ese código en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("El socio está referenciado en un préstamo de la base de datos.");
        }
    }

    private static void consultarSocios() {
        String sql = "SELECT * FROM socio";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Socio [Código = " + rs.getInt("codigo") + ", DNI = " + rs.getString("dni") + ", Nombre = " + rs.getString("nombre") + ", Domicilio = " + rs.getString("domicilio") + ", Teléfono = " + rs.getString("telefono") + ", Correo = " + rs.getString("correo") + "]");
            }
            if (!found) {
                System.out.println("No se ha encontrado ningún socio en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los socios: " + e.getMessage());
        }
    }

    private static void consultarSociosPorLocalidad() {
        System.out.print("Ingrese la localidad: ");
        String localidad = scanner.nextLine();
        String sql = "SELECT * FROM socio WHERE domicilio LIKE ? ORDER BY nombre ASC";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + localidad + "%");
            ResultSet rs = pstmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Socio [Código = " + rs.getInt("codigo") + ", DNI = " + rs.getString("dni") + ", Nombre = " + rs.getString("nombre") + ", Domicilio = " + rs.getString("domicilio") + ", Teléfono = " + rs.getString("telefono") + ", Correo = " + rs.getString("correo") + "]");
            }
            if (!found) {
                System.out.println("No existe ningún socio con esa localidad en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los socios por localidad: " + e.getMessage());
        }
    }
}
