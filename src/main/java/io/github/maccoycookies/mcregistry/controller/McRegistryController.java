package io.github.maccoycookies.mcregistry.controller;

import io.github.maccoycookies.mcregistry.cluster.Cluster;
import io.github.maccoycookies.mcregistry.cluster.Server;
import io.github.maccoycookies.mcregistry.cluster.Snapshot;
import io.github.maccoycookies.mcregistry.model.InstanceMeta;
import io.github.maccoycookies.mcregistry.service.IRegistryService;
import io.github.maccoycookies.mcregistry.service.impl.McRegistryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/4/14 21:34
 * Description Rest controller for registry service
 */
@Slf4j
@RestController
public class McRegistryController {

    @Autowired
    private IRegistryService registryService;

    @Autowired
    private Cluster cluster;

    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info(" ===> register {} @ {}", service, instance);
        checkLeader();
        return registryService.register(service, instance);
    }

    private void checkLeader() {
        if (!cluster.self().isLeader()) {
            throw new RuntimeException("current server is not a leader, the leader is " + cluster.leader().getUrl());
        }
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info(" ===> unregister {} @ {}", service, instance);
        return registryService.unregister(service, instance);
    }

    @RequestMapping("/findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam String service) {
        log.info(" ===> findAllInstances {}", service);
        return registryService.getAllInstances(service);
    }

    @RequestMapping("/renew")
    public long renew(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info(" ===> renew {} @ {}", service, instance);
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public long renews(@RequestParam String services, @RequestBody InstanceMeta instance) {
        log.info(" ===> renews {} @ {}", services, instance);
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public Long version(@RequestParam String service) {
        log.info(" ===> version {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam String services) {
        log.info(" ===> versions {}", services);
        return registryService.versions(services.split(","));
    }

    @RequestMapping("/info")
    public Server info() {
        Server server = cluster.self();
        log.info(" ===> info {}", server);
        return server;
    }

    @RequestMapping("/cluster")
    public List<Server> cluster() {
        List<Server> servers = cluster.getServers();
        log.info(" ===> cluster {}", servers);
        return servers;
    }

    @RequestMapping("/leader")
    public Server leader() {
        Server server = cluster.leader();
        log.info(" ===> leader {}", server);
        return server;
    }

    @RequestMapping("/snapshot")
    public Snapshot snapshot() {
        return McRegistryServiceImpl.snapshot();
    }

}
