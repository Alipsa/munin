package se.alipsa.munin.config;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @Bean
  public CronParser cronParser() {
    return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
  }
}
