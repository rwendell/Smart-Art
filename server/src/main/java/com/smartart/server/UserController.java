package com.smartart.server;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller    // This means that this class is a Controller
@RequestMapping(path = "/user") // This means URL's start with /user (after Application path)

public class UserController {

    @Autowired

    private UserRepository UserRepository;

    @PostMapping(path = "/add") //Map ONLY POST Requests
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
            String salt_string = "????????";//RandomStringUtils.random(8); for some reason this isn't working --> stores salt as ????????
            n.setSalt(salt_string);
            String passSalt = salt_string + password;
            String hashed = DigestUtils.sha256Hex(passSalt);
            n.setHash(hashed);
            UserRepository.save(n);
            return "Saved New User";
        }


        return "Username Taken";



        /*
            HOW TO RETRIEVE A PASSWORD

            Retrieve the user's salt and hash from the database.
            Prepend the salt to the given password and hash it using the same hash function.
            Compare the hash of the given password with the hash from the database.
            If they match, the password is correct.
            Otherwise, the password is incorrect.
         */
    }


    @PostMapping(path = "/login") //Map ONLY POST Requests
    public @ResponseBody
    String loginUser(@RequestParam String username
            , @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        User n = UserRepository.findByUsername(username);

        String entered, hashEntered, correct;

        correct = n.getHash();
        entered = n.getSalt() + password;
        hashEntered = DigestUtils.sha256Hex(entered);

        System.out.println("salt: " + n.getSalt());

        System.out.println("entered hash: " + hashEntered);
        System.out.println("correct hash: " + correct);

        if (correct.equals(hashEntered)) {
            return n.getUserId().toString();
        }


        return "Incorrect Login";
    }

    @PostMapping(path= "/changepass") //Map ONLY POST Requests
    public @ResponseBody String changePass (@RequestParam Long userId
            , @RequestParam String password) {


        User n = UserRepository.findOne(userId);
        String salt_string = "????????";//RandomStringUtils.random(8); for some reason this isn't working --> stores salt as ????????
        n.setSalt(salt_string);
        String passSalt = salt_string + password;
        String hashed = DigestUtils.sha256Hex(passSalt);
        n.setHash(hashed);
        UserRepository.save(n);

        return "password changed!";






    }


}