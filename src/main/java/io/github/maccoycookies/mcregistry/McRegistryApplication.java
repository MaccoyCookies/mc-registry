package io.github.maccoycookies.mcregistry;

import io.github.maccoycookies.mcregistry.config.McRegistryConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({McRegistryConfigProperties.class})
public class McRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(McRegistryApplication.class, args);
    }

}
