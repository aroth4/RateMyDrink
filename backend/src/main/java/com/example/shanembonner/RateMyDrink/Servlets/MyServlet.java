/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.shanembonner.RateMyDrink.Servlets;

import com.rateMyDrink.modelClasses.Beer;
import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Favorite;
import com.rateMyDrink.modelClasses.Liquor;
import com.rateMyDrink.modelClasses.MixedDrink;
import com.rateMyDrink.modelClasses.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.AddBeer;
import controllers.AddComment;
import controllers.AddDrink;
import controllers.AddFavorite;
import controllers.AddLiquor;
import controllers.AddMixedDrink;
import controllers.AddUser;
import controllers.DeleteDrink;
import controllers.DeleteUser;
import controllers.DeleteUserList;
import controllers.GetBeer;
import controllers.GetBeerList;
import controllers.GetComments;
import controllers.GetDrink;
import controllers.GetDrinkList;
import controllers.GetFavoritesList;
import controllers.GetLiquor;
import controllers.GetLiquorList;
import controllers.GetMixedDrink;
import controllers.GetMixedDrinkList;
import controllers.GetUser;
import controllers.GetUserList;
import controllers.UpdateRating;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        String id_param = req.getParameter("id");
        String pathInfo = req.getPathInfo(); //path
        String startIndex = req.getParameter("startIndex");
        String endIndex = req.getParameter("endIndex");

        System.out.println(req.getQueryString());

        if(action == null){
            System.out.println("action parameter is null");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("text/plain");
            resp.getWriter().println("action parameter is null");
            return;
        }

        if(action.equals("getBeer")){

            if(id_param == null){
                setBadHttpResponse(resp, "id parameter not found.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Beer beer = null;
            GetBeer controller = new GetBeer();

            int id = Integer.parseInt(id_param, 10);

            try{
                beer = controller.getBeer(id);
            }catch (SQLException e){
                e.printStackTrace();
            }

            if(beer == null){
                setBadHttpResponse(resp, "Beer object is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            setOkJsonDrinkHttpResponse(resp, beer.getDrinkName() + "was found.", beer);
            return;
        }

        if(action.equals("getBeerList")){

            GetBeerList getController = new GetBeerList();
            List<Beer> beerList = null;

            try {
                beerList = getController.getBeerList();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //print drinkList to user's terminal
            if(beerList == null){
                setBadHttpResponse(resp, "beerList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String[] beerNameList = new String[beerList.size()]; //return the list of beer names for the scoreboard
                                                                //as an array of strings to be displayed
            int count = 0;
            for(Beer beer: beerList){
                String beerName = beer.getDrinkName();
                beerNameList[count] = beerName;
                count++;
            }

            Drink[] drinkArr = beerList.toArray(new Drink[beerList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting beer list", drinkArr);

        }

        if(action.equals("getComments")){
            System.out.println("action is getComments.");

            if(startIndex == null || endIndex == null || id_param == null){
                setBadHttpResponse(resp, "startIndex or endIndex, or id_param null", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int id = Integer.parseInt(id_param, 10);
            int start = Integer.parseInt(startIndex, 10);
            int end = Integer.parseInt(endIndex, 10);
            List<Comment> commentList = null;
            GetComments controller = new GetComments();

            try{
                commentList = controller.getComments(id, start, end);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(commentList != null){
                Comment[] commentArr = commentList.toArray(new Comment[commentList.size()]);
                setOkJsonCommentsHttpResponse(resp, "getting drink list", commentArr);
                return;
            }else{
                setBadHttpResponse(resp, "unable to get comments", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

        }

        if(action.equals("getDrinkList")){
            GetDrinkList getController = new GetDrinkList();
            List<Drink> drinkList = null;

            try {
                drinkList = getController.getDrinkList();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //print drinkList to user's terminal
            if(drinkList == null){
                setBadHttpResponse(resp, "drinkList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Drink[] drinkArr = drinkList.toArray(new Drink[drinkList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting drink list", drinkArr);
        }

        if(action.equals("getLiquor")){
            if(id_param == null){
                setBadHttpResponse(resp, "id parameter not found.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int id = Integer.parseInt(id_param, 10);
            Liquor liquor = null;
            GetLiquor controller = new GetLiquor();

            try {
                liquor = controller.getLiquor(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(liquor == null){
                setBadHttpResponse(resp, "liquor object not found in database", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            setOkJsonDrinkHttpResponse(resp, liquor.getDrinkName() + " was found.", liquor);
            return;
        }

        if(action.equals("getLiquorList")){

            GetLiquorList getController = new GetLiquorList();
            List<Liquor> liquorList = null;

            try {
                liquorList = getController.getLiquorList();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if(liquorList == null){
                setBadHttpResponse(resp, "mixedDrinkList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Drink[] drinkArr = liquorList.toArray(new Drink[liquorList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting liquor list", drinkArr);

        }

        if(action.equals("getMixedDrink")){
            System.out.println("action is getMixedDrink.");
//            if(pathInfo.startsWith("/")){
//                pathInfo = pathInfo.substring(1);
//            }
            int id = Integer.parseInt(id_param, 10);
            MixedDrink mixedDrink = null;

            GetMixedDrink controller = new GetMixedDrink();

            try{
                mixedDrink = controller.getMixedDrink(id);
            }catch(SQLException e) {
                e.printStackTrace();
            }

            if(mixedDrink == null){
                setBadHttpResponse(resp, "No such mixed drink", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            setOkJsonDrinkHttpResponse(resp, mixedDrink.getDrinkName() + " was found.", mixedDrink);
            return;
        }

        if(action.equals("getDrink")){
            System.out.println("action is getDrink");

            int id = Integer.parseInt(id_param, 10);
            Drink drink = null;

            GetDrink controller = new GetDrink();

            try{
                drink = controller.getDrink(id);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(drink == null){
                setBadHttpResponse(resp, "No such rink", "text/plain", HttpServletResponse.SC_NOT_FOUND);
            }

            setOkJsonDrinkHttpResponse(resp, drink.getDrinkName() + " was found.", drink);
        }

        if(action.equals("getMixedDrinkList")){

            GetMixedDrinkList getController = new GetMixedDrinkList();
            List<MixedDrink> mixedDrinkList = null;

            try {
                mixedDrinkList = getController.getMixedDrinkList();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if(mixedDrinkList == null) {
                setBadHttpResponse(resp, "mixedDrinkList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Drink[] drinkArr = mixedDrinkList.toArray(new Drink[mixedDrinkList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting mixedDrink list", drinkArr);

        }

        if(action.equals("getUser")){
            System.out.println("action is getUser.");
            String username = req.getParameter("username");
            String password = req.getParameter("password");//JSON.getObjectMapper().readValue(req.getReader(), String.class);
            User user = null;
            GetUser controller = new GetUser();
            try {
                user = controller.getUser(username,password);
                System.out.println("accessed database");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(user == null){
                setBadHttpResponse(resp, "User was not found", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            setOkJsonUserHttpResponse(resp, user.getUserName() + "was found.", user);
            return;
        }

        if(action.equals("getUserList")){
            //retrieve inventory from database
            GetUserList getController = new GetUserList();
            List<User> userList = null;
            try {
                userList = getController.getUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //print userList to user's terminal
            if(userList == null){
                setBadHttpResponse(resp, "userList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String[] userNameList = new String[userList.size()]; //return the list of usernames for the scoreboard
            //as an array of strings to be displayed
            int count = 0;
            for(User user: userList){
                String userName = user.getUserName();
                userNameList[count] = userName;
                count++;
            }

            setOkJsonUserHttpResponse(resp, "getting user list.", userNameList);
            return;
        }

        if(action.equals("getFavoritesList")){
            if(id_param == null){
                setBadHttpResponse(resp, "id parameter not found.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            GetFavoritesList controller = new GetFavoritesList();
            List<Drink> drinkList = null;
            int id = Integer.parseInt(id_param);
            try{
                drinkList = controller.getFavoritesList(id);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(drinkList == null){
                setBadHttpResponse(resp, "favorites list is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
            }

            Drink[] drinkArr = drinkList.toArray(new Drink[drinkList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting favorites list", drinkArr);
        }

        if(action.equals("getDrinkList")){
            GetDrinkList getController = new GetDrinkList();
            List<Drink> drinkList = null;

            try {
                drinkList = getController.getDrinkList();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //print drinkList to user's terminal
            if(drinkList == null){
                setBadHttpResponse(resp, "drinkList is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Drink[] drinkArr = drinkList.toArray(new Drink[drinkList.size()]);
            setOkJsonDrinkHttpResponse(resp, "getting drink list", drinkArr);
        }

    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        throw new UnsupportedOperationException("PUT Method is not supported.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); //path
        String action = req.getParameter("action");

        if(action == null){
            setBadHttpResponse(resp, "action is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /**
         * to add a new beer object to the database
         */
        if(action.equals("addBeer")){
            System.out.println("action is addBeer");
            Beer newBeer = JSON.getObjectMapper().readValue(req.getReader(), Beer.class);
            AddBeer controller = new AddBeer();
            boolean success = false;

            try{
                success = controller.addBeer(newBeer);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if (success) {
                setOkJsonDrinkHttpResponse(resp, newBeer.getDrinkName() + " beer successfully added to database.", newBeer);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add beer to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        /**
         * to add a new comment object to the database
         */
        if(action.equals("addComment")){
            System.out.println("action is addComment");

            Comment newComment = JSON.getObjectMapper().readValue(req.getReader(), Comment.class);
            AddComment controller = new AddComment();
            boolean success = false;

            try{
                success = controller.addComment(newComment);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(success){
                setOkJsonCommentHttpResponse(resp, "new comment successfully added to database", newComment);
            }else{
                setBadHttpResponse(resp, "failed to add comment to database", "text/plain", HttpServletResponse.SC_NOT_FOUND);
            }
        }
        /**
         * to add a new liquor object to the database
         */
        if(action.equals("addLiquor")) {
            System.out.println("action is addLiquor.");

            Liquor newLiquor = JSON.getObjectMapper().readValue(req.getReader(), Liquor.class);

            if(newLiquor == null){
                setBadHttpResponse(resp, "new Liquor object is null.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            AddLiquor controller = new AddLiquor();
            boolean success = false;

            try {
                System.out.println("attempting to add liquor to database.");
                success = controller.addLiquor(newLiquor);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if (success) {
                setOkJsonDrinkHttpResponse(resp, newLiquor.getDrinkName() + " successfully added to database.", newLiquor);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add liquor to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        /**
         * to add a drink object to the database
         */
        if(action.equals("addDrink")){

            System.out.println("action: addDrink");
            System.out.println("pathinfo: " + pathInfo);
            Drink newDrink = JSON.getObjectMapper().readValue(req.getReader(), Drink.class);

            AddDrink controller = new AddDrink();
            boolean success = false;

            try{
                success = controller.addNewDrink(newDrink);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if (success) {
                setOkJsonDrinkHttpResponse(resp, newDrink.getDrinkName() + " successfully added to database.", newDrink);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add drink to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        /**
         * to add a favorite object to the database
         */
        if(action.equals("addFavorite")){
            System.out.println("action: addFavorite");

            Favorite newFavorite = JSON.getObjectMapper().readValue(req.getReader(), Favorite.class);

            AddFavorite controller = new AddFavorite();
            boolean success = false;

            try{
                success = controller.addFavorite(newFavorite);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(success){
                setOkFavoriteHttpResponse(resp, "favorite successfully added to database.", newFavorite);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add favorite to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        /**
         * to add a mixedDrink object to the database
         */
        if(action.equals("addMixedDrink")){
            System.out.println("action is addMixedDrink");
            MixedDrink mixedDrink = JSON.getObjectMapper().readValue(req.getReader(), MixedDrink.class);

            AddMixedDrink controller = new AddMixedDrink();
            boolean success = false;

            try {
                System.out.println("trying to add mixed drink to database.");
                success = controller.addMixedDrink(mixedDrink);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if (success) {
                setOkJsonDrinkHttpResponse(resp, mixedDrink.getDrinkName() + " successfully added to database.", mixedDrink);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add mixed drink to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        //case to handle adding a new user to the database
        if(action.equals("addUser")) {
            System.out.println("action is 'addUser'");

            User newUser = JSON.getObjectMapper().readValue(req.getReader(), User.class);
            String password = newUser.getUserPassword();

            AddUser addController = new AddUser();
            boolean success = false;

            try {
                System.out.println("attempting to add user to database");
                success = addController.addNewUser(newUser, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (success) {
                setOkJsonUserHttpResponse(resp, newUser.getUserName() + " successfully added to database.", newUser);
                return;
            }else{
                setBadHttpResponse(resp, "failed to add new user to database.", "text/plain", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

        }

        if(action.equals("loginUser")){
            return;
        }

        if(action.equals("updateRating")){
            System.out.println("action is 'updateRating'");

            Drink drink = JSON.getObjectMapper().readValue(req.getReader(), Drink.class);

            String ratingString = req.getParameter("rating");

            float rating = Float.parseFloat(ratingString);
            boolean success = false;

            UpdateRating controller = new UpdateRating();

            System.out.println("attempting to update rating for drink");

            try{
                success = controller.updateRating(drink, rating);
            }catch(SQLException e){
                e.printStackTrace();
            }
            if(success){
                setOkJsonDrinkHttpResponse(resp, "success updating drink rating", drink);
                return;
            }else{
                setBadHttpResponse(resp, "failed to update drink rating", "text/plain", HttpServletResponse.SC_NOT_FOUND);
            }

        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");

        //if there is an item name, delete it. if not, delete whole database
        if(action.equals("deleteDrink")){
            if(pathInfo.startsWith("/")){
                pathInfo = pathInfo.substring(1);
            }

            Drink drink = JSON.getObjectMapper().readValue(req.getReader(), Drink.class);
            DeleteDrink controller = new DeleteDrink();

            try {
                controller.deleteDrink(drink);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            JSON.getObjectMapper().writeValue(resp.getWriter(), drink);

        }
        if(action.equals("deleteUser")){
            //get the item name
            if(pathInfo.startsWith("/")){
                pathInfo = pathInfo.substring(1);
            }
            //delete the item from inventory
            DeleteUser controller = new DeleteUser();
            try {
                controller.deleteUser(pathInfo);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //a controller to get the inventory after an item is deleted
            GetUserList responseController = new GetUserList();

            //retrieve inventory and show the update
            List<User> userList = null;
            try {
                userList = responseController.getUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            JSON.getObjectMapper().writeValue(resp.getWriter(), userList);
        }

        if(action.equals("deleteUserList")){
            //delete the entire list
            DeleteUserList controller = new DeleteUserList();
            try {
                controller.deleteUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resp.getWriter().println("User list deleted");
        }
    }

    /**
     * sets a Servlet response with a 404 NOT FOUND status code with a message about the error occuring.
     *
     * @param resp the HttpServletResponse
     * @param msg the message to be sent to the user
     * @param contentType the content-type of the message
     * @param status the HTTP Status code
     * @throws IOException
     */
    private void setBadHttpResponse(HttpServletResponse resp, String msg, String contentType, int status) throws IOException{
        System.out.println(msg);
        resp.setStatus(status);
        resp.setContentType(contentType);
        resp.getWriter().println(msg);
    }

    /**
     * sets the Servlet response with a 200 OK status code and writes a Drink object to the body of the response.
     *
     * @param resp the servlet response.
     * @param msg a message to be printed in the console
     * @param drink the drink object to be written to the body of the response.
     * @throws IOException
     */
    private void setOkJsonDrinkHttpResponse(HttpServletResponse resp, String msg, Drink drink) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), drink);
    }

    private void setOkFavoriteHttpResponse(HttpServletResponse resp, String msg, Favorite fav) throws IOException {
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), fav);
    }

    /**
     *
     * @param resp the servlet response
     * @param msg a message to print to the console
     * @param comment the comment object to be written to the body of the response
     * @throws IOException
     */
    private void setOkJsonCommentHttpResponse(HttpServletResponse resp, String msg, Comment comment) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), comment);
    }

    /**
     * sets the Servlet response with a 200 OK status code and writes an array of Comment objects to the body of the response
     * @param resp the servlet response
     * @param msg a message to print to the console
     * @param comments the array of comment objects to be written to the body of the response
     */
    private void setOkJsonCommentsHttpResponse(HttpServletResponse resp, String msg, Comment[] comments) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), comments);
    }

    /**
     * writes the Servlet response with a 200 OK status code and writes an array of drink objects to the body of the response.
     *
     * @param resp the Servlet response
     * @param msg a message to be printed in the console.
     * @param drinks the array of drinks to be sent in the response.
     * @throws IOException
     */
    private void setOkJsonDrinkHttpResponse(HttpServletResponse resp, String msg, Drink[] drinks) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), drinks);
    }

    /**
     * sets the Servlet's response with a 200 OK status and writes the user object to the body of the response.
     *
     * @param resp the servlet response
     * @param msg a message to print to the console
     * @param user user object to be written to the response's body
     * @throws IOException
     */
    private void setOkJsonUserHttpResponse(HttpServletResponse resp, String msg, User user) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), user);
    }

    /**
     * set the Servlet's response with a 200 OK status and writes the list of usernames to the response body.
     *
     * @param resp the servlet response
     * @param msg a message that can be printed to the console.
     * @param users the list of usernames
     * @throws IOException
     */
    private void setOkJsonUserHttpResponse(HttpServletResponse resp, String msg, String[] users) throws IOException{
        System.out.println(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        JSON.getObjectMapper().writeValue(resp.getWriter(), users);
    }

}
