package org.devberat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotEmpty
    @NotBlank
    @NotNull
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    //UserDetails Methods

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Appending to authorities List
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    @NullMarked
    public String getUsername() {
        // means (username == email)
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Account can not expire. For now :)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //Account con not lock. also for now this too.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Password can not expire.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // If user active then can log in.
        return isActive;
    }
}