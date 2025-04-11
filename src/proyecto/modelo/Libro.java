package proyecto.modelo;

public class Libro {
/**
     * Consulta todos los libros de la base de datos biblioteca.db,
     * ordenados por título de forma ascendente.
     * @return lista de libros
     * @throws BDException
     */
    public static List<Libro> consultarLibros() throws BDException {

        List<Libro> listaLibros = new ArrayList<Libro>();
        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            // Conexión a la base de datos
            conexion = ConfigMySql.abrirConexion();

            String query = "SELECT * FROM libro ORDER BY titulo ASC";
            ps = conexion.prepareStatement(query);
            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                Libro libro = new Libro(
                        resultados.getInt("codigo"),
                        resultados.getString("isbn"),
                        resultados.getString("titulo"),
                        resultados.getString("escritor"),
                        resultados.getInt("ano_publicacion"),
                        resultados.getDouble("puntuacion")
                );
                listaLibros.add(libro);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return listaLibros;
    }

    /**
     * Inserta un nuevo libro en la base de datos biblioteca.db.
     * @param libro el libro a insertar
     * @throws BDException
     */
    public static void insertarLibro(Libro libro) throws BDException {
        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            // Conexión a la base de datos
            conexion = ConfigMySql.abrirConexion();

            String query = "INSERT INTO libro (isbn, titulo, escritor, ano_publicacion, puntuacion) VALUES (?, ?, ?, ?, ?)";
            ps = conexion.prepareStatement(query);
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getEscritor());
            ps.setInt(4, libro.getAnoPublicacion());
            ps.setDouble(5, libro.getPuntuacion());

            int rowsAffected = ps.executeUpdate();
            conexion.commit();

            if (rowsAffected > 0) {
                System.out.println("Se ha insertado un libro en la base de datos.");
            } else {
                System.out.println("No se pudo insertar el libro.");
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
    }

    /**
     * Elimina un libro de la base de datos, por código.
     * @param codigo el código del libro a eliminar
     * @throws BDException
     */
    public static void eliminarLibro(int codigo) throws BDException {
        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            // Conexión a la base de datos
            conexion = ConfigMySql.abrirConexion();

            // Verificar si el libro está referenciado en algún préstamo
            String checkPrestamoQuery = "SELECT COUNT(*) FROM prestamo WHERE codigo_libro = ?";
            ps = conexion.prepareStatement(checkPrestamoQuery);
            ps.setInt(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.getInt(1) > 0) {
                System.out.println("El libro está referenciado en un préstamo de la base de datos.");
                return;
            }

            // Eliminar el libro
            String deleteQuery = "DELETE FROM libro WHERE codigo = ?";
            ps = conexion.prepareStatement(deleteQuery);
            ps.setInt(1, codigo);

            int rowsAffected = ps.executeUpdate();
            conexion.commit();

            if (rowsAffected > 0) {
                System.out.println("Se ha eliminado un libro de la base de datos.");
            } else {
                System.out.println("No existe ningún libro con ese código en la base de datos.");
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
    }

    /**
     * Consulta un libro por código.
     * @param codigo el código del libro
     * @return el libro encontrado
     * @throws BDException
     */
    public static Libro consultarLibroPorCodigo(int codigo) throws BDException {
        Libro libro = null;
        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            // Conexión a la base de datos
            conexion = ConfigMySql.abrirConexion();

            String query = "SELECT * FROM libro WHERE codigo = ?";
            ps = conexion.prepareStatement(query);
            ps.setInt(1, codigo);
            ResultSet resultados = ps.executeQuery();

            if (resultados.next()) {
                libro = new Libro(
                        resultados.getInt("codigo"),
                        resultados.getString("isbn"),
                        resultados.getString("titulo"),
                        resultados.getString("escritor"),
                        resultados.getInt("ano_publicacion"),
                        resultados.getDouble("puntuacion")
                );
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libro;
    }
}
