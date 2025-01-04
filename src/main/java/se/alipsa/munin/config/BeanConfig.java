package se.alipsa.munin.config;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class BeanConfig {

  @Bean("springCronParser")
  public CronParser springCronParser() {
    return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
  }
}
