package se.alipsa.munin.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static se.alipsa.munin.config.Role.*;

/**
 * Security configuration class for setting up authentication and authorization.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnBean(WebConfig.class)
@AutoConfigureAfter(WebConfig.class)
public class SecurityConfig {

  private final DataSource dataSource;

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Dependency injection: storing DataSource reference is intentional and safe")
  @Autowired
  public SecurityConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Configures global authentication using JDBC with a data source.
   *
   * @return the UserDetailsService for authentication
   * @throws ConfigurationException if an error occurs during configuration
   */
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

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http the HttpSecurity object to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .headers(h -> h.frameOptions(o -> o.sameOrigin()))
        .authorizeHttpRequests( req ->
          req.requestMatchers(
              "/resetPassword",
              "/webjars/**",
              "/js/**",
              "/css/**",
              "/favicon.ico" ,
              "/img/**",
              "/actuator/health",
              "/h2-console/**",
              "/common/**"
              )
            .permitAll()
          .requestMatchers("/reports/**").authenticated()
          .requestMatchers(
              "/manage/**",
              "/api/**"
          )
            .hasRole(ROLE_ANALYST.getShortName())
          .requestMatchers( "/admin/**")
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
