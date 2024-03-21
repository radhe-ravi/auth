package org.project.auth.Service;

import lombok.AllArgsConstructor;
import org.project.auth.Model.AuthenticationResponse;
import org.project.auth.Model.User;
import org.project.auth.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@AllArgsConstructor
public class AuthenticationService {

    private static final String MESSAGE = "successfully Added";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public AuthenticationResponse register(
            @RequestBody User request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(MESSAGE, token);

    }

    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Name Not found"));

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(MESSAGE, token);
    }
}
