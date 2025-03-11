package mp3.dam.elpuig.pokeFX.util;

import mp3.dam.elpuig.pokeFX.model.Pokemon;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PokemonStats {
    private final Map<String, DoubleSummaryStatistics> typeStats;
    private final Map<Integer, List<Pokemon>> generationStats;
    
    public PokemonStats(List<Pokemon> pokemons) {
        typeStats = pokemons.stream()
            .collect(Collectors.groupingBy(
                Pokemon::getTipoPrimario,
                Collectors.summarizingDouble(Pokemon::getStatTotal)
            ));
            
        generationStats = pokemons.stream()
            .collect(Collectors.groupingBy(Pokemon::getGeneracion));
    }
    
    public double getAverageStatsByType(String type) {
        return typeStats.get(type).getAverage();
    }
    
    public List<Pokemon> getPokemonsByGeneration(int gen) {
        return generationStats.getOrDefault(gen, Collections.emptyList());
    }
} 