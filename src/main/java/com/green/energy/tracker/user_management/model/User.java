package com.green.energy.tracker.user_management.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "realm_id", nullable = false)
    private String realmId;
    @Column(name = "username", nullable = false)
    private String username;
}
