package com.example.reactive.core;


import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;

//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
public class SecurityConfig {

//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//
//        UserDetails user = User
//                .withUsername("user")
//                .password(passwordEncoder().encode("userpwd"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User
//                .withUsername("admin")
//                .password(passwordEncoder().encode("adminpwd"))
//                .roles("ADMIN")
//                .build();
//
//        return new MapReactiveUserDetailsService(user, admin);
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http.authorizeExchange()
//                .pathMatchers("/students/admin")
//                .hasAuthority("ROLE_ADMIN")
//                .anyExchange()
//                .authenticated()
//                .and().httpBasic()
//                .and().build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

}
