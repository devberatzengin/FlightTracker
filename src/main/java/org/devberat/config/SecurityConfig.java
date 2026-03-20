package org.devberat.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import lombok.RequiredArgsConstructor;
import org.devberat.repository.IUserRepository;
import org.devberat.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers("/rest/api/auth/**").permitAll()

                        // Flight
                        .requestMatchers(HttpMethod.POST, "/rest/api/flight/create").hasAnyAuthority("ADMIN", "TOWER")
                        .requestMatchers(HttpMethod.PUT, "/rest/api/flight/update-status/**").hasAnyAuthority("ADMIN", "TOWER")
                        .requestMatchers(HttpMethod.PUT, "/rest/api/flight/*/assign-captain/*").hasAnyAuthority("ADMIN", "TOWER")
                        .requestMatchers(HttpMethod.DELETE, "/rest/api/flight/delete/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/rest/api/flight/list", "/rest/api/flight/*").authenticated() // Visible for everyone

                        // Airport
                        .requestMatchers(HttpMethod.GET, "/rest/api/airport/list", "/rest/api/airport/*").authenticated()
                        .requestMatchers("/rest/api/airport/create", "/rest/api/airport/delete/**").hasAuthority("ADMIN")

                        // Aircraft
                        .requestMatchers(HttpMethod.GET, "/rest/api/aircraft/list", "/rest/api/aircraft/*").hasAnyAuthority("ADMIN", "TOWER", "CAPTAIN")
                        .requestMatchers("/rest/api/aircraft/create", "/rest/api/aircraft/delete/**").hasAuthority("ADMIN")

                        // Ticket
                        .requestMatchers("/rest/api/ticket/book").hasAnyAuthority("PASSENGER", "ADMIN") 
                        .requestMatchers("/rest/api/ticket/my-tickets").hasAnyAuthority("PASSENGER", "ADMIN") 
                        .requestMatchers("/rest/api/ticket/cancel/**").hasAnyAuthority("PASSENGER", "ADMIN")

                        // User
                        .requestMatchers("/rest/api/user/me").authenticated()
                        .requestMatchers("/rest/api/user/activate/**", "/rest/api/user/deactivate/**").hasAuthority("ADMIN")
                        .requestMatchers("/rest/api/user/save").hasAuthority("ADMIN")

                        // Auth needed for anything else
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}