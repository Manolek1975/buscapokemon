package com.buscapokemon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSearch;
    ImageView imagePokemon;
    TextView txtJson;
    TextView habilidades;
    ProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        imagePokemon = findViewById(R.id.imagePokemon);
        txtJson = findViewById(R.id.txtJson);
        habilidades = findViewById(R.id.habilidades);
        pd = findViewById(R.id.progressBar);

        btnSearch.setOnClickListener(v ->
                new JsonTask().execute("https://pokeapi.co/api/v2/pokemon/ditto"));
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
            String name, weight, height;


            try {
                JSONObject jsonObject = new JSONObject(result);
                LinearLayout lin = findViewById(R.id.linearLayout);
                name = jsonObject.getString("name");
                weight = jsonObject.getString("weight");
                height = jsonObject.getString("height");
                JSONArray abilities = jsonObject.getJSONArray("abilities");

                JSONObject sprites = jsonObject.getJSONObject("sprites");
                String sprite = sprites.getString("front_default");
                Drawable dw = Drawable.createFromPath(sprite);
                imagePokemon.setBackground(dw);

                for (int i=0; i<abilities.length(); i++){
                    JSONObject json_obj = abilities.getJSONObject(i);
                    JSONObject ability = json_obj.getJSONObject("ability");
                    String name_hab = ability.getString("name");
                    TextView habView = new TextView(MainActivity.this);
                    habView.setText(name_hab);
                    lin.addView(habView);
                }




                txtJson.setText(name + " " + weight + " " + height);



            } catch (JSONException e) {
                e.printStackTrace();
            }

            //txtJson.setText(result);
        }
    }
}