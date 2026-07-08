package br.com.estoqueinox.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {
        boolean devProfile = environment.acceptsProfiles(Profiles.of("dev"));

        http
                .authorizeHttpRequests(authorize -> {
                    if (devProfile) {
                        authorize.requestMatchers(PathRequest.toH2Console()).permitAll();
                    } else {
                        authorize.requestMatchers("/h2-console/**").denyAll();
                    }

                    authorize
                            .requestMatchers("/", "/login").permitAll()
                            .requestMatchers("/produtos").hasAnyRole("ADMIN", "VENDEDORA")
                            .requestMatchers("/vendas", "/vendas/**").hasAnyRole("ADMIN", "VENDEDORA")
                            .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                            .requestMatchers("/dashboard").authenticated()
                            .anyRequest().authenticated();
                })
                .csrf(csrf -> {
                    if (devProfile) {
                        csrf.ignoringRequestMatchers(PathRequest.toH2Console());
                    }
                })
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
