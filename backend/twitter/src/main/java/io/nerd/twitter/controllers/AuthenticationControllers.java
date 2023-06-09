/**
 * @author Hassan Refaat <hassan.refaat.dev@gmail.com>
 * @Created 4/30/2023 5:07 AM
 */
package io.nerd.twitter.controllers;

import io.nerd.twitter.exception.EmailAlreadyTakenException;
import io.nerd.twitter.exception.EmailFailedToSendException;
import io.nerd.twitter.exception.IncorrectVerificationCodeException;
import io.nerd.twitter.exception.UserDoesNotExistException;
import io.nerd.twitter.models.ApplicationUser;
import io.nerd.twitter.models.RegistrationObject;
import io.nerd.twitter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationControllers {
    private final UserService userService;

    @ExceptionHandler({EmailAlreadyTakenException.class})
    public ResponseEntity<String> handleEmailAlreadyTakenException() {
        return new ResponseEntity<>("The Email provided already taken", HttpStatus.CONFLICT);
    }

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationObject ro) {
        return userService.registerUser(ro);
    }

    @ExceptionHandler({UserDoesNotExistException.class})
    public ResponseEntity<String> handleUserDoesNotExistException() {
        return new ResponseEntity<>("The User does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/phone")
    public ApplicationUser updatePhoneNumber(@RequestBody LinkedHashMap<String, String> body) {
        var username = body.get("username");
        var phone = body.get("phone");

        var user = userService.getUserByUsername(username);

        user.setPhone(phone);
        return userService.updateUser(user);
    }

    @ExceptionHandler({EmailFailedToSendException.class})
    public ResponseEntity<String> handleEmailFailedToSendException() {
        return new ResponseEntity<>("The Email failed to send", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/email/code")
    public ResponseEntity<String> createVerificationCode(@RequestBody LinkedHashMap<String, String> body) {
        userService.generateEmailVerification(body.get("username"));
        return new ResponseEntity<>("Verification code sent to your email", HttpStatus.OK);
    }

    @ExceptionHandler({IncorrectVerificationCodeException.class})
    public ResponseEntity<String> handleIncorrectVerificationCodeException() {
        return new ResponseEntity<>("Incorrect verification code", HttpStatus.CONFLICT);
    }

    @PostMapping("/email/verify")
    public ApplicationUser verifyEmail(@RequestBody LinkedHashMap<String, String> body) {
        var username = body.get("username");
        var code = Long.parseLong(body.get("code"));

        return userService.verifyEmail(username, code);
    }
    @PutMapping("/update/password")
    public ApplicationUser updatePassword(@RequestBody LinkedHashMap<String,String> body){
        var username = body.get("username");
        var password = body.get("password");
        return userService.setPassword(username,password);
    }
}
