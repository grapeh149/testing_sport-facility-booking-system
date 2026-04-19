package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.request.UserLoginRequest;
import group6.it.ou.sportfacilitybooking.request.UserRegisterRequest;
import group6.it.ou.sportfacilitybooking.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/username/{username}")
    public UserDTO getByUsername(@PathVariable String username) {
        return service.getByUsername(username);
    }

    @GetMapping("/email/{email}")
    public UserDTO getByEmail(@PathVariable String email) {
        return service.getByEmail(email);
    }

    @GetMapping("/role/{role}")
    public List<UserDTO> getByRole(@PathVariable String role) {
        return service.getByRole(role);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/register")
    public UserDTO register(@RequestBody UserRegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody UserLoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/check-username/{username}")
    public boolean checkUsernameExists(@PathVariable String username) {
        return service.existsByUsername(username);
    }

    @GetMapping("/check-email/{email}")
    public boolean checkEmailExists(@PathVariable String email) {
        return service.existsByEmail(email);
    }
}
