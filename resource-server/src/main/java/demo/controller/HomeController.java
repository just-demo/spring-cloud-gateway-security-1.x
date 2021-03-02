package demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping
    public String home() {
        return "Allowed!";
    }

    @GetMapping("/principal")
    public Principal principal(Principal principal) {
        return principal;
    }

    @PreAuthorize("#oauth2.hasScope('demoScope') and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "Allowed!";
    }
}