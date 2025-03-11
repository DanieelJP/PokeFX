package mp3.dam.elpuig.pokeFX.control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mp3.dam.elpuig.pokeFX.connection.DataConnection;
import mp3.dam.elpuig.pokeFX.model.Pokemon;
import mp3.dam.elpuig.pokeFX.util.PokemonStats;
import javafx.scene.web.WebView;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import java.util.HashMap;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;

public class MainWindow implements Initializable {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());
    
    private Scene scene;
    private Stage stage;
    private List<Pokemon> pokemons;

    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();

    @FXML
    private HBox hBox0;
    @FXML
    private VBox vBox0;
    @FXML
    private ComboBox<String> cmbTipoPokemon;
    @FXML private TabPane tabPane;
    @FXML private VBox basicStatsContainer;
    @FXML private VBox radarChartContainer;
    @FXML private Label lblTotalCount;
    
    private PokemonStats pokemonStats;

    @FXML private ProgressBar progressBar;
    @FXML private TableView<Pokemon> pokemonTable;
    @FXML private TableColumn<Pokemon, Integer> idColumn;
    @FXML private TableColumn<Pokemon, String> nombreColumn;
    @FXML private TableColumn<Pokemon, String> tipoColumn;
    @FXML private TableColumn<Pokemon, Integer> statsColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmbTipoPokemon.getItems().addAll("Todos", "fire", "water", "grass", "normal", 
                    "electric", "ice", "fighting", "poison", "ground", "flying", 
                    "psychic", "bug", "rock", "ghost", "dragon", "dark", 
                    "steel", "fairy");
            
            cmbTipoPokemon.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> handleFilter());

            // Configurar columnas de la tabla
            idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
            nombreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
            tipoColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoSecundario() == null ? 
                data.getValue().getTipoPrimario() : 
                data.getValue().getTipoPrimario() + "/" + data.getValue().getTipoSecundario()
            ));
            statsColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStatTotal()).asObject());

            // Añadir ordenamiento
            pokemonTable.getSortOrder().add(idColumn);
            
        } catch (Exception e) {
            logger.severe("Error en la inicialización: " + e.getMessage());
            manejarExcepciones(e);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void clickMenuItemLoad(ActionEvent event) {
        try {
            progressBar.setVisible(true);
            progressBar.setProgress(0);

            Thread loadThread = new Thread(() -> {
                try {
                    List<Pokemon> loadedPokemon = new ArrayList<>();
                    int totalPokemon = 151;
                    
                    for (int i = 1; i <= totalPokemon; i++) {
                        final int current = i;
                        Platform.runLater(() -> progressBar.setProgress((double) current / totalPokemon));
                        
                        Pokemon pokemon = DataConnection.getPokemonById(i);
                        if (pokemon != null) {
                            loadedPokemon.add(pokemon);
                        }
                    }

                    Platform.runLater(() -> {
                        pokemons = loadedPokemon;
                        progressBar.setVisible(false);
                        handleFilter();
                        pokemonTable.setItems(FXCollections.observableArrayList(pokemons));
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        manejarExcepciones(e);
                    });
                }
            });

            loadThread.start();

        } catch (Exception e) {
            logger.severe("Error al iniciar la carga: " + e.getMessage());
            manejarExcepciones(e);
        }
    }

    @FXML
    public void clickMenuItemClose(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void handleFilter() {
        try {
            if (pokemons == null) return;
            
            List<Pokemon> filtered = pokemons.stream()
                .filter(this::aplicarFiltros)
                .collect(Collectors.toList());
                
            actualizarGraficos(filtered);
            
            // Actualizar también la tabla
            pokemonTable.setItems(FXCollections.observableArrayList(filtered));
            
        } catch (Exception e) {
            logger.warning("Error al filtrar: " + e.getMessage());
            manejarExcepciones(e);
        }
    }

    private boolean aplicarFiltros(Pokemon p) {
        String tipo = cmbTipoPokemon.getValue();
        
        return tipo == null || tipo.equals("Todos") || 
                p.getTipoPrimario().equalsIgnoreCase(tipo) || 
                (p.getTipoSecundario() != null && p.getTipoSecundario().equalsIgnoreCase(tipo));
    }

    private void actualizarGraficos(List<Pokemon> filtered) {
        try {
            if (filtered == null || filtered.isEmpty()) {
                return;
            }

            // Limpiar gráficos existentes
            basicStatsContainer.getChildren().clear();

            // Crear nuevo gráfico de barras
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Estadísticas Promedio de Pokémon");
            barChart.setPrefWidth(780);
            barChart.setPrefHeight(400);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Estadísticas");

            // Calcular promedios
            Map<String, Double> promedios = new HashMap<>();
            promedios.put("HP", filtered.stream().mapToInt(Pokemon::getHp).average().orElse(0));
            promedios.put("Ataque", filtered.stream().mapToInt(Pokemon::getAtaque).average().orElse(0));
            promedios.put("Defensa", filtered.stream().mapToInt(Pokemon::getDefensa).average().orElse(0));
            promedios.put("At.Esp", filtered.stream().mapToInt(Pokemon::getAtaqueEspecial).average().orElse(0));
            promedios.put("Def.Esp", filtered.stream().mapToInt(Pokemon::getDefensaEspecial).average().orElse(0));
            promedios.put("Velocidad", filtered.stream().mapToInt(Pokemon::getVelocidad).average().orElse(0));

            // Añadir datos al gráfico
            promedios.forEach((stat, value) -> 
                series.getData().add(new XYChart.Data<>(stat, value))
            );

            barChart.getData().add(series);
            barChart.setAnimated(false);
            
            // Aplicar estilos
            barChart.getStyleClass().add("custom-chart");
            
            basicStatsContainer.getChildren().add(barChart);
            
            // Actualizar contador
            lblTotalCount.setText("Total: " + filtered.size() + " Pokémon");
            
            // Actualizar gráfico radar si está visible
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Gráfico Radar")) {
                actualizarGraficoRadar(filtered);
            }
            
        } catch (Exception e) {
            logger.severe("Error al actualizar gráficos: " + e.getMessage());
            manejarExcepciones(e);
        }
    }

    private void actualizarGraficoRadar(List<Pokemon> filtered) {
        try {
            radarChartContainer.getChildren().clear();

            // Crear un LineChart para simular un gráfico radar
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<Number, Number> radarChart = new LineChart<>(xAxis, yAxis);
            radarChart.setTitle("Estadísticas Promedio (Radar)");
            radarChart.setPrefWidth(780);
            radarChart.setPrefHeight(400);

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Estadísticas");

            // Calcular promedios
            double avgHP = filtered.stream().mapToInt(Pokemon::getHp).average().orElse(0);
            double avgAtk = filtered.stream().mapToInt(Pokemon::getAtaque).average().orElse(0);
            double avgDef = filtered.stream().mapToInt(Pokemon::getDefensa).average().orElse(0);
            double avgSpAtk = filtered.stream().mapToInt(Pokemon::getAtaqueEspecial).average().orElse(0);
            double avgSpDef = filtered.stream().mapToInt(Pokemon::getDefensaEspecial).average().orElse(0);
            double avgSpd = filtered.stream().mapToInt(Pokemon::getVelocidad).average().orElse(0);

            // Añadir puntos en forma circular
            double angle = 0;
            double angleStep = 2 * Math.PI / 6;
            
            series.getData().add(new XYChart.Data<>(avgHP * Math.cos(angle), avgHP * Math.sin(angle)));
            angle += angleStep;
            series.getData().add(new XYChart.Data<>(avgAtk * Math.cos(angle), avgAtk * Math.sin(angle)));
            angle += angleStep;
            series.getData().add(new XYChart.Data<>(avgDef * Math.cos(angle), avgDef * Math.sin(angle)));
            angle += angleStep;
            series.getData().add(new XYChart.Data<>(avgSpAtk * Math.cos(angle), avgSpAtk * Math.sin(angle)));
            angle += angleStep;
            series.getData().add(new XYChart.Data<>(avgSpDef * Math.cos(angle), avgSpDef * Math.sin(angle)));
            angle += angleStep;
            series.getData().add(new XYChart.Data<>(avgSpd * Math.cos(angle), avgSpd * Math.sin(angle)));
            
            // Cerrar el polígono
            series.getData().add(series.getData().get(0));

            radarChart.getData().add(series);
            radarChart.setCreateSymbols(false);
            radarChartContainer.getChildren().add(radarChart);

        } catch (Exception e) {
            logger.severe("Error al actualizar gráfico radar: " + e.getMessage());
            manejarExcepciones(e);
        }
    }

    @FXML
    private void showAdvancedStats(ActionEvent event) {
        try {
            if (pokemons == null || pokemons.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("No hay datos");
                alert.setContentText("Por favor, carga primero los datos de Pokémon.");
                alert.showAndWait();
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("Estadísticas Avanzadas");
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            
            TextArea statsArea = new TextArea();
            statsArea.setEditable(false);
            statsArea.setText(generarEstadisticasAvanzadas());
            
            content.getChildren().add(statsArea);
            
            Scene scene = new Scene(content, 600, 400);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            manejarExcepciones(e);
        }
    }

    private String generarEstadisticasAvanzadas() {
        // Implementa la lógica para generar estadísticas avanzadas
        return "Estadísticas avanzadas generadas aquí";
    }

    private void manejarExcepciones(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(e.getMessage());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        
        TextArea textArea = new TextArea(sw.toString());
        alert.getDialogPane().setExpandableContent(textArea);
        
        alert.showAndWait();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("PokéFX");
        alert.setContentText("Aplicación para análisis de estadísticas Pokémon\n" +
                           "Versión 1.0");
        alert.showAndWait();
    }
} 