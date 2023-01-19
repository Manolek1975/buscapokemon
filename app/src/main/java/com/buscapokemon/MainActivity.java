package com.buscapokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    ImageButton btnSearch;
    ImageView imagePokemon;
    TextView nameView;
    TextView alturaView;
    TextView pesoView;
    ProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        imagePokemon = findViewById(R.id.imagePokemon);
        nameView = findViewById(R.id.namePoke);
        pesoView = findViewById(R.id.pesoPoke);
        alturaView = findViewById(R.id.alturaPoke);
        pd = findViewById(R.id.progressBar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnSearch.setOnClickListener(v ->
                new JsonTask().execute("https://pokeapi.co/api/v2/pokemon/pikachu"));
    }


    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.setVisibility(View.INVISIBLE);
            String name, peso, altura;

            try {
                JSONObject jsonObject = new JSONObject(result);
                LinearLayout lin = findViewById(R.id.linStats);
                name = jsonObject.getString("name");
                peso = jsonObject.getString("weight");
                altura = jsonObject.getString("height");
                JSONArray abilities = jsonObject.getJSONArray("abilities");
                JSONObject sprites = jsonObject.getJSONObject("sprites");

                String sprite = sprites.getString("front_default");
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(sprite).getContent());
                imagePokemon.setImageBitmap(bitmap);
                nameView.setText(name);
                pesoView.setText(String.format("Peso:%s", peso));
                alturaView.setText(String.format("Altura:%s", altura));

                for (int i = 0; i < abilities.length(); i++) {
                    JSONObject json_obj = abilities.getJSONObject(i);
                    JSONObject ability = json_obj.getJSONObject("ability");
                    String name_hab = ability.getString("name");
                    TextView habView = new TextView(MainActivity.this);
                    habView.setTextColor(Color.WHITE);
                    habView.setText(name_hab);
                    habView.setGravity(Gravity.CENTER);
                    lin.addView(habView);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}