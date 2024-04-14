package io.github.maccoycookies.mcregistry.health;

import io.github.maccoycookies.mcregistry.model.InstanceMeta;
import io.github.maccoycookies.mcregistry.service.IRegistryService;
import io.github.maccoycookies.mcregistry.service.impl.McRegistryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/4/14 22:12
 * Description Default implementation of HealthChecker
 */
@Slf4j
public class McHealthChecker implements HealthChecker {

    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    long timeout = 20_000;

    private final IRegistryService registryService;

    public McHealthChecker(@Autowired IRegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void start() {
        executorService.scheduleWithFixedDelay(() -> {
            log.info(" ===> Health checker running ...");
            long now = System.currentTimeMillis();
            for (Map.Entry<String, Long> entry : McRegistryServiceImpl.TIMESTAMPS.entrySet()) {
                if (now - entry.getValue() > timeout) {
                    log.info(" ===> Health checker: {} is down", entry.getKey());
                    int index = entry.getKey().indexOf("@");
                    String service = entry.getKey().substring(0, index);
                    String url = entry.getKey().substring(index + 1);
                    InstanceMeta instance = InstanceMeta.from(url);
                    registryService.unregister(service, instance);
                    McRegistryServiceImpl.TIMESTAMPS.remove(entry.getKey());
                }
            }

        }, 10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {

    }
}
