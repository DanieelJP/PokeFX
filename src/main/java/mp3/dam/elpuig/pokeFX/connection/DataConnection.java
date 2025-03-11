package mp3.dam.elpuig.pokeFX.connection;

import mp3.dam.elpuig.pokeFX.model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DataConnection {
    private static final Logger logger = Logger.getLogger(DataConnection.class.getName());
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static List<Pokemon> getPokemonData() {
        List<Pokemon> pokemons = new ArrayList<>();
        try {
            // Primero obtenemos la lista de Pokémon (limitada a 151 para la primera generación)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "pokemon?limit=151"))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                logger.severe("Error en la respuesta de la API: " + response.statusCode());
                throw new IOException("Error al obtener datos de la API: " + response.statusCode());
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject pokemonJson = results.getJSONObject(i);
                String url = pokemonJson.getString("url");
                
                // Obtener detalles de cada Pokémon
                HttpRequest pokemonRequest = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .build();
                
                HttpResponse<String> pokemonResponse = client.send(pokemonRequest, 
                        HttpResponse.BodyHandlers.ofString());
                
                if (pokemonResponse.statusCode() != 200) {
                    logger.warning("Error al obtener detalles del Pokémon: " + url);
                    continue;
                }

                JSONObject pokemonDetails = new JSONObject(pokemonResponse.body());
                
                // Extraer datos básicos
                int id = pokemonDetails.getInt("id");
                String nombre = pokemonDetails.getString("name");
                
                // Extraer tipos
                JSONArray types = pokemonDetails.getJSONArray("types");
                String tipoPrimario = types.getJSONObject(0)
                        .getJSONObject("type").getString("name");
                String tipoSecundario = types.length() > 1 ? 
                        types.getJSONObject(1).getJSONObject("type").getString("name") : null;
                
                // Extraer estadísticas
                JSONArray stats = pokemonDetails.getJSONArray("stats");
                int hp = stats.getJSONObject(0).getInt("base_stat");
                int ataque = stats.getJSONObject(1).getInt("base_stat");
                int defensa = stats.getJSONObject(2).getInt("base_stat");
                int ataqueEsp = stats.getJSONObject(3).getInt("base_stat");
                int defensaEsp = stats.getJSONObject(4).getInt("base_stat");
                int velocidad = stats.getJSONObject(5).getInt("base_stat");
                
                // Determinar generación basado en el ID
                int generacion = determinarGeneracion(id);
                
                // Crear y añadir el Pokémon a la lista
                Pokemon pokemon = new Pokemon(id, nombre, tipoPrimario, tipoSecundario,
                        generacion, hp, ataque, defensa, ataqueEsp, defensaEsp, 
                        velocidad, false);
                pokemons.add(pokemon);
                
                // Log de progreso
                logger.info("Pokémon cargado: " + nombre + " (" + (i + 1) + "/" + results.length() + ")");
            }
            
        } catch (Exception e) {
            logger.severe("Error al obtener datos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pokemons;
    }
    
    private static int determinarGeneracion(int id) {
        if (id <= 151) return 1;
        if (id <= 251) return 2;
        if (id <= 386) return 3;
        if (id <= 493) return 4;
        if (id <= 649) return 5;
        if (id <= 721) return 6;
        if (id <= 809) return 7;
        return 8;
    }

    public static Pokemon getPokemonById(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "pokemon/" + id))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                logger.warning("Error al obtener Pokémon ID " + id + ": " + response.statusCode());
                return null;
            }

            JSONObject pokemonDetails = new JSONObject(response.body());
            
            String nombre = pokemonDetails.getString("name");
            JSONArray types = pokemonDetails.getJSONArray("types");
            String tipoPrimario = types.getJSONObject(0).getJSONObject("type").getString("name");
            String tipoSecundario = types.length() > 1 ? 
                    types.getJSONObject(1).getJSONObject("type").getString("name") : null;
            
            JSONArray stats = pokemonDetails.getJSONArray("stats");
            int hp = stats.getJSONObject(0).getInt("base_stat");
            int ataque = stats.getJSONObject(1).getInt("base_stat");
            int defensa = stats.getJSONObject(2).getInt("base_stat");
            int ataqueEsp = stats.getJSONObject(3).getInt("base_stat");
            int defensaEsp = stats.getJSONObject(4).getInt("base_stat");
            int velocidad = stats.getJSONObject(5).getInt("base_stat");
            
            int generacion = determinarGeneracion(id);
            
            return new Pokemon(id, nombre, tipoPrimario, tipoSecundario,
                    generacion, hp, ataque, defensa, ataqueEsp, defensaEsp, 
                    velocidad, false);
                    
        } catch (Exception e) {
            logger.warning("Error al obtener Pokémon ID " + id + ": " + e.getMessage());
            return null;
        }
    }
} 