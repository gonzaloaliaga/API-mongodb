package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Usuarios", description = "Operaciones de gestion, registro e inicio de sesion de usuarios.")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // OBTENER TODOS LOS USUARIOS
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve la lista completa de todos los usuarios registrados en el sistema.")
    @GetMapping
    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    // OBTENER USUARIO POR ID
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico usando su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public Usuario getUserById(
        @Parameter(description = "ID del usuario a buscar")
        @PathVariable String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // INICIO DE SESIÓN
    @Operation(summary = "Iniciar Sesión", description = "Verifica credenciales de correo y contraseña. Retorna el objeto Usuario si son correctas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas")
    })
    @PostMapping("/login")
    public Usuario loginUser(
            @RequestBody Usuario loginRequest) {
        
        return usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .filter(u -> u.getPass().equals(loginRequest.getPass()))
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));
    }


    // REGISTRAR NUEVO USUARIO
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado y devuelto"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PostMapping
    public Usuario createUser(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // ACTUALIZAR USUARIO EXISTENTE
    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public Usuario updateUser(
        @Parameter(description = "ID del usuario a actualizar")
        @PathVariable String id, 
        @RequestBody Usuario userDetails) {

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

    // ELIMINAR USUARIO
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario de la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usuarioRepository.deleteById(id);
    }
}

