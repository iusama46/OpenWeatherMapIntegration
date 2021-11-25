package co.example.openweathermapintegration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import co.example.openweathermapintegration.models.Weather;


/**
 * Created by Ussama Iftikhar on 03-April-2021.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    final String APP_ID = "a2ae7e5a17634c9913f38aa855dbb769";
    final String SYMBOL = "°C";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    RelativeLayout lay;
    TextView temp, feelsTemp, minTemp, maxTemp, humidity;
    TextView pressure, windSpeed, description, clouds;
    ImageView tempIcon;
    LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lay = findViewById(R.id.lay);
        lay.setVisibility(View.INVISIBLE);
        temp = findViewById(R.id.temp);
        tempIcon = findViewById(R.id.img);
        feelsTemp = findViewById(R.id.feels_like);
        minTemp = findViewById(R.id.min_temp);
        maxTemp = findViewById(R.id.max_temp);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        windSpeed = findViewById(R.id.wind);
        description = findViewById(R.id.description);
        clouds = findViewById(R.id.clouds);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        //fusedLocationClient.getCurrentLocation()

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d("clima 5", String.valueOf(location.getLatitude()));
                        } else {
                            Log.d("clima", "String.valueOf(location.getLatitude()");

                            LocationRequest request = new LocationRequest().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setNumUpdates(1);

                            LocationCallback callback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    Log.d("clima22", String.valueOf(locationResult.getLastLocation().getLatitude()));
                                }

                                @Override
                                public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                                    super.onLocationAvailability(locationAvailability);
                                }


                            };

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            fusedLocationClient.requestLocationUpdates(request, callback, Looper.myLooper());
                        }
                    }
                });


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }


//
//
//


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting weather");
        progressDialog.show();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=toronto&appid=" + APP_ID + "&units=metric";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("clima", response);
                Gson gson = new GsonBuilder().create();
                Weather weather = gson.fromJson(response, Weather.class);

                lay.setVisibility(View.VISIBLE);
                progressDialog.hide();

                DecimalFormat df = new DecimalFormat("####0");
                temp.setText(df.format(weather.getMain().getTemp()).toString());

                minTemp.setText(String.format("%s%s", weather.getMain().getTempMin(), SYMBOL));
                maxTemp.setText(String.format("%s%s", weather.getMain().getTempMax(), SYMBOL));
                feelsTemp.setText(String.format("%s%s", weather.getMain().getFeelsLike(), SYMBOL));


                humidity.setText(String.format("%s%s", weather.getMain().getHumidity(), "%"));
                description.setText(weather.getWeather().get(0).getMain());

                windSpeed.setText(String.format("%s%s", weather.getWind().getSpeed(), "km/h"));
                pressure.setText(String.format("%s%s", weather.getMain().getPressure(), "mbar"));
                clouds.setText(String.format("%s%s", weather.getClouds().getAll(), "%"));


                String icon = weather.getWeather().get(0).getIcon();
                String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
                Picasso.get()
                        .load(iconUrl)
                        .placeholder(R.drawable.sun)
                        .resize(140, 140)
                        .into(tempIcon);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went gone wrong", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}