package se.alipsa.munin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.sql.DataSource;

import static se.alipsa.munin.config.Role.*;

@Configuration
@EnableWebSecurity
@ConditionalOnBean(WebConfig.class)
@AutoConfigureAfter(WebConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
        .disable()
        .headers().frameOptions().sameOrigin()
        .and()
        //.csrfTokenRepository(csrfTokenRepository())
        //.and()
        .authorizeRequests()
          .antMatchers("/resetPassword", "/webjars/**", "/js/**", "/css/**", "/favicon.ico" , "/img/**",
              "/actuator/health", "/h2-console/**", "/common/**")
            .permitAll()
          .antMatchers("/reports/**")
            .authenticated()
          .antMatchers("/manage/**", "/api/**")
            .hasRole(ROLE_ANALYST.getShortName())
          .antMatchers( "/admin/**")
            .hasRole(ROLE_ADMIN.getShortName())
          .anyRequest()
            .authenticated()

        .and()
        .formLogin()
        .loginPage("/login.html").permitAll()
        .failureUrl("/login.html").permitAll()
        .and()
        .logout().permitAll()
        .logoutSuccessUrl("/login.html")
        .and()
        .httpBasic();
    /*
        .and()
        .cors().and()
        .csrf()
        .csrfTokenRepository(csrfTokenRepository())

        .anyRequest().denyAll() // Reject anything else not matched above

        .and().headers().frameOptions().sameOrigin()
        .and().httpBasic()

        // Uncomment this to change to enforce SSL
        //.and().requiresChannel().antMatchers(BASE_PATH + "/**").requiresSecure()
        .and().requiresChannel().anyRequest().requiresInsecure()

     */
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
