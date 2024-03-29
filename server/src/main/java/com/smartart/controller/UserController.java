package com.smartart.controller;


import com.smartart.model.Artboard;
import com.smartart.model.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rwendell
 *
 * This Class controls the Users
 */
@Controller    // This means that this class is a Controller
@RequestMapping(path = "/user") // This means URL's start with /user (after Application path)

public class UserController {

    @Autowired
    private com.smartart.model.UserRepository UserRepository;

    @Autowired
    private com.smartart.model.ArtboardRepository ArtboardRepository;

    /**
     * Tries to add a new user
     * @param username the username
     * @param password the password
     * @return a JSON with the required information for the client to determine if a user could be added
     */
    @PostMapping(path = "/add", produces = "application/json") //Map ONLY POST Requests
    public @ResponseBody
    String addNewUser(@RequestParam String username
            , @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        try {
            //noinspection ResultOfMethodCallIgnored    This makes sure the warning is suppresed
            UserRepository.findByUsername(username).getUsername();
        } catch (NullPointerException ex) {
            User n = new User();
            n.setUsername(username);  //sets Username
            n.setAdmin(0);      //default to 0 -> false
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
            String salt_string = RandomStringUtils.random(8, characters);
            n.setSalt(salt_string);
            String passSalt = salt_string + password;
            String hashed = DigestUtils.sha256Hex(passSalt);
            n.setHash(hashed);
            UserRepository.save(n);


            JSONObject userInfo = new JSONObject();
            userInfo.put("userId", n.getUserId());
            userInfo.put("username", n.getUsername());
            userInfo.put("response", "user successfully added");

            return userInfo.toString();
        }

        JSONObject fail = new JSONObject();
        fail.put("failure", "Username Taken");

        return fail.toString();



        /*
            HOW TO RETRIEVE A PASSWORD

            Retrieve the user's salt and hash from the database.
            Prepend the salt to the given password and hash it using the same hash function.
            Compare the hash of the given password with the hash from the database.
            If they match, the password is correct.
            Otherwise, the password is incorrect.
         */
    }


    /**
     * Simple login checker
     *
     * @param username the username
     * @param password the password
     * @return A success or a fail with different values for each
     */
    @PostMapping(path = "/login", produces = "application/json") //Map ONLY POST Requests
    public @ResponseBody
    String loginUser(@RequestParam String username
            , @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        JSONObject fail = new JSONObject();
        fail.put("response", "Incorrect Login");

        try {
            //noinspection ResultOfMethodCallIgnored    This makes sure the warning is suppresed
            UserRepository.findByUsername(username).getUsername();
        } catch (NullPointerException ex){


            return fail.toString();

        }



        User n = UserRepository.findByUsername(username);

        String entered, hashEntered, correct;

        correct = n.getHash();
        entered = n.getSalt() + password;
        hashEntered = DigestUtils.sha256Hex(entered);

        List<Long> boardIDs = new CopyOnWriteArrayList<>();

        for (Artboard a : ArtboardRepository.findAll()) {

            if (a.getUserId() == n.getUserId()) {
                boardIDs.add(a.getArtboardId());
            }

        }

        if (correct.equals(hashEntered)) {
            JSONObject success = new JSONObject();
            success.put("isAdmin", n.getAdmin());
            success.put("boardIDs", boardIDs);
            success.put("userId", n.getUserId());
            success.put("username", n.getUsername());
            success.put("response", "login success");
            return success.toString();
        }


        return fail.toString();
    }


    @GetMapping(path = "/getboards", produces = "application/json")
    public @ResponseBody
    String getBoards(@RequestParam Long userID){

        JSONObject response = new JSONObject();
        User n = UserRepository.findByUserId(userID);
        List<Long> boardIDs = new CopyOnWriteArrayList<>();

        for (Artboard a : ArtboardRepository.findAll()) {

            if (a.getUserId() == n.getUserId()) {
                boardIDs.add(a.getArtboardId());
            }

        }

        response.put("boards", boardIDs);
        response.put("response", "Success");

        return response.toString();


    }

    /**
     * Allows user to change password
     * @param userId the user's unique ID generated from the mySQL database
     * @param password the user's new password
     * @return returns a success on password change
     */
    @PostMapping(path = "/changepass", produces = "application/json") //Map ONLY POST Requests
    public @ResponseBody
    String changePass(@RequestParam Long userId
            , @RequestParam String password) {


        User n = UserRepository.findOne(userId);

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        String salt_string = RandomStringUtils.random(8, characters);
        n.setSalt(salt_string);
        String passSalt = salt_string + password;
        String hashed = DigestUtils.sha256Hex(passSalt);
        n.setHash(hashed);
        UserRepository.save(n);

        JSONObject success = new JSONObject();
        success.put("response", "successfully changed password");

        return success.toString();


    }


}
