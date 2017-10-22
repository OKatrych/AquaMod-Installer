package eu.warble.aquamodinstaller.ui.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.ui.main.search_mod.SearchModFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, SearchModFragment.newInstance())
                .commit();
    }
}
