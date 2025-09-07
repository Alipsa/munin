package se.alipsa.munin.config;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining beans used in the application.
 */
@Configuration
public class BeanConfig {

  /**
   * Default constructor.
   */
  public BeanConfig() {
    // Default constructor
  }

  /**
   * Creates and configures a CronParser bean for parsing Spring-style cron expressions.
   *
   * @return a CronParser configured for Spring cron expressions
   */
  @Bean("springCronParser")
  public CronParser springCronParser() {
    return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
  }
}
