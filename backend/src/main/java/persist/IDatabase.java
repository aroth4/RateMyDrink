package persist;

import com.rateMyDrink.modelClasses.Beer;
import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Favorite;
import com.rateMyDrink.modelClasses.Liquor;
import com.rateMyDrink.modelClasses.MixedDrink;
import com.rateMyDrink.modelClasses.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by shanembonner on 1/20/15.
 */
public interface IDatabase {

    boolean addNewBeer(Beer beer) throws SQLException;

    boolean addNewComment(Comment comment) throws SQLException;

    boolean addNewDrink(Drink drink) throws SQLException;

    boolean addNewFavorite(Favorite fav) throws SQLException;

    boolean addNewLiquor(Liquor liquor) throws SQLException;

    boolean addNewMixedDrink(MixedDrink mixedDrink) throws SQLException;

    public boolean addNewUser(User user, String hashedPassword) throws SQLException;

    void deleteDrink(Drink drink) throws SQLException;

    void deleteUserList() throws SQLException;

    void deleteUser(String userName) throws SQLException;

    public User findUser(String userName);

    Beer getBeer(int id) throws SQLException;

    List<Beer> getBeerList() throws SQLException;

    //TODO:edit to take start index and end index
    List<Comment> getComments(int id, int start, int end) throws SQLException;

    List<Drink> getDrinkList() throws SQLException;

    List<Drink> getFavoritesForUser(int userId) throws SQLException;

    Liquor getLiquor(int id) throws SQLException;

    List<Liquor> getLiquorList() throws SQLException;

    MixedDrink getMixedDrink(int id) throws SQLException;

    List<MixedDrink> getMixedDrinkList() throws SQLException;

    User getUser(String userName, String password) throws SQLException;

    List<User> getUserList() throws SQLException;

    Drink getDrink(int id) throws SQLException;

    boolean updateRating(Drink drink, float rating) throws SQLException;

    void replaceUserList(List<User> newUserList);

    void replaceUser(String oldUserName, User newUser) throws SQLException;

    public User loginUser(String userName, String password) throws SQLException;


}
