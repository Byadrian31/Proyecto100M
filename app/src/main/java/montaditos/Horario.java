package montaditos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Representa un horario de trabajo para un usuario.
 * @version 1.0.
 */
public class Horario {
    private final SimpleIntegerProperty idUsuario;
    private final SimpleStringProperty diaSemana;
    private final SimpleStringProperty franjaHoraria;

    /**
     * Constructor de la clase Horario.
     *
     * @param idUsuario      El ID del usuario al que pertenece el horario.
     * @param diaSemana      El día de la semana del horario.
     * @param franjaHoraria  La franja horaria del horario.
     */
    public Horario(int idUsuario, String diaSemana, String franjaHoraria) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.diaSemana = new SimpleStringProperty(diaSemana);
        this.franjaHoraria = new SimpleStringProperty(franjaHoraria);
    }

    /**
     * Obtiene el ID del usuario al que pertenece el horario.
     *
     * @return El ID del usuario.
     */
    public int getIdUsuario() {
        return idUsuario.get();
    }

    /**
     * Obtiene la propiedad del ID del usuario.
     *
     * @return La propiedad del ID del usuario.
     */
    public SimpleIntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    /**
     * Obtiene el día de la semana del horario.
     *
     * @return El día de la semana.
     */
    public String getDiaSemana() {
        return diaSemana.get();
    }

    /**
     * Obtiene la propiedad del día de la semana.
     *
     * @return La propiedad del día de la semana.
     */
    public SimpleStringProperty diaSemanaProperty() {
        return diaSemana;
    }

    /**
     * Obtiene la franja horaria del horario.
     *
     * @return La franja horaria.
     */
    public String getFranjaHoraria() {
        return franjaHoraria.get();
    }

    /**
     * Obtiene la propiedad de la franja horaria.
     *
     * @return La propiedad de la franja horaria.
     */
    public SimpleStringProperty franjaHorariaProperty() {
        return franjaHoraria;
    }
}
