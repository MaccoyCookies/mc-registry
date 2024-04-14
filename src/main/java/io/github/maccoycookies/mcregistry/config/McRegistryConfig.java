package io.github.maccoycookies.mcregistry.config;

import io.github.maccoycookies.mcregistry.health.HealthChecker;
import io.github.maccoycookies.mcregistry.health.McHealthChecker;
import io.github.maccoycookies.mcregistry.service.IRegistryService;
import io.github.maccoycookies.mcregistry.service.impl.McRegistryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Maccoy
 * @date 2024/4/14 21:35
 * Description configuration for all beans.
 */
@Configuration
public class McRegistryConfig {

    @Bean
    public IRegistryService registryService() {
        return new McRegistryServiceImpl();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(@Autowired IRegistryService registryService) {
        return new McHealthChecker(registryService);
    }
}
