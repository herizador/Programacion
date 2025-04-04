package proyecto.modelo;

public class Prestamo {
  private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws BDException {
        private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws BDException {
		int opcion;
		do {
			mostrarMenu();
			opcion = leerEntero("Seleccione una opción: ");
			switch (opcion) {
			case 1:
				insertarPrestamo();
				break;
			case 2:
				actualizarPrestamo();
				break;
			case 3:
				eliminarPrestamo();
				break;
			case 4:
				consultarPrestamos();
				break;
			case 5:
				consultarPrestamosNoDevueltos();
				break;
			case 6:
				consultarPrestamosPorFecha();
				break;
			case 0:
				System.out.println("Saliendo del programa...");
				break;
			default:
				System.out.println("La opción de menú debe estar comprendida entre 0 y 6.");
			}
		} while (opcion != 0);
	}

	private static void mostrarMenu() {
		System.out.println("--- Menú de Gestión de Préstamos ---");
		System.out.println("1) Insertar un préstamo");
		System.out.println("2) Actualizar un préstamo");
		System.out.println("3) Eliminar un préstamo");
		System.out.println("4) Consultar todos los préstamos");
		System.out.println("5) Consultar préstamos no devueltos");
		System.out.println("6) Consultar préstamos por fecha");
		System.out.println("0) Salir");
	}

	private static int leerEntero(String mensaje) {
		System.out.print(mensaje);
		while (!scanner.hasNextInt()) {
			System.out.println("Entrada inválida. Introduzca un número entero.");
			scanner.next();
		}
		return scanner.nextInt();
	}

	private static void insertarPrestamo() throws BDException {
		Connection conexion = null;
		try {
			conexion = ConfigMySql.abrirConexion();
			System.out.print("Ingrese el usuario: ");
			int CodUsuario = scanner.nextInt();
			System.out.print("Ingrese el libro: ");
			int CodLibro = scanner.nextInt();
			scanner.nextLine();
			
			// Obtener fecha actual en formato yyyy-MM-dd
			LocalDate fechaActual = LocalDate.now();
			String fechaInicioPrestamo = fechaActual.toString();
			
			System.out.print("Ingrese la fecha fin del préstamo (YYYY-MM-DD): ");
			String fechaFinPrestamo = scanner.nextLine();

			String sql = "INSERT INTO prestamos (codigo_libro, codigo_socio,fecha_inicio,fecha_fin,fecha_devolucion) VALUES (?, ?, ?, ?,null)";
			PreparedStatement statement = conexion.prepareStatement(sql);
			statement.setInt(1, CodLibro);
			statement.setInt(2, CodUsuario);
			statement.setString(3, fechaInicioPrestamo);
			statement.setString(4, fechaFinPrestamo);

			int filas = statement.executeUpdate();
			if (filas >= 0) {
				System.out.println("Préstamo agregado correctamente.");
			} else {
				System.out.println("No se pudo agregar el préstamo.");
			}

			statement.close();
		} catch (BDException bd) {
			throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySql.cerrarConexion(conexion);
			}
		}
	}

	private static void actualizarPrestamo() throws BDException {
		Connection conexion = null;
		try {
			conexion = ConfigMySql.abrirConexion();
			System.out.print("Ingrese el codigo del Libro: ");
			int CodLibro = scanner.nextInt();
			System.out.print("Ingrese el codigo del Usuario: ");
			int CodUsuario = scanner.nextInt();
			scanner.nextLine();
			System.out.print("Ingrese la fecha de inicio: ");
			String fechaPrestamo = scanner.nextLine();
			System.out.print("Ingrese la fecha de devolucion: ");
			String devuelto = scanner.nextLine();

			String sql = "UPDATE prestamos SET fecha_devolucion = ? WHERE codigo_libro = ? and codigo_socio = ? and fecha_inicio = ?";
			PreparedStatement statement = conexion.prepareStatement(sql);
			statement.setString(1, devuelto);
			statement.setInt(2, CodLibro);
			statement.setInt(3, CodUsuario);
			statement.setString(4, fechaPrestamo);

			int filas = statement.executeUpdate();
			if (filas > 0) {
				System.out.println("Préstamo actualizado correctamente.");
			} else {
				System.out.println("No se encontró el préstamo.");
			}

			statement.close();
		} catch (BDException bd) {
			throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
		} finally {
			if (conexion != null) {
				ConfigMySql.cerrarConexion(conexion);
			}
		}
	}

	private static void eliminarPrestamo() throws BDException {
			Connection conexion = null;
			try {
				conexion = ConfigMySql.abrirConexion();
				System.out.print("Ingrese el ID del préstamo a eliminar: ");
				int id = scanner.nextInt();

				String sql = "DELETE FROM prestamos WHERE codigo_libro = ?";
				PreparedStatement statement = conexion.prepareStatement(sql);
				statement.setInt(1, id);

				int filas = statement.executeUpdate();
				if (filas > 0) {
					System.out.println("Préstamo eliminado correctamente.");
				} else {
					System.out.println("No se encontró el préstamo.");
				}

				statement.close();
			} catch (BDException bd) {
				throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
			} catch (SQLException e) {
				throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
			} finally {
				if (conexion != null) {
					ConfigMySql.cerrarConexion(conexion);
				}
			}
		}

	private static void consultarPrestamos() throws BDException {
		Connection conexion = null;
		try {
			conexion = ConfigMySql.abrirConexion();
			String sql = "SELECT * FROM prestamos";
			PreparedStatement statement = conexion.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			System.out.println("\n--- Lista de Préstamos ---");
			while (rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + ", Usuario: " + rs.getString("usuario") + ", Libro: "
						+ rs.getString("libro") + ", Fecha Préstamo: " + rs.getDate("fecha_prestamo") + ", Devuelto: "
						+ rs.getBoolean("devuelto"));
			}

			rs.close();
			statement.close();
		} catch (BDException bd) {
			throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
		} finally {
			ConfigMySql.cerrarConexion(conexion);
		}
	}

	private static void consultarPrestamosNoDevueltos() throws BDException {
		Connection conexion = null;
		try {
			conexion = ConfigMySql.abrirConexion();
			String sql = "SELECT * FROM prestamos WHERE devuelto = false";
			PreparedStatement statement = conexion.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			System.out.println("\n--- Préstamos No Devueltos ---");
			while (rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + ", Usuario: " + rs.getString("usuario") + ", Libro: "
						+ rs.getString("libro") + ", Fecha Préstamo: " + rs.getDate("fecha_prestamo"));
			}

			rs.close();
			statement.close();
		} catch (BDException bd) {
			throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
		} finally {
			ConfigMySql.cerrarConexion(conexion);
		}
	}

	private static void consultarPrestamosPorFecha() throws BDException {
		Connection conexion = null;
		try {
			conexion = ConfigMySql.abrirConexion();
			System.out.print("Ingrese la fecha a consultar (YYYY-MM-DD): ");
			String fecha = scanner.next();

			String sql = "SELECT * FROM prestamos WHERE fecha_prestamo = ?";
			PreparedStatement statement = conexion.prepareStatement(sql);
			statement.setString(1, fecha);
			ResultSet rs = statement.executeQuery();

			System.out.println("\n--- Préstamos en la Fecha " + fecha + " ---");
			while (rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + ", Usuario: " + rs.getString("usuario") + ", Libro: "
						+ rs.getString("libro") + ", Devuelto: " + rs.getBoolean("devuelto"));
			}

			rs.close();
			statement.close();
		} catch (BDException bd) {
			throw new BDException(BDException.ERROR_QUERY + bd.getMessage());
		} catch (SQLException e) {
			throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
		} finally {
			ConfigMySql.cerrarConexion(conexion);
		}
	}
}
