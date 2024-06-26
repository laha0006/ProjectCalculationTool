package dev.tolana.projectcalculationtool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize annotation. // giver også @PostFilter annotation
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/organisation").authenticated()
                        .requestMatchers("/organisation/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/dashboard")
                        .failureUrl("/user/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/user/login?logout")
                        .permitAll());

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}