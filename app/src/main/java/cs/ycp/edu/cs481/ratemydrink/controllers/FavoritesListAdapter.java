package cs.ycp.edu.cs481.ratemydrink.controllers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.rateMyDrink.modelClasses.Favorite;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Josh on 5/8/2015.
 */
public class FavoritesListAdapter extends TypeAdapter<Favorite[]> {
    @Override
    public void write(JsonWriter writer, Favorite[] value) throws IOException {

    }

    @Override
    public Favorite[] read(JsonReader reader) throws IOException {

        ArrayList<Favorite> favoriteList = new ArrayList<Favorite>();

        while(reader.hasNext()){
            int drink = -1, user = -1;
            reader.beginObject();
                String field = "";
                if(reader.peek() == JsonToken.NAME){
                    field = reader.nextName();
                }
                if(field.equals("drinkId")){
                    drink = reader.nextInt();
                }
                if(field.equals("userId")){
                    user = reader.nextInt();
                }
            reader.endObject();
            if(drink >= 0 && user >= 0){
                favoriteList.add(new Favorite(drink, user));
            }

        }

        return (Favorite[]) favoriteList.toArray();
    }
}
