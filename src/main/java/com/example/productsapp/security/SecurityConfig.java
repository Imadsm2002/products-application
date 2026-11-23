package com.example.productsapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définition des utilisateurs en mémoire avec mots de passe hashés par BCrypt
     * - user1 / 1234 : Rôle USER (Consultation, Recherche, Pagination)
     * - user2 / 1234 : Rôle USER (Consultation, Recherche, Pagination)
     * - admin / 1234 : Rôles USER + ADMIN (Ajout, Modification, Suppression)
     */
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode("1234");

        UserDetails user1 = User.withUsername("user1")
                .password(encodedPassword)
                .roles("USER")
                .build();

        UserDetails user2 = User.withUsername("user2")
                .password(encodedPassword)
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encodedPassword)
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, admin);
    }

    /**
     * Configuration de la chaîne de sécurité HTTP
     * Personnalisation de la protection Spring Security :
     * - Ressources statiques et console H2 accessibles
     * - Formulaire de login personnalisé
     * - Gestion des rôles USER et ADMIN
     * - Page 403 (Not Authorized)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Ressources publiques (CSS, JS, WebJars, images)
                .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Console H2 pour le débogage (accessible librement)
                .requestMatchers("/h2-console/**").permitAll()
                // Page de connexion et page 403
                .requestMatchers("/login", "/notAuthorized").permitAll()
                // Actions réservées exclusivement aux administrateurs (ADMIN)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Actions accessibles aux utilisateurs et administrateurs (USER ou ADMIN)
                .requestMatchers("/user/**", "/", "/index").hasRole("USER")
                // Toute autre requête doit être authentifiée
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/user/index", true)
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretProductsKey_2024")
                .tokenValiditySeconds(86400 * 7) // 7 jours
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/notAuthorized")
            );

        // Nécessaire pour autoriser l'affichage des frames de la console H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
