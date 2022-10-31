package org.dflynt.primmy.authservice.controllers;

import org.dflynt.primmy.authservice.exceptions.IncorrectCredentialsException;
import org.dflynt.primmy.authservice.exceptions.InvalidRefreshTokenException;
import org.dflynt.primmy.authservice.exceptions.UserNotFoundException;
import org.dflynt.primmy.authservice.models.User;
import org.dflynt.primmy.authservice.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class AuthenticationController {

    Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    AuthenticationService authService;

    public AuthenticationController() { }

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<User> attemptLogin(@RequestBody Map<String, String> credentials) {
        //TODO implement logging for IP address
        try {
            return new ResponseEntity(authService.attemptLogin(credentials), HttpStatus.OK);
        } catch(UserNotFoundException unf) {
            return new ResponseEntity(unf.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IncorrectCredentialsException ice) {
            return new ResponseEntity(ice.getMessage(), HttpStatus.FORBIDDEN);
        }
        //TODO add jwt to redis cache for quick access when refreshing

    }

    @PostMapping(value = "/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            return new ResponseEntity(authService.refreshToken(authHeader), HttpStatus.OK);
        } catch (InvalidRefreshTokenException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
}
