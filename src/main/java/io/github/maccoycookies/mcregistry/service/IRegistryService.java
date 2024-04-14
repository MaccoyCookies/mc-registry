package io.github.maccoycookies.mcregistry.service;

import io.github.maccoycookies.mcregistry.model.InstanceMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/4/14 21:20
 * Description Interface for registry service
 */
public interface IRegistryService {

    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // TODO

    long renew(InstanceMeta instance, String ... services);

    Long version(String service);

    Map<String, Long> versions(String ... services);
}

