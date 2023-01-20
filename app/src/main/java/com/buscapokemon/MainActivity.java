package com.buscapokemon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton btnSearch;
    ImageView imagePokemon;
    TextView errorView;
    TextView nameView;
    TextView alturaView;
    TextView pesoView;
    TextView locationPoke;
    TextView habText;
    TextView itemsText;
    TextView statText;
    ProgressBar pd;
    EditText editPokemon;
    String pokemon;
    String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPokemon = findViewById(R.id.editPokemon);
        btnSearch = findViewById(R.id.btnSearch);
        errorView = findViewById(R.id.errorPoke);
        imagePokemon = findViewById(R.id.imagePokemon);
        nameView = findViewById(R.id.namePoke);
        pesoView = findViewById(R.id.pesoPoke);
        alturaView = findViewById(R.id.alturaPoke);
        locationPoke = findViewById(R.id.locationPoke);
        habText = findViewById(R.id.habText);
        itemsText = findViewById(R.id.itemsText);
        //statText = findViewById(R.id.statsText);

        pd = findViewById(R.id.progressBar);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnSearch.setOnClickListener(view -> {
            hideSoftKeyboard(this);
            pokemon = editPokemon.getText().toString();
            if (!pokemon.isEmpty())
                new JsonTask().execute("https://pokeapi.co/api/v2/pokemon/" + pokemon.toLowerCase());
            editPokemon.setText("");
            errorView.setText("");
        });

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
            if (result == null) {
                errorView.setText(String.format("No existe el pokemon %s", pokemon));
                pd.setVisibility(View.INVISIBLE);
                return;
            }
            super.onPostExecute(result);
            pd.setVisibility(View.INVISIBLE);
            habText.setVisibility(View.VISIBLE);
            itemsText.setVisibility(View.VISIBLE);
            errorView.setText("");

            Pokemon pokemon = new Pokemon();
            try {
                pokemon = pokemon.getJSON(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameView.setText(pokemon.getName());
            pesoView.setText(String.format("Peso: %s", pokemon.getWeight()));
            alturaView.setText(String.format("Altura: %s", pokemon.getHeight()));
            imagePokemon.setImageBitmap(pokemon.getImage());

/*            locationPoke.setClickable(true);
            locationPoke.setMovementMethod(LinkMovementMethod.getInstance());
            locationPoke.setText(Html.fromHtml(pokemon.getLocation(), Html.FROM_HTML_MODE_COMPACT));*/

            LinearLayout habLin = findViewById(R.id.habilidades);
            habLin.removeAllViews();
            List<String> listAbilities = pokemon.getAbilities();
            for (String ability : listAbilities){
                TextView habView = new TextView(MainActivity.this);
                habView.setTextColor(Color.WHITE);
                habView.setText(ability);
                habView.setGravity(Gravity.START);
                habLin.addView(habView);
            }

            LinearLayout itemsLin = findViewById(R.id.items);
            itemsLin.removeAllViews();
            List<String> listItems = pokemon.getItems();
            for (String item : listItems){
                TextView statView = new TextView(MainActivity.this);
                statView.setTextColor(Color.WHITE);
                statView.setText(item);
                statView.setGravity(Gravity.START);
                itemsLin.addView(statView);
            }


/*            try {

                JSONObject jsonObject = new JSONObject(result);
                name = jsonObject.getString("name");
                peso = jsonObject.getString("weight");
                altura = jsonObject.getString("height");
                JSONArray abilities = jsonObject.getJSONArray("abilities");
                JSONArray stats = jsonObject.getJSONArray("stats");
                JSONObject sprites = jsonObject.getJSONObject("sprites");

                String sprite = sprites.getString("front_default");
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(sprite).getContent());
                imagePokemon.setImageBitmap(bitmap);
                nameView.setText(name);
                pesoView.setText(String.format("Peso: %s", peso));
                alturaView.setText(String.format("Altura: %s", altura));

                TextView habText = findViewById(R.id.habilidadesText);
                TextView statText = findViewById(R.id.statsText);
                habText.setVisibility(View.VISIBLE);
                statText.setVisibility(View.VISIBLE);
                LinearLayout lin = findViewById(R.id.habilidades);
                LinearLayout lin_stat = findViewById(R.id.stats);
                lin.removeAllViews();
                lin_stat.removeAllViews();

                for (int i = 0; i < abilities.length(); i++) {
                    JSONObject json_obj = abilities.getJSONObject(i);
                    JSONObject ability = json_obj.getJSONObject("ability");
                    String name_hab = ability.getString("name");
                    TextView habView = new TextView(MainActivity.this);
                    habView.setTextColor(Color.WHITE);
                    habView.setText(name_hab);
                    habView.setGravity(Gravity.START);
                    lin.addView(habView);
                }

                for (int i = 0; i < stats.length(); i++) {
                    JSONObject json_obj = stats.getJSONObject(i);
                    JSONObject stat = json_obj.getJSONObject("stat");
                    String name_stat = stat.getString("name");
                    TextView statView = new TextView(MainActivity.this);
                    statView.setTextColor(Color.WHITE);
                    statView.setText(name_stat);
                    statView.setGravity(Gravity.START);
                    lin_stat.addView(statView);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}