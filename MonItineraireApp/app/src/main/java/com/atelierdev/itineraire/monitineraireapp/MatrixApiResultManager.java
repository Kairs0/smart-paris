package com.atelierdev.itineraire.monitineraireapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnaud on 10/01/2018.
 */

public class MatrixApiResultManager {
    private String jsonStr;
    private List<List<Integer>> timesResult = new ArrayList<>();

    public MatrixApiResultManager(String json){
        this.jsonStr = json;
    }

    public List<List<Integer>> getTimesResult() {
        return timesResult;
    }

    public void calculTime() throws JSONException{

        JSONObject jsonObject = new JSONObject(this.jsonStr);
        JSONArray rows = jsonObject.getJSONArray("rows");
        List<List<Integer>> result = new ArrayList<>();
        for (int i=0; i < rows.length(); i++){
            JSONObject row = rows.getJSONObject(i);
            JSONArray elements = row.getJSONArray("elements");
            List<Integer> toElement = new ArrayList<>();
            for (int j=0; j < elements.length(); j++){
                JSONObject element = elements.getJSONObject(j);
                JSONObject duration = element.getJSONObject("duration");
                int valueInSec = duration.getInt("value");
                toElement.add(valueInSec);
            }
            result.add(toElement);
        }
        this.timesResult = result;
    }
}
