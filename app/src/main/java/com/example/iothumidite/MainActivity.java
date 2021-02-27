package com.example.iothumidite;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    final static int MIN = 300;
    final static int MAX = 700;
    final static int STEP = 5;
    final static String TARGET_HUMIDITY_REFERENCE = "Humidity_Controll/FC:F5:C4:09:73:DC";
    final static String SENSORS_DATA_REFERENCE = "/FC:F5:C4:09:73:DC";
    FirebaseDatabase database;
    DatabaseReference targetHumidity;
    DatabaseReference sensorsData;
    private int humidityValue = 0;
    TextView humidityWanted;
    TextView actualHumidityValue;
    TextView temperatureValue;
    TextView instantValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("IoT - Gestion de l'humidité ");

        this.humidityWanted = findViewById(R.id.humidityWanted);
        SeekBar humidityBar = findViewById(R.id.humidityBar);
        this.actualHumidityValue = findViewById(R.id.actualHumidityValue);
        this.temperatureValue = findViewById(R.id.temperatureValue);
        this.instantValue = findViewById(R.id.instantValue);
        humidityBar.setMax( (this.MAX - this.MIN) / this.STEP );
        //DatabaseReference lePif = database.getReference("Humidity_Controll");
        //Toast.makeText(MainActivity.this,"Valeur d'humidité souhaitée : "+ lePif.child("FC:F5:C4:09:73:DC").get().,
        //        Toast.LENGTH_SHORT).show();

        humidityBar.setOnSeekBarChangeListener(this);


        this.database = FirebaseDatabase.getInstance();

        this.targetHumidity = database.getReference(TARGET_HUMIDITY_REFERENCE);
        this.sensorsData = database.getReference(SENSORS_DATA_REFERENCE);

        this.targetHumidity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                humidityWanted.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Erreur de lecture de l'humidité cible : "+ databaseError.getCode(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        this.sensorsData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Query lastQuery = database.getReference().child(SENSORS_DATA_REFERENCE).orderByKey().limitToLast(1);
                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        System.out.println(ds.getRef());
                        DataSnapshot lastSensorsValue = ds.getChildren().iterator().next();
                        actualHumidityValue.setText(String.valueOf(lastSensorsValue.child("Humi").getValue()));
                        temperatureValue.setText(String.valueOf(lastSensorsValue.child("Temp").getValue()) + "°C");
                        instantValue.setText(String.valueOf(lastSensorsValue.child("Timestamp").getValue()));

                        int actualHumidityNumber = Integer.parseInt(actualHumidityValue.getText().toString());
                        int wantedHumidityNumber = Integer.parseInt(humidityWanted.getText().toString());
                        if(actualHumidityNumber < wantedHumidityNumber){
                            actualHumidityValue.setBackgroundColor(0xFF00BFFF);
                        }
                        else if (actualHumidityNumber > wantedHumidityNumber){
                            actualHumidityValue.setBackgroundColor(0xFFDC143C);
                        }
                        else {
                            actualHumidityValue.setBackgroundColor(0xFF3AD03F);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println(databaseError.toString());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Erreur de lecture de l'humidité cible : "+ databaseError.getCode(),
                        Toast.LENGTH_SHORT).show();

            }
        });


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

        //humidityWanted.setText(String.valueOf(humidityValue));
        this.targetHumidity.setValue(humidityValue);
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
