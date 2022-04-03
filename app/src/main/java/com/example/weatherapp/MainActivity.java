package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempratureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView SearchTV, iconTV, backTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeRL=findViewById(R.id.idRLHome);
        loadingPB=findViewById(R.id.idPBLoading);
        cityNameTV=findViewById(R.id.idTVCityName);
        tempratureTV=findViewById(R.id.idTVTemprature);
        conditionTV=findViewById(R.id.idTVcondition);
        weatherRV=findViewById(R.id.idTVweather);
        cityEdt=findViewById(R.id.idEdtCity);
        SearchTV=findViewById(R.id.idTVsearch);
        iconTV=findViewById(R.id.idTVIcon);
        backTV=findViewById(R.id.idVBack);

    }
}