package controllers;

import com.rateMyDrink.modelClasses.Drink;

import java.sql.SQLException;

import persist.DatabaseProvider;
import persist.IDatabase;

/**
 * Created by shanembonner on 5/10/15.
 */
public class GetDrink {
    public Drink getDrink(int id) throws SQLException {
        IDatabase db = DatabaseProvider.getInstance();
        return db.getDrink(id);
    }
}
