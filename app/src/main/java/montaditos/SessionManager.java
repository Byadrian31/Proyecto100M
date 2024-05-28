package montaditos;

/**
 * Gestiona la sesión de usuario.
 * @version 1.0.
 */
public class SessionManager {
    private static SessionManager instance;
    private int userId;

    /**
     * Constructor privado para evitar la creación de instancias externas.
     * Inicializa el ID de usuario en -1 para indicar que no hay sesión activa.
     */
    private SessionManager() {
        userId = -1;
    }

    /**
     * Obtiene la única instancia de SessionManager (patrón Singleton).
     *
     * @return La instancia única de SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Establece el ID del usuario activo.
     *
     * @param userId El ID del usuario activo.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Obtiene el ID del usuario activo.
     *
     * @return El ID del usuario activo.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Borra la sesión del usuario al restablecer el ID de usuario a -1.
     */
    public void clearSession() {
        userId = -1;
    }
}
