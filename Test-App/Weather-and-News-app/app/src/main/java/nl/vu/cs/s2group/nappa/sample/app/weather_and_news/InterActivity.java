package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.NappaLifecycleObserver;

public class InterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NappaLifecycleObserver(this));
        setContentView(R.layout.activity_inter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button kabulButton = findViewById(R.id.button_kabul);

        kabulButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("capital", "Kabul");
            Nappa.notifyExtra("capital", "Kabul");
            startActivity(intent);
        });
        Button sessionButton = findViewById(R.id.button_session);
        sessionButton.setText("CapitalList");
        sessionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CapitalListActivity.class);
            startActivity(intent);
        });

        Button graphButton = findViewById(R.id.button_news);

        graphButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        });
    }

}
