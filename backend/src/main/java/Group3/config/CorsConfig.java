package Group3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Configuration class for handling Cross-Origin Resource Sharing (CORS).
 *
 * This class defines allowed origins, HTTP methods, headers,
 * and credentials for cross-origin requests.
 */
@Configuration
public class CorsConfig {

  @Value("${frontend.origin:http://localhost:5173}")
  private String frontendOrigins;
  /**
   * Default constructor for CorsConfig.
   */
  public CorsConfig() {
  }
/**
   * Configures CORS settings for the application.
   *
   * Allows requests from specified frontend origins and
   * supports common HTTP methods and headers.
   *
   * @return a WebMvcConfigurer that applies CORS configuration
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = Arrays.stream(frontendOrigins.split(","))
          .map(String::trim)
          .filter(origin -> !origin.isEmpty())
          .toArray(String[]::new);

        registry.addMapping("/**")
          .allowedOriginPatterns(allowedOrigins)
          .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true)
          .maxAge(3600);
      }
    };
  }
}