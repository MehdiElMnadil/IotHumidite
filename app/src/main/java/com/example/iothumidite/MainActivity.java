package com.example.iothumidite;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    final static int MIN = 300;
    final static int MAX = 700;
    final static int STEP = 5;
    final static String DATABASE_ADDRESS = "https://projet-iot9-default-rtdb.firebaseio.com/";

    private int humidityValue = 0;
    TextView humidityWanted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_ADDRESS);

        humidityWanted = findViewById(R.id.humidityWanted);
        SeekBar humidityBar = findViewById(R.id.humidityBar);
        humidityBar.setMax( (this.MAX - this.MIN) / this.STEP );
        DatabaseReference lePif = database.getReference("Humidity_Controll");
        Toast.makeText(MainActivity.this,"Valeur d'humidité souhaitée : "+ lePif.child("FC:F5:C4:09:73:DC").getKey(),
                Toast.LENGTH_SHORT).show();

        humidityBar.setOnSeekBarChangeListener(this);
    }


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        humidityValue = MIN + (progress * STEP);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(MainActivity.this,"Valeur d'humidité souhaitée : "+humidityValue,
                Toast.LENGTH_SHORT).show();

        humidityWanted.setText(String.valueOf(humidityValue));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
