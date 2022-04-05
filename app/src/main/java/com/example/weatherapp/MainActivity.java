package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempratureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView SearchTV, iconTV, backTV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private  WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityName;
    private String API_KEY="";//paste your API key from www.weatherapi.com


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
        weatherRVModalArrayList=new ArrayList<>();
        weatherRVAdapter=new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        //mumbai
        //cityName=getCityName(19.089984632708717, 72.86950246388896);
        //Debugging
        //Log.d("CityName", cityName);
        cityName=getCityName(19.089984632708717, 72.86950246388896);
        getWeatherinfo(cityName);


        SearchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=cityEdt.getText().toString();

                if (city.isEmpty()){
                 Toast.makeText(MainActivity.this,"Please Enter a Valid city Name!",Toast.LENGTH_SHORT).show();
                }else{
                    getWeatherinfo(city);
                }
            }
        });


    }

    //Find CityName
    private String getCityName(double latitude , double longitude){
        String cityName="";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList=gcd.getFromLocation(latitude,longitude,10);
            for (Address adr: addressList) {
                if (adr != null) {
                    String city=adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName=city;
                        break;
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "User city Not found..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }

    //City Weather info
    private void getWeatherinfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key="+API_KEY+"&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();


                try {
                    String cityTemp=cityName.substring(0,1).toUpperCase() + cityName.substring(1).toLowerCase().trim();
                    cityNameTV.setText(cityTemp);
                    String temprature = response.getJSONObject("current").getString("temp_c");
                    tempratureTV.setText(temprature+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String icon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:"+icon).into(iconTV);
                    conditionTV.setText(condition);
                    if (isDay==1){

                        backTV.setBackgroundResource(R.drawable.morning);
                    }else{
                        backTV.setBackgroundResource(R.drawable.night);
                    }

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastDate= forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forecastDate.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourobj =hourArray.getJSONObject(i);
                        String time=hourobj.getString("time");
                        String temp=hourobj.getString("temp_c");
                        String img=hourobj.getJSONObject("condition").getString("icon");
                        String wind=hourobj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time,temp,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City Name",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Permission Granted!",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"Please Provide the permisson!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}