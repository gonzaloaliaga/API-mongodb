package com.example.miapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
@Schema(description = "Representa la información de un usuario registrado en el sistema. Utilizado para registro e inicio de sesión.")
public class Usuario {

    @Id
    @Schema(description = "Identificador único de MongoDB.", example = "60a8b9f1d43a6d40003b5f92")
    private String id;

    @Schema(description = "Nombre completo del usuario.", example = "Clark Kent")
    private String nombre;

    @Schema(description = "Correo electrónico del usuario.", example = "clark.kent@duoc.cl")
    private String correo;

    @Schema(description = "Contraseña de la cuenta.", example = "ClaveSegura123")
    private String pass;

    @Schema(description = "Número de teléfono.", example = "987654321")
    private String telefono;

    @Schema(description = "Región de residencia del usuario.", example = "Metropolitana de Santiago")
    private String region;

    @Schema(description = "Comuna de residencia del usuario.", example = "Providencia")
    private String comuna;

    // Usado en proyecto Kotlin
    private String rol = "cliente";

}