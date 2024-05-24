package montaditos;

public class SessionManager {
    private static SessionManager instance;
    private int userId;

    private SessionManager() {
        // Inicializar el ID de usuario en -1 para indicar que no hay sesión activa
        userId = -1;
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void clearSession() {
        userId = -1;
    }
}
