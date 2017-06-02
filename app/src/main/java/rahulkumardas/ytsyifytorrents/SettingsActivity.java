package rahulkumardas.ytsyifytorrents;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Rahul Kumar Das on 01-06-2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat auto;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Spinner spinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        auto = (SwitchCompat) findViewById(R.id.auto);
        spinner = (Spinner) findViewById(R.id.spans);
        spinner.setSelection(prefs.getInt("span", 0));
        if (prefs.getBoolean("auto", false) == true) {
            auto.setChecked(true);
        }
        editor = prefs.edit();
        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
//                    Toast.makeText(SettingsActivity.this, compoundButton.isChecked()+"", Toast.LENGTH_SHORT).show();
                    YtsApplication.AtoSearchEnabled = true;
                    editor.putBoolean("auto", true);
                    editor.commit();
                } else {
//                    Toast.makeText(SettingsActivity.this, compoundButton.isChecked()+"", Toast.LENGTH_SHORT).show();
                    YtsApplication.AtoSearchEnabled = false;
                    editor.putBoolean("auto", false);
                    editor.commit();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                YtsApplication.Span = i + 2;
                editor.putInt("span", i);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
