package com.buscapokemon;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface IPokemon {

    Pokemon getJSON(String json) throws JSONException;
    Bitmap getJSONImage(String json) throws JSONException;
    List<String> getJSONAbilities(String json) throws JSONException;
    List<String> getJSONItems(String json) throws  JSONException;

}
