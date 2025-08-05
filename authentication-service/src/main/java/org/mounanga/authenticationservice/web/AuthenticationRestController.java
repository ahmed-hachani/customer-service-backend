package org.mounanga.authenticationservice.web;

import org.mounanga.authenticationservice.dto.LoginRequestDTO;
import org.mounanga.authenticationservice.dto.LoginResponseDTO;
import org.mounanga.authenticationservice.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin()
@RestController
@RequestMapping("/authentication")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponseDTO authenticate(@RequestBody LoginRequestDTO requestDTO) {
        return authenticationService.authenticate(requestDTO);
    }
}
