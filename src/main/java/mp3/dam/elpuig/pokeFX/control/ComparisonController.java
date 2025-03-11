package mp3.dam.elpuig.pokeFX.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import mp3.dam.elpuig.pokeFX.connection.DataConnection;
import mp3.dam.elpuig.pokeFX.model.Pokemon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ComparisonController implements Initializable {
    @FXML private ComboBox<Pokemon> cmbPokemon1;
    @FXML private ComboBox<Pokemon> cmbPokemon2;
    @FXML private LineChart<String, Number> statsChart;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Pokemon> pokemons = DataConnection.getPokemonData();
        cmbPokemon1.getItems().addAll(pokemons);
        cmbPokemon2.getItems().addAll(pokemons);
    }
    
    @FXML
    private void compare() {
        statsChart.getData().clear();
        
        Pokemon p1 = cmbPokemon1.getValue();
        Pokemon p2 = cmbPokemon2.getValue();
        
        if (p1 != null && p2 != null) {
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName(p1.getNombre());
            
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.setName(p2.getNombre());
            
            // Añadir datos para cada estadística
            String[] stats = {"HP", "Ataque", "Defensa", "At.Esp", "Def.Esp", "Velocidad"};
            int[] values1 = {p1.getHp(), p1.getAtaque(), p1.getDefensa(), 
                           p1.getAtaqueEspecial(), p1.getDefensaEspecial(), p1.getVelocidad()};
            int[] values2 = {p2.getHp(), p2.getAtaque(), p2.getDefensa(), 
                           p2.getAtaqueEspecial(), p2.getDefensaEspecial(), p2.getVelocidad()};
            
            for (int i = 0; i < stats.length; i++) {
                series1.getData().add(new XYChart.Data<>(stats[i], values1[i]));
                series2.getData().add(new XYChart.Data<>(stats[i], values2[i]));
            }
            
            statsChart.getData().addAll(series1, series2);
        }
    }
} 