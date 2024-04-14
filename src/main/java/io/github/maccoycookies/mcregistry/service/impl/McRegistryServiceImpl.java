package io.github.maccoycookies.mcregistry.service.impl;

import io.github.maccoycookies.mcregistry.model.InstanceMeta;
import io.github.maccoycookies.mcregistry.service.IRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/4/14 21:24
 * Description Default implementation of RegistryService.
 */
@Slf4j
public class McRegistryServiceImpl implements IRegistryService {

    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();

    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();

    final static AtomicLong VERSION = new AtomicLong(0);

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas != null && instanceMetas.contains(instance)) {
            log.info(" ===> instance {} already registered", instance.toUrl());
            instance.setStatus(true);
            return instance;
        }
        log.info(" ===> register instance {}", instance.toUrl());
        REGISTRY.add(service, instance);
        instance.setStatus(true);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas == null || instanceMetas.isEmpty()) {
            return null;
        }
        log.info(" ===> unregister instance {}", instance.toUrl());
        instanceMetas.removeIf((meta) -> meta.equals(instance));
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }

    public long renew(InstanceMeta instance, String ... services) {
        // VERSIONS.put(service, VERSION.incrementAndGet());
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMPS.put(service + "@" + instance.toUrl(), now);
        }
        return now;
    }

    public Long version(String service) {
        return VERSIONS.get(service);
    }

    public Map<String, Long> versions(String ... services) {
        return Arrays.stream(services).filter(VERSIONS::containsKey)
                .collect(Collectors.toMap(key -> key, VERSIONS::get, (o1, o2) -> o2));
    }

}