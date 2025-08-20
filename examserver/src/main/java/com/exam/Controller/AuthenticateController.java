package com.exam.Controller;

import com.exam.Config.JwtUtil;
import com.exam.Entities.JwtRequest;
import com.exam.Entities.JwtResponse;
import com.exam.Entities.User;
import com.exam.Services.Implement.UserDetailsServiceImpl;
import com.exam.helper.UserFoundException;
import com.exam.helper.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
 public class AuthenticateController{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    //generate token
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest  jwtRequest) throws Exception {
        try
        {

            authenticate(jwtRequest.getUsername(),jwtRequest.getPassword());


        }
        catch (UserNotFoundException e)
        {
            e.printStackTrace();
            throw new Exception("User Not found");
        }

        UserDetails userDetails=this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token=this.jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));

    }

    private  void  authenticate(String username,String password) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));


        }
        catch (DisabledException e)
        {
            throw  new Exception("User disabled" + e.getMessage());
        }
        catch (BadCredentialsException e)
        {
            throw  new Exception("Invalid Credentials"+e.getMessage());
        }
    }
    @GetMapping("/current-user")
    public User getCurrentUser(Principal principal)
    {
        return ((User)this.userDetailsService.loadUserByUsername(principal.getName()));
    }
    @ExceptionHandler(UserFoundException.class)
    public ResponseEntity<?> exceptionHandler(UserFoundException ex) {
        return ResponseEntity.ok(ex.getMessage());
    }

 }