package co.example.openweathermapintegration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;

import co.example.openweathermapintegration.models.Weather;


/**
 * Created by Ussama Iftikhar on 03-April-2021.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class MainActivity extends AppCompatActivity {

    RelativeLayout lay;
    TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lay = findViewById(R.id.lay);
        lay.setVisibility(View.INVISIBLE);
        temp = findViewById(R.id.temp);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting weather");
        progressDialog.show();

        String url = "http://api.openweathermap.org/data/2.5/weather?q=new%20york&appid=a2ae7e5a17634c9913f38aa855dbb769&units=imperial";
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

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went gone wrong", Toast.LENGTH_SHORT).show();


            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}