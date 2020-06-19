package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.APIResourceUtil;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

public class PokemonActivity extends AppCompatActivity {
    Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        PokemonAPI.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Pokemon pokemon) {
        this.pokemon = pokemon;
        setPageTitle();
        setPokemonCharacteristics();
        setPokemonStats();
        setNamedAPIResourceList(R.id.ll_pokemon_abilities, pokemon.abilities, "getAbility");
        setNamedAPIResourceList(R.id.ll_pokemon_types, pokemon.types, "getType");
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(pokemon.name);
    }

    private void setPokemonCharacteristics() {
        String baseXp = String.format(Config.LOCALE, "%d", pokemon.base_experience) + " xp";
        ((TextView) findViewById(R.id.tv_pokemon_base_experience)).setText(baseXp);

        String height = String.format(Config.LOCALE, "%d", pokemon.height) + " dm";
        ((TextView) findViewById(R.id.tv_pokemon_height)).setText(height);

        String weight = String.format(Config.LOCALE, "%d", pokemon.weight) + " hg";
        ((TextView) findViewById(R.id.tv_pokemon_weight)).setText(weight);

        int isDefaultTextId = pokemon.is_default ? R.string.yes : R.string.no;
        String isDefault = getResources().getString(isDefaultTextId);
        ((TextView) findViewById(R.id.tv_pokemon_species_default)).setText(isDefault);
    }

    private void setPokemonStats() {
        runOnUiThread(() -> {
            LinearLayoutCompat layout = findViewById(R.id.ll_pokemon_stats);
            for (PokemonStat pokemonStat : pokemon.stats) {
                TextView tvStatLabel = ViewUtil.createTextView(this, pokemonStat.getStat().getName(), 0.5f);
                String stateValueStr = String.format(Config.LOCALE, "%d", pokemonStat.getBase_stat()) +
                        " (" + String.format(Config.LOCALE, "%d", pokemonStat.effort) + " EV)";
                TextView tvStatValue = ViewUtil.createTextView(this, stateValueStr, 0.5f);

                LinearLayoutCompat rowLayout = new LinearLayoutCompat(this, null);
                rowLayout.addView(tvStatLabel);
                rowLayout.addView(tvStatValue);

                layout.addView(rowLayout);
            }
        });
    }

    private void setNamedAPIResourceList(int viewId, List<?> list, String getterMethod) {
        List<NamedAPIResource> namedAPIResourceList = APIResourceUtil.parseListToNamedAPOResourceList(list, getterMethod);
        runOnUiThread(() -> {
            LinearLayoutCompat linearLayout = findViewById(viewId);
            for (NamedAPIResource namedAPIResource : namedAPIResourceList) {
                linearLayout.addView(ViewUtil.createTextView(this, namedAPIResource.getName()));
            }
        });
    }
}