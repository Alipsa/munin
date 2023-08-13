package se.alipsa.munin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static se.alipsa.munin.config.Role.*;

@Configuration
@EnableWebSecurity
@ConditionalOnBean(WebConfig.class)
@AutoConfigureAfter(WebConfig.class)
public class SecurityConfig {

  @Autowired
  private DataSource dataSource;

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  @Bean
  public UserDetailsService configureGlobal() throws ConfigurationException {
    try {
      var auth = new JdbcUserDetailsManager(dataSource);
      auth.setUsersByUsernameQuery("select username, password, enabled from users where username=?");
      auth.setAuthoritiesByUsernameQuery("select username, authority from authorities where username=?");
      return auth;
    } catch (Exception e) {
      throw new ConfigurationException("Error setting up authentication in configureGlobal()", e);
    }
  }

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(c -> c.disable())
        .headers(h -> h.frameOptions(o -> o.sameOrigin()))
        .authorizeHttpRequests( req ->
          req.requestMatchers(
              antMatcher("/resetPassword"),
              antMatcher("/webjars/**"),
              antMatcher("/js/**"),
              antMatcher("/css/**"),
              antMatcher("/favicon.ico") ,
              antMatcher("/img/**"),
              antMatcher("/actuator/health"),
              antMatcher("/h2-console/**"),
              antMatcher("/common/**")
              )
            .permitAll()
          .requestMatchers(antMatcher("/reports/**"))
            .authenticated()
          .requestMatchers(
              antMatcher("/manage/**"),
              antMatcher("/api/**")
          )
            .hasRole(ROLE_ANALYST.getShortName())
          .requestMatchers( antMatcher("/admin/**"))
            .hasRole(ROLE_ADMIN.getShortName())
          .anyRequest()
            .authenticated()
        )

        .formLogin(l -> l
            .loginPage("/login.html").permitAll()
            .failureUrl("/login.html").permitAll())

        .logout(lo -> lo.permitAll().logoutSuccessUrl("/login.html"))

        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
