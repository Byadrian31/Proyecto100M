package montaditos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Horario {
    private final SimpleIntegerProperty idUsuario;
    private final SimpleStringProperty diaSemana;
    private final SimpleStringProperty franjaHoraria;

    public Horario(int idUsuario, String diaSemana, String franjaHoraria) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.diaSemana = new SimpleStringProperty(diaSemana);
        this.franjaHoraria = new SimpleStringProperty(franjaHoraria);
    }

    public int getIdUsuario() {
        return idUsuario.get();
    }

    public SimpleIntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public String getDiaSemana() {
        return diaSemana.get();
    }

    public SimpleStringProperty diaSemanaProperty() {
        return diaSemana;
    }

    public String getFranjaHoraria() {
        return franjaHoraria.get();
    }

    public SimpleStringProperty franjaHorariaProperty() {
        return franjaHoraria;
    }
}
