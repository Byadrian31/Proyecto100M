package montaditos;

/**
 * Representa un producto disponible en el menú.
 * @version 1.0.
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;

    /**
     * Constructor para un nuevo producto sin ID.
     *
     * @param nombre      El nombre del producto.
     * @param descripcion La descripción del producto.
     * @param precio      El precio del producto.
     */
    public Producto(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    /**
     * Constructor para un producto existente con ID.
     *
     * @param idProducto  El ID del producto.
     * @param nombre      El nombre del producto.
     * @param descripcion La descripción del producto.
     * @param precio      El precio del producto.
     */
    public Producto(int idProducto, String nombre, String descripcion, double precio) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    /**
     * Constructor para un producto existente con ID y cantidad.
     *
     * @param idProducto  El ID del producto.
     * @param nombre      El nombre del producto.
     * @param descripcion La descripción del producto.
     * @param precio      El precio del producto.
     * @param cantidad    La cantidad disponible del producto.
     */
    public Producto(int idProducto, String nombre, String descripcion, double precio, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el ID del producto.
     *
     * @return El ID del producto.
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la descripción del producto.
     *
     * @return La descripción del producto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return El precio del producto.
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Obtiene la cantidad disponible del producto.
     *
     * @return La cantidad disponible del producto.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece el precio del producto.
     *
     * @param precio El nuevo precio del producto.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Establece la cantidad disponible del producto.
     *
     * @param cantidad La nueva cantidad disponible del producto.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
