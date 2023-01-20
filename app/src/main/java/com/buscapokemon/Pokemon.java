package com.buscapokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Pokemon implements IPokemon{

    private String name;
    private int height;
    private int weight;
    private String location;
    private Bitmap image;
    private List<String> abilities;
    private List<String> items;
    private List<String> stats;



    public Pokemon() {
        super();
    }

    @Override
    public Pokemon getJSON(String json) throws JSONException {
        Pokemon pokemon = new Pokemon();
        JSONObject jsonObject = new JSONObject(json);
        name = jsonObject.getString("name");
        weight = Integer.parseInt(jsonObject.getString("weight"));
        height = Integer.parseInt(jsonObject.getString("height"));
        location = jsonObject.getString("location_area_encounters");
        pokemon.setName(name);
        pokemon.setWeight(weight);
        pokemon.setHeight(height);
        pokemon.setLocation(location);
        pokemon.setImage(getJSONImage(json));
        pokemon.setAbilities(getJSONAbilities(json));
        pokemon.setItems(getJSONItems(json));
        return pokemon;
    }

    @Override
    public Bitmap getJSONImage(String json) throws JSONException {
        Bitmap bitmap = null;
        JSONObject jsonObject = new JSONObject(json);
        JSONObject sprites = jsonObject.getJSONObject("sprites");
        String sprite = sprites.getString("front_default");
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(sprite).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public List<String> getJSONAbilities(String json) throws JSONException {
        //List<Pokemon> pokemon = new ArrayList<>();
        abilities = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray js = jsonObject.getJSONArray("abilities");
        for (int i = 0; i < js.length(); i++) {
            JSONObject json_obj = js.getJSONObject(i);
            JSONObject ability = json_obj.getJSONObject("ability");
            String name_hab = ability.getString("name");
            abilities.add(name_hab);
        }
        return abilities;
    }

    @Override
    public List<String> getJSONItems(String json) throws JSONException {
        items = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray js = jsonObject.getJSONArray("held_items");
        for (int i = 0; i < js.length(); i++) {
            JSONObject json_obj = js.getJSONObject(i);
            JSONObject item = json_obj.getJSONObject("item");
            String name_form = item.getString("name");
            items.add(name_form);
        }
        return items;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public List<String> getStats() {
        return stats;
    }

    public void setStats(List<String> stats) {
        this.stats = stats;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> forms) {
        this.items = forms;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
