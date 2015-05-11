package cs.ycp.edu.cs481.ratemydrink.controllers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.rateMyDrink.modelClasses.Drink;

import java.io.IOException;

/**
 * A GSON TypeAdapter extension to serialize and deserialize Drink objects.
 */
public class DrinkAdapter extends TypeAdapter<Drink> {
    @Override
    public void write(JsonWriter writer, Drink drink) throws IOException {

        writer.beginObject();
            writer.name("drinkName").value(drink.getDrinkName());
            writer.name("description").value(drink.getDescription());
            writer.name("rating").value(drink.getRating());
            writer.name("id").value(String.valueOf(drink.getId()));
        writer.endObject();

    }

    @Override
    public Drink read(JsonReader reader) throws IOException {
        if(reader.peek() == JsonToken.NULL){
            reader.nextNull();
            return null;
        }

        Drink drink = new Drink();

        while(reader.hasNext()){
            String name = "";
            if(reader.peek() == JsonToken.NAME){
                name = reader.nextName();
            }

            if(name.equals("id")){
                drink.setId(reader.nextInt());
            }
            if(name.equals("description")){
                drink.setDescription(reader.nextString());
            }
            if(name.equals("drinkName")){
                drink.setDrinkName(reader.nextString());
            }
            if(name.equals("rating")){
                drink.setRating(Float.valueOf(reader.nextString()));
            }
        }

        return drink;
    }
}
