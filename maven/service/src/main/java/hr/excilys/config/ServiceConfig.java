package hr.excilys.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"hr.excilys.service"})
public class ServiceConfig {

}
