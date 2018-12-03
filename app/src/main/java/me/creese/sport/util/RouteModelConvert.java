package me.creese.sport.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.creese.sport.models.RouteModel;

public class RouteModelConvert implements JsonDeserializer<RouteModel> {
    @Override
    public RouteModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        JsonArray points = jsonObject.get("points").getAsJsonArray();

        ArrayList<LatLng> p = new ArrayList<>();
        for (JsonElement point : points) {
            double latitude = point.getAsJsonObject().get("latitude").getAsDouble();
            double longitude = point.getAsJsonObject().get("longitude").getAsDouble();

            p.add(new LatLng(latitude,longitude));
        }


        return new RouteModel(jsonObject.get("name").getAsString(),p,jsonObject.get("time").getAsLong(), 0, false,0);
    }


}
