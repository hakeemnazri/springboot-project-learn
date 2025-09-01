package com.spring_boot.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
        @UniqueConstraint( columnNames = "username"),
        @UniqueConstraint( columnNames = "email"),
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
                fetch = FetchType.EAGER
    )
    @JoinTable(name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Cart cart;

    @ToString.Exclude
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    private Set<Product> products;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
