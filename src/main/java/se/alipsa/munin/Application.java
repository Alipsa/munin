package se.alipsa.munin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.alipsa.munin.service.FileStorageProperties;
import se.alipsa.munin.service.FileStorageService;

/**
 * Main application class for the Munin application.
 * It initializes the Spring Boot application and sets up file storage properties.
 */
@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
@EnableScheduling
public class Application {

  /**
   * Default constructor.
   */
  public Application() {
    // Default constructor
  }

  /**
   * The main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * Initializes the file storage service on application startup.
   *
   * @param fileStorageService the file storage service to be initialized
   * @return a CommandLineRunner that initializes the file storage service
   */
  @Bean
  CommandLineRunner init(FileStorageService fileStorageService) {
    return (args) -> {
      fileStorageService.init();
    };
  }
}
