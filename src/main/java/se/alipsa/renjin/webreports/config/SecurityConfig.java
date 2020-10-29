package se.alipsa.renjin.webreports.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ConditionalOnBean(WebConfig.class)
@AutoConfigureAfter(WebConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  // Note that spring boot does some magic on role names and prefixes the, with ROLE_,
  // hence we use the constants in the code but the DB contains the full ROLE_xxx name.
  public static final String ROLE_WEB = "USER";
  public static final String ROLE_ANALYST = "ANALYST";
  public static final String ROLE_ADMIN = "ADMIN";

  @Autowired
  private DataSource dataSource;

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws ConfigurationException {
    try {
      auth.jdbcAuthentication().dataSource(dataSource)
          .usersByUsernameQuery("select username, password, enabled from users where username=?")
          .authoritiesByUsernameQuery("select username, authority from authorities where username=?");
    } catch (Exception e) {
      throw new ConfigurationException("Error setting up authentication in configureGlobal()", e);
    }

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors().and()
        .csrf()
          .csrfTokenRepository(csrfTokenRepository())
          .ignoringAntMatchers("/test/**")
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/health", "/h2-console/**", "/webjars/**", "/test/**", "/", "/favicon.ico")
          .permitAll()// disable security for resources and simulator endpoints
        .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/configuration/ui", "/swagger-resources/**")
          .authenticated()
        .antMatchers("/reports/**")
          .hasRole(ROLE_WEB)
        .antMatchers("/manage/**")
          .hasRole(ROLE_ANALYST)
        .antMatchers( "/admin/**")
          .hasRole(ROLE_ADMIN)
        .anyRequest().denyAll() // Reject anything else not matched above

        .and().headers().frameOptions().sameOrigin()
        .and().httpBasic()

        // Uncomment this to change to enforce SSL
        //.and().requiresChannel().antMatchers(BASE_PATH + "/**").requiresSecure()
        .and().requiresChannel().anyRequest().requiresInsecure()

        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
    return CookieCsrfTokenRepository.withHttpOnlyFalse();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
