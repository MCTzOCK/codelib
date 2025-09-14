package com.bensiebert.codelib.auth.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    @With private String email;

    @Schema(hidden = true)
    @Column(nullable = false)
    @With private String passwordHash;

    @Column(nullable = false)
    @With private String name;

    @Column(nullable = false)
    @With private String role;

}
