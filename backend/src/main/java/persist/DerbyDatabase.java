package persist;

import com.rateMyDrink.modelClasses.Beer;
import com.rateMyDrink.modelClasses.BeerType;
import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.DrinkType;
import com.rateMyDrink.modelClasses.Favorite;
import com.rateMyDrink.modelClasses.Ingredient;
import com.rateMyDrink.modelClasses.Liquor;
import com.rateMyDrink.modelClasses.LiquorType;
import com.rateMyDrink.modelClasses.MixedDrink;
import com.rateMyDrink.modelClasses.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanembonner on 1/20/15.
 */
public class DerbyDatabase implements IDatabase {
    static{
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (Exception e) {
            throw new IllegalStateException("Could not load derby driver");
        }
    }

    private static final int MAX_ATTEMPTS = 10;
    private static final String DB_DIRECTORY = "Users/shanembonner/rateMyDrinkDB/rateMyDrink.db";
    //private static final String DB_DIRECTORY = "rateMyDrinkDB/rateMyDrink.db"; //josh's
    private static final String DB_USER_TABLENAME = "userList";
    private static final String DB_MAIN_DRINK_TABLENAME = "mainDrinkTable";
    private static final String DB_BEER_TABLENAME = "beerTable";
    private static final String DB_MIXED_DRINK_TABLENAME = "mixedDrinkTable";
    private static final String DB_LIQUOR_TABLENAME = "liquorTable";
    private static final String DB_COMMENT_TABLENAME = "commentTable";
    private static final String DB_INGREDIENTS_TABLENAME = "ingredientsTable";
    private static final String DB_FAVORITES_TABLENAME = "favoritesTable";


    @Override
    public boolean addNewBeer(final Beer beer) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet generatedKeys = null;

                try{
                    //type cast the object so that it can be stored in the main database, with a link to the subclass database
                    Drink tempDrink = (Drink) beer;
                    stmt = conn.prepareStatement(
                            "insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating, numRatings, drinkType) values (?,?,?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    tempDrink.setDrinkType(DrinkType.BEER);
                    storeDrinkNoId(tempDrink, stmt, 1);
                    stmt.executeUpdate();

                    //determine auto-generated id
                    generatedKeys = stmt.getGeneratedKeys();
                    if(!generatedKeys.next()){
                        throw new SQLException("Could not get auto-generated key for inserted Drink");
                    }

                    //id is used to link the drink in the main table to an item in the subclass table
                    int drinkId = generatedKeys.getInt(1);
                    tempDrink.setId(drinkId);

                    stmt2 = conn.prepareStatement(
                            "insert into " + DB_BEER_TABLENAME + "(drinkId, cals, abv, beerType) values (?,?,?,?)"

                    );

                    storeBeerNoId(beer, stmt2, 1);
                    stmt2.executeUpdate();
                    return true;
                }finally{
                    DBUtil.closeQuietly(generatedKeys);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }
            }
        });
    }

    @Override
    public boolean addNewComment(final Comment comment) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {

            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;

                try{
                    stmt = conn.prepareStatement(
                            "insert into " + DB_COMMENT_TABLENAME + "(drinkId, userName, comment) values (?,?,?)"
                    );

                    storeCommentNoId(comment, stmt, 1);
                    stmt.executeUpdate();

                    return true;
                }finally{
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    //unused function
    @Override
    public boolean addNewDrink(final Drink drink) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet generatedKeys = null;

                try{
                    stmt = conn.prepareStatement(
                            "insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating, numRatings, drinkType) values (?,?,?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    storeDrinkNoId(drink, stmt, 1);

                    //attempt to insert the drink
                    stmt.executeUpdate();

                    //determine auto-generated id
                    generatedKeys = stmt.getGeneratedKeys();
                    if(!generatedKeys.next()){
                        throw new SQLException("Could not get auto-generated key for inserted Drink");

                    }

                    //id is used to link the drink in the main table to an item in the subclass table
                    int drinkId = generatedKeys.getInt(1);
                    drink.setId(drinkId);

                    //Determine which of the subclasses the new drink is, and store it in the correct
                    //sub-table with the corresponding variables and storeNoId function call
                    if(drink instanceof Beer){
                        Beer tempBeer = (Beer) drink;

                        stmt2 = conn.prepareStatement(
                                "insert into " + DB_BEER_TABLENAME + "(drinkId, cals, abv, beerType) values (?,?,?,?)"

                        );
                        storeBeerNoId(tempBeer, stmt, 1);
                        stmt2.executeUpdate();
                    }

                    if(drink instanceof MixedDrink){
                        //TODO:implement MixedDrink
                    }

                    if(drink instanceof Liquor){
                        Liquor tempLiquor = (Liquor) drink;
                        stmt2 = conn.prepareStatement(
                                "insert into " + DB_LIQUOR_TABLENAME + "(drinkId, content, liquorType) values (?,?,?)"

                        );
                        storeLiquorNoId(tempLiquor, stmt, 1);
                        stmt2.executeUpdate();
                    }

                    return true;
                }finally{
                    DBUtil.closeQuietly(generatedKeys);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }
            }
        });
    }

    @Override
    public boolean addNewFavorite(final Favorite fav) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;

                try{
                    stmt = conn.prepareStatement(
                            "insert into " + DB_FAVORITES_TABLENAME + "(userId, drinkId) values (?,?)"
                    );

                    storeFavoriteNoId(fav, stmt, 1);
                    stmt.executeUpdate();

                    return true;
                }finally{
                    DBUtil.closeQuietly(stmt);
                }

            }
        });
    }
    @Override
    public boolean addNewLiquor(final Liquor liquor) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet generatedKeys = null;

                try{
                    //type cast the object so that it can be stored in the main database, with a link to the subclass database
                    Drink tempDrink = (Drink) liquor;
                    stmt = conn.prepareStatement(
                            "insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating, numRatings, drinkType) values (?,?,?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    tempDrink.setDrinkType(DrinkType.LIQUOR);
                    storeDrinkNoId(tempDrink, stmt, 1);
                    stmt.executeUpdate();

                    //determine auto-generated id
                    generatedKeys = stmt.getGeneratedKeys();
                    if(!generatedKeys.next()){
                        throw new SQLException("Could not get auto-generated key for inserted Drink");

                    }

                    //id is used to link the drink in the main table to an item in the subclass table
                    int drinkId = generatedKeys.getInt(1);
                    tempDrink.setId(drinkId);


                    stmt2 = conn.prepareStatement(
                            "insert into " + DB_LIQUOR_TABLENAME + "(drinkId, content, liquorType) values (?,?,?)"
                    );
                    storeLiquorNoId(liquor, stmt2, 1);
                    stmt2.executeUpdate();
                    return true;
                }finally{
                    DBUtil.closeQuietly(generatedKeys);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }
            }
        });
    }

    @Override
    public boolean addNewMixedDrink(final MixedDrink mixedDrink) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                PreparedStatement stmt3 = null;
                PreparedStatement stmt4 = null;
                ResultSet generatedKeys = null;

                try{
                    //type cast the object so that it can be stored in the main database, with a link to the subclass database
                    Drink tempDrink = (Drink) mixedDrink;
                    stmt = conn.prepareStatement(
                            "insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating, numRatings, drinkType) values (?,?,?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    tempDrink.setDrinkType(DrinkType.MIXEDDRINK);
                    storeDrinkNoId(tempDrink, stmt, 1);
                    stmt.executeUpdate();

                    //determine auto-generated id
                    generatedKeys = stmt.getGeneratedKeys();
                    if(!generatedKeys.next()){
                        throw new SQLException("Could not get auto-generated key for inserted Drink");

                    }

                    //id is used to link the drink in the main table to an item in the subclass table
                    int drinkId = generatedKeys.getInt(1);
                    tempDrink.setId(drinkId);


                    //extract the ingredients list and their corresponding amounts to be stored in the database
                    ArrayList<Ingredient> ingrList = new ArrayList<Ingredient>();
                    ingrList.addAll(mixedDrink.getIngredients());
                    //ArrayList<Double> ingrAmountList = new ArrayList<Double>();
                    //ingrAmountList.addAll(mixedDrink.getIngrAmount());

                    //addbatch
                    //executebatch

                    stmt2 = conn.prepareStatement(
                            "insert into " + DB_MIXED_DRINK_TABLENAME + " (drinkId, mainIng) values (?,?)"
                    );

                    storeMixedDrinkNoId(mixedDrink, stmt2, 1);
                    stmt2.executeUpdate();


                    for(Ingredient item : ingrList){
                        //prepare statement for each ingredient
                        stmt3 = conn.prepareStatement(
                                "insert into " + DB_INGREDIENTS_TABLENAME + "(drinkId, name, amt) values (?,?,?)",
                                PreparedStatement.RETURN_GENERATED_KEYS
                        );

                        //set up the statement for each ingredient and add for a batch insert
                        storeMixedDrinkIngredientNoId(item, stmt, 1);
                        stmt3.addBatch();

                    }
                    stmt3.executeBatch();


                    return true;
                }finally{
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }


            }
        });
    }


    @Override
    public boolean addNewUser(final User user, String hashedPassword) throws SQLException {
        return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet generatedKeys = null;

                try{
                    stmt = conn.prepareStatement(
                            "insert into " + DB_USER_TABLENAME + " (userName, password) values (?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    storeUserNoId(user, stmt, 1);

                    //attempt to insert the user
                    stmt.executeUpdate();

                    //determine auto-generated id
                    generatedKeys = stmt.getGeneratedKeys();
                    if(!generatedKeys.next()){
                        throw new SQLException("Could not get auto-generated key for inserted User");

                    }
                    int userId = generatedKeys.getInt(1);

                    user.setId(userId);

                    return true;
                }finally{
                    DBUtil.closeQuietly(generatedKeys);
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    @Override
    public void deleteDrink(final Drink drink) throws SQLException {
        executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = conn.prepareStatement("delete from " + DB_MAIN_DRINK_TABLENAME + " where drinkName = ?");
                stmt.setString(1, drink.getDrinkName());


                if(drink instanceof Beer){
                    PreparedStatement stmt2 = conn.prepareStatement("delete from " + DB_BEER_TABLENAME + "where drinkId = ?");
                    stmt2.setInt(1, drink.getId());
                    stmt2.executeUpdate();
                }

                if(drink instanceof MixedDrink){
                    //TODO: delete Mixed DRINKS
                }

                if(drink instanceof Liquor){
                    PreparedStatement stmt2 = conn.prepareStatement("delete from " + DB_LIQUOR_TABLENAME + " where drinkId = ?");
                    stmt2.setInt(1, drink.getId());
                    stmt2.executeUpdate();
                }
                stmt.executeUpdate();

                return true;
            }
        });
    }

    @Override
    public void deleteUserList() throws SQLException {
        executeTransaction(new Transaction<Boolean>(){
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = conn.prepareStatement("delete from " + DB_USER_TABLENAME);
                stmt.executeUpdate();
                return true;
            }
        });
    }

    @Override
    public void deleteUser(final String userName) throws SQLException {
        executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = conn.prepareStatement("delete from " + DB_USER_TABLENAME + " where userName = ?");
                stmt.setString(1, userName);
                stmt.executeUpdate();
                return true;
            }
        });
    }



    @Override
    public Beer getBeer(final int id) throws SQLException {
        return executeTransaction(new Transaction<Beer>() {
            @Override
            public Beer execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
                ResultSet resultSet2 = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_BEER_TABLENAME + " where drinkId = ?");
                    stmt.setInt(1, id);

                    resultSet = stmt.executeQuery();

                    if(!resultSet.next()){
                        //no such beer
                        return null;
                    }

                    Beer beer = new Beer();
                    loadBeer(beer, resultSet, 1);

                    //get the drink object to retrieve the drink's name and description
                    stmt2 = conn.prepareStatement("select * from " + DB_MAIN_DRINK_TABLENAME + " where id = ?");

                    stmt2.setInt(1, id);
                    resultSet2 = stmt2.executeQuery();

                    if(!resultSet2.next()){
                        //no such drink
                        return null;
                    }

                    Drink drink = new Drink();
                    loadDrink(drink, resultSet2, 1);

                    beer.setDrinkName(drink.getDrinkName());
                    beer.setDescription(drink.getDescription());
                    beer.setNumRatings(drink.getNumRatings());
                    beer.setRating(drink.getRating());
                    beer.setDrinkType(drink.getDrinkType());


                    return beer;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(resultSet2);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }
            }
        });
    }

    @Override
    public List<Beer> getBeerList() throws SQLException{
        return executeTransaction(new Transaction<List<Beer>>() {
            @Override
            public List<Beer> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                //PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
                //ResultSet resultSet2 = null;

                try{

                    stmt = conn.prepareStatement("select d.*, b.* " +
                            " from mainDrinkTable as d, beerTable as b " +
                            "  where d.id = b.drinkId");

                    List<Beer> result = new ArrayList<Beer>();

                    resultSet = stmt.executeQuery();
                    while(resultSet.next()) {
                        Drink drink = new Drink();
                        Beer beer = new Beer();
                        // the third parameter of loadDrink and loadBeer is
                        // the offset in the ResultSet where the field values
                        // will be loaded from (i.e., the index)
                        loadDrink(drink, resultSet, 1);
                        loadBeer(beer, resultSet, Drink.NUM_FIELDS+1);

                        beer.setDrinkName(drink.getDrinkName());
                        beer.setDescription(drink.getDescription());
                        beer.setNumRatings(drink.getNumRatings());
                        beer.setRating(drink.getRating());
                        beer.setDrinkType(drink.getDrinkType());

                        result.add(beer);
                    }


                    return result;

                }finally{
                    DBUtil.closeQuietly(stmt);
                 //   DBUtil.closeQuietly(stmt2);
                    DBUtil.closeQuietly(resultSet);
                 // DBUtil.closeQuietly(resultSet2);
                }
            }
        });
    }

    //this method will end up being unused
    @Override
    public List<Comment> getComments(final int id, final int start, final int end) throws SQLException {
        return executeTransaction(new Transaction<List<Comment>>() {
            @Override
            public List<Comment> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_COMMENT_TABLENAME + " where drinkId = ?");
                    stmt.setInt(1, id);
                    resultSet = stmt.executeQuery();

                    List<Comment> result = new ArrayList<Comment>();
                    int count = start;
                    //ensure that the resultSet only contains the specified ids
                    while(resultSet.next() && count <= end){
                        Comment comment = new Comment();
                        loadComment(comment, resultSet, 1);
                        result.add(comment);
                        count++;
                    }

                    return result;
                }finally{
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(resultSet);

                }
            }
        });
    }

    @Override
    public List<Drink> getDrinkList() throws SQLException {
        return executeTransaction(new Transaction<List<Drink>>() {
            @Override
            public List<Drink> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_MAIN_DRINK_TABLENAME);
                    resultSet = stmt.executeQuery();

                    List<Drink> result = new ArrayList<Drink>();
                    while(resultSet.next()){
                        Drink drink = new Drink();
                        loadDrink(drink, resultSet, 1);
                        result.add(drink);
                    }
                    return result;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    @Override
    public List<Drink> getFavoritesForUser(final int userId) throws SQLException {
        return executeTransaction(new Transaction<List<Drink>>() {
            @Override
            public List<Drink> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
               // PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
               // ResultSet resultSet2 = null;

                try {
                    stmt = conn.prepareStatement("select d.*, f.* " +
                            " from mainDrinkTable as d, favoritesTable as f " +
                            " where d.id = f.drinkId " +
                            " and f.userId = ?");

                    stmt.setInt(1, userId);
                    List<Drink> result = new ArrayList<Drink>();

                    resultSet = stmt.executeQuery();
                    while (resultSet.next()) {
                        Drink drink = new Drink();
                        Favorite fav = new Favorite();

                        loadDrink(drink, resultSet, 1);
                        loadFavorite(fav, resultSet, Drink.NUM_FIELDS + 1);

                        if (drink.getDrinkType() == DrinkType.BEER) {
                          //  stmt2 = conn.prepareStatement(
                            //        "select * from " + DB_BEER_TABLENAME + " where drinkId = ?"
                           // );
                            //stmt2.setInt(1, drink.getId());

                            //resultSet2 = stmt2.executeQuery();

                            Beer beer = getBeer(drink.getId());
                          //  loadBeer(beer, resultSet2, 1);

                            beer.setDrinkName(drink.getDrinkName());
                            beer.setDescription(drink.getDescription());
                            beer.setNumRatings(drink.getNumRatings());
                            beer.setRating(drink.getRating());
                            beer.setDrinkType(drink.getDrinkType());

                            result.add(beer);
                        }

                        if (drink.getDrinkType() == DrinkType.MIXEDDRINK) {
                       //     stmt2 = conn.prepareStatement(
                         //           "select * from " + DB_MIXED_DRINK_TABLENAME + " where drinkId = ?"
                       //     );
                        //    stmt2.setInt(1, drink.getId());

                        //    resultSet2 = stmt2.executeQuery();

                            MixedDrink mixedDrink = getMixedDrink(drink.getId());
                           // loadMixedDrink(mixedDrink, resultSet2, 1);

                            mixedDrink.setIngredients(getIngredientsForMixedDrink(conn, mixedDrink));

                            mixedDrink.setDrinkName(drink.getDrinkName());
                            mixedDrink.setDescription(drink.getDescription());
                            mixedDrink.setNumRatings(drink.getNumRatings());
                            mixedDrink.setRating(drink.getRating());
                            mixedDrink.setDrinkType(drink.getDrinkType());

                            result.add(mixedDrink);

                        }

                        if (drink.getDrinkType() == DrinkType.LIQUOR) {
                         //   stmt2 = conn.prepareStatement(
                        //            "select * from " + DB_LIQUOR_TABLENAME + " where drinkId = ?"
                        //    );
                        //    stmt2.setInt(1, drink.getId());

                        //    resultSet2 = stmt2.executeQuery();

                            Liquor liquor = getLiquor(drink.getId());
                            //loadLiquor(liquor, resultSet2, 1);

                            liquor.setDrinkName(drink.getDrinkName());
                            liquor.setDescription(drink.getDescription());
                            liquor.setNumRatings(drink.getNumRatings());
                            liquor.setRating(drink.getRating());
                            liquor.setDrinkType(drink.getDrinkType());

                            result.add(liquor);

                        }
                    }
                    return result;
                } finally {
                    DBUtil.closeQuietly(stmt);
                    //DBUtil.closeQuietly(stmt2);
                    DBUtil.closeQuietly(resultSet);
                  //  DBUtil.closeQuietly(resultSet2);
                }
            }
        });
    }

    @Override
    public Liquor getLiquor(final int id) throws SQLException {
        return executeTransaction(new Transaction<Liquor>() {
            @Override
            public Liquor execute(Connection conn) throws SQLException{
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
                ResultSet resultSet2 = null;
                try {
                    stmt = conn.prepareStatement("select * from " + DB_LIQUOR_TABLENAME + " where drinkId = ?");
                    stmt.setInt(1, id);

                    resultSet = stmt.executeQuery();

                    if(!resultSet.next()){
                        //no such liquor
                        return null;
                    }
                    Liquor liquor = new Liquor();
                    loadLiquor(liquor, resultSet, 1);

                    //get the drink object to retrieve the drink's name and description
                    stmt2 = conn.prepareStatement("select * from " + DB_MAIN_DRINK_TABLENAME + " where id = ?");
                    stmt2.setInt(1, id);
                    resultSet2 = stmt2.executeQuery();

                    if(!resultSet2.next()){
                        //no such drink
                        return null;
                    }

                    Drink drink = new Drink();
                    loadDrink(drink, resultSet2, 1);

                    liquor.setDrinkName(drink.getDrinkName());
                    liquor.setDescription(drink.getDescription());
                    liquor.setNumRatings(drink.getNumRatings());
                    liquor.setRating(drink.getRating());
                    liquor.setDrinkType(drink.getDrinkType());

                    return liquor;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(resultSet2);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                }
            }
        });
    }

    @Override
    public List<Liquor> getLiquorList() throws SQLException{
        return executeTransaction(new Transaction<List<Liquor>>() {
            @Override
            public List<Liquor> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
                ResultSet resultSet2 = null;

                try{
                    stmt = conn.prepareStatement("select d.*, l.* " +
                            " from mainDrinkTable as d, liquorTable as l " +
                            "  where d.id = l.drinkId");

                    List<Liquor> result = new ArrayList<Liquor>();

                    resultSet = stmt.executeQuery();
                    while(resultSet.next()) {
                        Drink drink = new Drink();
                        Liquor liquor = new Liquor();
                        // the third parameter of loadDrink and loadBeer is
                        // the offset in the ResultSet where the field values
                        // will be loaded from (i.e., the index)
                        loadDrink(drink, resultSet, 1);
                        loadLiquor(liquor, resultSet, Drink.NUM_FIELDS+1);

                        liquor.setDrinkName(drink.getDrinkName());
                        liquor.setDescription(drink.getDescription());
                        liquor.setNumRatings(drink.getNumRatings());
                        liquor.setRating(drink.getRating());
                        liquor.setDrinkType(drink.getDrinkType());

                        result.add(liquor);
                    }

                    return result;
                }finally{
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(resultSet);
                }
            }
        });
    }

    @Override
    public MixedDrink getMixedDrink(final int id) throws SQLException {
        return executeTransaction(new Transaction<MixedDrink>() {
            @Override
            public MixedDrink execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                PreparedStatement stmt3 = null;
                ResultSet resultSet = null;
                ResultSet resultSet2 = null; //for the ingredients list
                ResultSet resultSet3 = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_MIXED_DRINK_TABLENAME + " where drinkId = ?");
                    stmt.setInt(1, id);

                    resultSet = stmt.executeQuery();

                    if(!resultSet.next()){
                        //no such mixed drink
                        return null;
                    }

                    MixedDrink mixedDrink = new MixedDrink();
                    loadMixedDrink(mixedDrink, resultSet, 1);

                    stmt2 = conn.prepareStatement("select * from " + DB_INGREDIENTS_TABLENAME + " where drinkId = ?");
                    stmt2.setInt(1, id);

                    resultSet2 = stmt2.executeQuery();

                    ArrayList<Ingredient> result = new ArrayList<Ingredient>();
                    while(resultSet.next()){
                        Ingredient ingr = new Ingredient();
                        loadMixedDrinkIngredient(ingr, resultSet2, 1);
                        result.add(ingr);
                    }

                    mixedDrink.setIngredients(result);

                    //get the drink object to retrieve the drink's name and description
                    stmt3 = conn.prepareStatement("select * from " + DB_MAIN_DRINK_TABLENAME + " where id = ?");
                    stmt3.setInt(1, id);
                    resultSet3 = stmt3.executeQuery();

                    if(!resultSet3.next()){
                        //no such drink
                        return null;
                    }

                    Drink drink = new Drink();
                    loadDrink(drink, resultSet3, 1);

                    mixedDrink.setDrinkName(drink.getDrinkName());
                    mixedDrink.setDescription(drink.getDescription());
                    mixedDrink.setRating(drink.getRating());
                    mixedDrink.setNumRatings(drink.getNumRatings());
                    mixedDrink.setDrinkType(drink.getDrinkType());
                    return mixedDrink;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(resultSet2);
                    DBUtil.closeQuietly(resultSet3);
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                    DBUtil.closeQuietly(stmt3);
                }
            }
        });
    }

    @Override
    public List<MixedDrink> getMixedDrinkList() throws SQLException{
        return executeTransaction(new Transaction<List<MixedDrink>>() {
            @Override
            public List<MixedDrink> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null; //get the mixedDrink object

                try{
                    stmt = conn.prepareStatement("select d.*, m.* " +
                            " from mainDrinkTable as d, mixedDrinkTable as m " +
                            "  where d.id = m.drinkId");


                    List<MixedDrink> result = new ArrayList<MixedDrink>();

                    resultSet = stmt.executeQuery();
                    while(resultSet.next()) {
                        Drink drink = new Drink();
                        MixedDrink mixedDrink = new MixedDrink();
                        // the third parameter of loadDrink and loadMixedDrink is
                        // the offset in the ResultSet where the field values
                        // will be loaded from (i.e., the index)
                        loadDrink(drink, resultSet, 1);
                        loadMixedDrink(mixedDrink, resultSet, Drink.NUM_FIELDS + 1);

                        mixedDrink.setIngredients(getIngredientsForMixedDrink(conn, mixedDrink)); //method below

                        mixedDrink.setDrinkName(drink.getDrinkName());
                        mixedDrink.setDescription(drink.getDescription());
                        mixedDrink.setRating(drink.getRating());
                        mixedDrink.setNumRatings(drink.getNumRatings());
                        mixedDrink.setDrinkType(drink.getDrinkType());

                        result.add(mixedDrink);
                    }

                    return result;
                }finally{
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(resultSet);

                }
            }
        });
    }

    private ArrayList<Ingredient> getIngredientsForMixedDrink(Connection conn, MixedDrink mixedDrink) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = conn.prepareStatement("select * from ingredientsTable where drinkId = ?");
            stmt.setInt(1, mixedDrink.getId());
            resultSet = stmt.executeQuery();

            ArrayList<Ingredient> result = new ArrayList<Ingredient>();

            while(resultSet.next()){
                Ingredient ingr = new Ingredient();
                loadMixedDrinkIngredient(ingr, resultSet, 1);
                result.add(ingr);

            }

            return result;
        } finally {
            DBUtil.closeQuietly(stmt);
            DBUtil.closeQuietly(resultSet);
        }
    }


    @Override
    public User getUser(final String userName, final String password) throws SQLException {
        return executeTransaction(new Transaction<User>() {
            @Override
            public User execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_USER_TABLENAME + " where userName = ?");
                    stmt.setString(1, userName);
                   // stmt.setString(2, password);

                    resultSet = stmt.executeQuery();

                    if(!resultSet.next()){
                        //no such user
                        return null;
                    }

                    User user = new User(userName, password);
                    loadUser(user, resultSet, 1);
                    return user;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }


    @Override
    public List<User> getUserList() throws SQLException {
        return executeTransaction(new Transaction<List<User>>() {

            @Override
            public List<User> execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_USER_TABLENAME);
                    resultSet = stmt.executeQuery();

                    List<User> result = new ArrayList<User>();
                    while(resultSet.next()){
                        User user = new User();
                        loadUser(user, resultSet, 1);
                        result.add(user);
                    }
                    return result;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    @Override
    public User loginUser(final String userName, final String password) throws SQLException {
        return executeTransaction(new Transaction<User>(){

            @Override
            public User execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                ResultSet resultSet = null;

                try{
                    stmt = conn.prepareStatement("select * from " + DB_USER_TABLENAME + " where userName = ?");
                    stmt.setString(1, userName);

                    resultSet = stmt.executeQuery();

                    if(!resultSet.next()){
                        //no such user
                        return null;
                    }

                    User user = new User(userName, password);
                    loadUser(user, resultSet, 1);
                    return user;
                }finally{
                    DBUtil.closeQuietly(resultSet);
                    DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    @Override
    public void replaceUser(final String oldUserName, final User newUser) throws SQLException {
        executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = conn.prepareStatement("update " + DB_USER_TABLENAME + " set userName = ? where userName = ?");
                stmt.setString(1, newUser.getUserName());
                stmt.setString(2, oldUserName);
                stmt.executeUpdate();
                return true;
            }
        });
    }

    @Override
    public Drink getDrink(final int id) throws SQLException {
        return executeTransaction(new Transaction<Drink>() {
            @Override
            public Drink execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
                ResultSet resultSet = null;
                
                try{
                    stmt = conn.prepareStatement("select * from " + DB_MAIN_DRINK_TABLENAME + " where id = ?");
                    stmt.setInt(1, id);
                    resultSet = stmt.executeQuery();

                    Drink newDrink = new Drink();
                    loadDrink(newDrink, resultSet, 1);


                    return newDrink;
                }finally{
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(resultSet);
                }
            }
        });
    }

    @Override
    public boolean updateRating(final Drink drink, final float rating) throws SQLException{
       return executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
              //  PreparedStatement stmt = null;
                PreparedStatement stmt2 = null;
               // PreparedStatement stmt3 = null;
                ResultSet resultSet = null;

                try{

                    Drink newDrink = getDrink(drink.getId());

                    int numRatings = newDrink.getNumRatings();
                    float newRating = newDrink.getRating();

                    numRatings++;
                    newRating = (newRating + rating) / numRatings;

                    stmt2 = conn.prepareStatement("update " + DB_MAIN_DRINK_TABLENAME +
                                                  "set rating = ?, numRatings = ? where id = ?"
                    );
                    stmt2.setFloat(1, newRating);
                    stmt2.setInt(2, numRatings);
                    stmt2.setInt(3, drink.getId());

                    stmt2.executeUpdate();

                    return true;
                } finally {
                    //DBUtil.closeQuietly(stmt);
                }
            }
        });
    }

    @Override
    public void replaceUserList(List<User> newUserList) {
        //TODO: replace user list
    }


    @Override
    public User findUser(String userName) {
        //TODO: find user
        return null;
    }


    private interface Transaction<ResultType> {
        public ResultType execute(Connection conn) throws SQLException;
    }

    public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) throws SQLException {

    try{
        return doExecuteTransaction(txn);
    } catch(SQLException e){
        throw new PersistenceException("Transaction failed", e);
    }

    }

    private <ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
        Connection conn = connect();

        try{
            int numAttempts = 0;
            boolean success = false;
            ResultType result = null;

            while(!success && numAttempts < MAX_ATTEMPTS){
                try{
                    result = txn.execute(conn);
                    conn.commit();
                    success = true;
                }catch(SQLException e) {
                    if(e.getSQLState() != null && e.getSQLState().equals("41000")){
                        //deadlock: retry unless max retry count has been reached
                        numAttempts++;
                    }else{
                        throw e;
                    }
                }
            }
            if(!success){
                throw new SQLException("Transaction failed (too many retries)");
            }

            return result;
        }finally{
            DBUtil.closeQuietly(conn);
        }
    }

    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby:" + DB_DIRECTORY + ";create=true");

        //set autocommit to false to allow multiple executions of
        //multiple queries/statements as part of the same transaction
        conn.setAutoCommit(false);

        return conn;
    }

    //this is the main part of the database. the "tables" store the information
    //that the client side needs

    //Each subclass database table will contain a drink_id variable, which
    //corresponds to its place in the main drink (superclass) table
    public void createTables() throws SQLException {
        executeTransaction(new Transaction<Boolean>() {
            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null; //user database table
                PreparedStatement stmt2 = null; //main drink database table
                PreparedStatement stmt3 = null; //beer database table
                PreparedStatement stmt4 = null; //mixed drink database table
                PreparedStatement stmt5 = null; //liquor database table
                PreparedStatement stmt6 = null; //ingredient amount database table
                PreparedStatement stmt7 = null; //comment table
                PreparedStatement stmt8 = null; //favorites table

                try{
                    //database table to store all created user objects
                    stmt = conn.prepareStatement(
                            "create table " + DB_USER_TABLENAME + " (" +
                            " id integer primary key not null generated always as identity," +
                            " userName varchar(80) unique," +
                            " password varchar(80)" +
                            ")"
                    );

                    //database table to store all drink objects, subclasses included. Needed
                    //to generate drinkIds and relate all subclass tables to this table
                    stmt2 = conn.prepareStatement(
                            "create table " + DB_MAIN_DRINK_TABLENAME + " (" +
                            " id integer primary key not null generated always as identity," +
                            " drinkName varchar(200) unique," +
                            " description varchar(1500)," +
                            " rating float(1)," +
                            " numRatings integer," +
                            " drinkType integer" +
                            ")"
                     );

                    //database table for the beer objects
                    stmt3 = conn.prepareStatement(
                            "create table " + DB_BEER_TABLENAME + " (" +
                            //" id integer primary key not null generated always as identity," +
                            " drinkId integer," +
                            " cals integer," +
                            " abv double," +
                            " beerType integer" +
                            ")"
                    );

                    //database table for the mixed drink objects
                    stmt4 = conn.prepareStatement(
                            "create table " + DB_MIXED_DRINK_TABLENAME + " (" +
                            " drinkId integer," +
                            " mainIng integer" +
                            ")"
                    );

                    //database table for the liquor objects
                    stmt5 = conn.prepareStatement(
                            "create table " + DB_LIQUOR_TABLENAME + " (" +
                            " drinkId integer," +
                            " content float(1)," +
                            " liquorType integer" +
                            ")"
                    );

                    //table for the ingredients in a mixed drink object. Stored with a
                    //reference to the id of the corresponding drink
                    stmt6 = conn.prepareStatement(
                            "create table " + DB_INGREDIENTS_TABLENAME + " (" +
                            " drinkId integer," +
                            " name varchar(200)," +
                            " amt double" +
                            ")"
                    );

                    //table for the comments stored for a specific drink, with the id of
                    //the user who posted the comment
                    stmt7 = conn.prepareStatement(
                            "create table " + DB_COMMENT_TABLENAME + " (" +
                            " drinkId integer," +
                            " userName varchar(80)," +
                            " comment varchar(800)" +
                            ")"
                    );

                    stmt8 = conn.prepareStatement(
                            "create table " + DB_FAVORITES_TABLENAME + " (" +
                            " userId integer," +
                            " drinkId integer" +
                            ")"
                    );


                    stmt.executeUpdate();
                    stmt2.executeUpdate();
                    stmt3.executeUpdate();
                    stmt4.executeUpdate();
                    stmt5.executeUpdate();
                    stmt6.executeUpdate();
                    stmt7.executeUpdate();
                    stmt8.executeUpdate();

                    return true;
                }finally {
                    DBUtil.closeQuietly(stmt);
                    DBUtil.closeQuietly(stmt2);
                    DBUtil.closeQuietly(stmt3);
                    DBUtil.closeQuietly(stmt4);
                    DBUtil.closeQuietly(stmt5);
                    DBUtil.closeQuietly(stmt6);
                    DBUtil.closeQuietly(stmt7);
                    DBUtil.closeQuietly(stmt8);
                }
            }
        } );

    }

    protected void storeUserNoId(User user, PreparedStatement stmt, int index) throws SQLException {
        //method assumes that the User doesn't have a valid id, so we are not attempting
        //to store the invalid id.
        stmt.setString(index++, user.getUserName());
        stmt.setString(index++, user.getUserPassword());
    }

    protected void storeBeerNoId(Beer beer, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, beer.getId());
        stmt.setInt(index++, beer.getCalories());
        stmt.setDouble(index++, beer.getABV());
        stmt.setInt(index++, beer.getBeerType().ordinal());
    }

    protected void storeCommentNoId(Comment comment, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, comment.getDrinkId());
        stmt.setString(index++, comment.getUsername());
        stmt.setString(index++, comment.getComment());

    }

    protected void storeDrinkNoId(Drink drink, PreparedStatement stmt, int index) throws SQLException {
        stmt.setString(index++, drink.getDrinkName());
        stmt.setString(index++, drink.getDescription());
        stmt.setFloat(index++, drink.getRating());
        stmt.setInt(index++, drink.getNumRatings());
        stmt.setInt(index++, drink.getDrinkType().ordinal());
    }

    protected void storeFavoriteNoId(Favorite fav, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, fav.getUserId());
        stmt.setInt(index++, fav.getDrinkId());
    }


    protected void storeMixedDrinkNoId(MixedDrink mixedDrink, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, mixedDrink.getId());
        stmt.setInt(index++, mixedDrink.getMaxIngedient().ordinal());

    }

    protected void storeMixedDrinkIngredientNoId(Ingredient ingr, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, ingr.getDrinkId());
        stmt.setString(index++, ingr.getIngredientName());
        stmt.setDouble(index++, ingr.getAmount());
    }

    protected void storeLiquorNoId(Liquor liquor, PreparedStatement stmt, int index) throws SQLException {
        stmt.setInt(index++, liquor.getId());
        stmt.setFloat(index++, liquor.getAlcoholContent());
        stmt.setInt(index++, liquor.getLiquorType().ordinal());
    }


    public void loadInitialData() throws SQLException {
        executeTransaction(new Transaction<Boolean>() {

            @Override
            public Boolean execute(Connection conn) throws SQLException {
                PreparedStatement stmt = null;
             //   PreparedStatement stmt2 = null;
                PreparedStatement stmt3 = null;
                PreparedStatement stmt4 = null;

                try {
                    stmt = conn.prepareStatement("insert into " + DB_USER_TABLENAME + " (userName, password) values (?,?)");
                    storeUserNoId(new User("testUser", "password"), stmt, 1);
                    stmt.addBatch();
                    stmt.executeBatch();


                //    stmt2 = conn.prepareStatement("insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating) values (?,?,?)");
                  //  Drink drink = new Drink();
                    //drink.setDrinkName("testDrink");
                    //drink.setRating(5);
                    //storeDrinkNoId(drink, stmt2, 1);
                    //stmt2.addBatch();
                    //stmt2.executeBatch();

           //         stmt2 = conn.prepareStatement("insert into " + DB_MAIN_DRINK_TABLENAME + " (drinkName, description, rating) values (?,?,?)");
                //    Drink drink = new Drink();
                //    drink.setDrinkName("testDrink");
                //    drink.setRating(5);
                //    storeDrinkNoId(drink, stmt2, 1);
                //    stmt2.addBatch();
                //    stmt2.executeBatch();

                    stmt3 = conn.prepareStatement("insert into " + DB_BEER_TABLENAME + "(drinkId, cals, beerType) values (?,?,?)");
                    Beer beer = new Beer();
                    beer.setCalories(400);
                    beer.setBeerType(BeerType.LAGER);
                    stmt3.addBatch();
                    stmt3.executeBatch();

                    stmt4 = conn.prepareStatement("insert into " + DB_LIQUOR_TABLENAME + "(drinkId, content, liquorType) values (?,?,?)");
                    Liquor liquor = new Liquor();
                    liquor.setAlcoholContent(75.50f);
                    liquor.setLiquorType(LiquorType.RUM);
                    stmt4.addBatch();
                    stmt4.executeBatch();

                    return true;
                } finally {
                    DBUtil.closeQuietly(stmt);
                  //  DBUtil.closeQuietly(stmt2);
                    DBUtil.closeQuietly(stmt3);
                    DBUtil.closeQuietly(stmt4);
                }
            }
        });
    }


    protected void loadBeer(Beer beer, ResultSet resultSet, int index) throws SQLException {
        beer.setId(resultSet.getInt(index++));
        beer.setCalories(resultSet.getInt(index++));
        beer.setABV(resultSet.getInt(index++));

        int beerTypeOrdinal = resultSet.getInt(index++);
        BeerType[] beerTypes = BeerType.values();
        beer.setBeerType(beerTypes[beerTypeOrdinal]);
    }

    protected void loadComment(Comment comment, ResultSet resultSet, int index) throws SQLException {
        comment.setDrinkId(resultSet.getInt(index++));
        comment.setUsername(resultSet.getString(index++));
        comment.setComment(resultSet.getString(index++));
    }
    protected void loadDrink(Drink drink, ResultSet resultSet, int index) throws SQLException {
        drink.setId(resultSet.getInt(index++));
        drink.setDrinkName(resultSet.getString(index++));
        drink.setDescription(resultSet.getString(index++));
        drink.setRating(resultSet.getFloat(index++));
        drink.setNumRatings(resultSet.getInt(index++));

        int drinkTypeOrdinal = resultSet.getInt(index++);
        DrinkType[] drinkTypes = DrinkType.values();
        drink.setDrinkType(drinkTypes[drinkTypeOrdinal]);
    }

    protected void loadLiquor(Liquor liquor, ResultSet resultSet, int index) throws SQLException {
        liquor.setId(resultSet.getInt(index++));
        liquor.setAlcoholContent(resultSet.getFloat(index++));
        int liquorTypeOrdinal = resultSet.getInt(index++);
        LiquorType[] liquorTypes = LiquorType.values();
        liquor.setLiquorType(liquorTypes[liquorTypeOrdinal]);

    }

    protected void loadFavorite(Favorite favorite, ResultSet resultSet, int index) throws SQLException {
        favorite.setUserId(resultSet.getInt(index++));
        favorite.setDrinkId(resultSet.getInt(index++));
    }

    protected void loadMixedDrink(MixedDrink mixedDrink, ResultSet resultSet, int index) throws SQLException {
        mixedDrink.setId(resultSet.getInt(index++));
        int liquorTypeOrdinal = resultSet.getInt(index++);
        LiquorType[] liquorTypes = LiquorType.values();
        mixedDrink.setMaxIngredient(liquorTypes[liquorTypeOrdinal]);
    }

    protected void loadMixedDrinkIngredient(Ingredient ingredient, ResultSet resultSet, int index) throws SQLException {
        ingredient.setDrinkId(resultSet.getInt(index++));
        ingredient.setIngredientName(resultSet.getString(index++));
        ingredient.setAmount(resultSet.getDouble(index++));
    }

    protected void loadUser(User user, ResultSet resultSet, int index) throws SQLException {
        user.setId(resultSet.getInt(index++));
        user.setUserName(resultSet.getString(index++));
        user.setUserPassword(resultSet.getString(index++));
    }

    public static void main(String[] args) throws SQLException {
        DerbyDatabase db = new DerbyDatabase();
        System.out.println("Creating tables...");
        db.createTables();
        System.out.println("Loading intial data...");
        db.loadInitialData();
        System.out.println("Done!");

    }
}
