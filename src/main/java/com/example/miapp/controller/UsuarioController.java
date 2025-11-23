package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.miapp.model.Usuario;
import com.example.miapp.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public Usuario getUserById(@PathVariable String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Login con correo y contraseña
    @GetMapping("/login")
    public Usuario loginUser(
            @RequestParam String correo,
            @RequestParam String password) {

        return usuarioRepository.findByCorreo(correo)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // Crear un nuevo usuario
    @PostMapping
    public Usuario createUser(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario existente
    @PutMapping("/{id}")
    public Usuario updateUser(@PathVariable String id, @RequestBody Usuario userDetails) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getNombre() != null)
            usuario.setNombre(userDetails.getNombre());

        if (userDetails.getCorreo() != null)
            usuario.setCorreo(userDetails.getCorreo());

        if (userDetails.getPass() != null)
            usuario.setPass(userDetails.getPass());

        if (userDetails.getTelefono() != null)
            usuario.setTelefono(userDetails.getTelefono());

        if (userDetails.getRegion() != null)
            usuario.setRegion(userDetails.getRegion());

        if (userDetails.getComuna() != null)
            usuario.setComuna(userDetails.getComuna());

        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usuarioRepository.deleteById(id);
    }
}

