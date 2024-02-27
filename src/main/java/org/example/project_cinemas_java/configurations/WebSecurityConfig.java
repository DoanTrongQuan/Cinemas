package org.example.project_cinemas_java.configurations;


import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.filters.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    //Pair.of(String.format("%s/products", apiPrefix), "GET"),
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/users/confirm-register", apiPrefix),
                                    String.format("%s/users/forgot-password", apiPrefix),
                                    String.format("%s/users/confirm-new-password", apiPrefix),
                                    String.format("%s/movie/top-movies", apiPrefix)
                            )
                            .permitAll()
                            .requestMatchers(POST, String.format("/%s/user/refreshtoken",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/user/refreshtoken",apiPrefix)).hasRole("USER")
                            .requestMatchers(POST, String.format("/%s/users/change-password",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/users/change-password",apiPrefix)).hasRole("USER")

                            .requestMatchers(POST, String.format("/%s/cinema/create-cinema",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/room/create-room",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/seat/create-seat",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/food/create-food",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/movie/create-movie",apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(PUT, String.format("/%s/food/update-food",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/cinema/update-cinema",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/room/update-room",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/seat/update-seat",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/movie/update-movie",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE, String.format("/%s/cinema/delete-cinema",apiPrefix)).hasRole("ADMIN")

                            .anyRequest().authenticated();
                    //.anyRequest().permitAll();

                })
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}
