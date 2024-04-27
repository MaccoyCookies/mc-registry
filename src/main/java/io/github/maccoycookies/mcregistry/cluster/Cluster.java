package io.github.maccoycookies.mcregistry.cluster;

import io.github.maccoycookies.mcregistry.config.McRegistryConfigProperties;
import io.github.maccoycookies.mcregistry.service.impl.McRegistryServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maccoy
 * @date 2024/4/20 10:16
 * Description Registry cluster
 */
@Slf4j
public class Cluster {

    @Value("${server.port}")
    private String port;

    private String host;

    Server MYSELF;

    McRegistryConfigProperties registryConfigProperties;

    @Getter
    List<Server> servers;

    public Cluster(McRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;
    }

    public void init() {
        try (InetUtils inetUtils = new InetUtils(new InetUtilsProperties())) {
            host = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
            log.info(" ===> findFirstNonLoopbackHostInfo = {}", host);
        } catch (Exception exception) {
            host = "127.0.0.1";
        }
        this.MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        log.info(" ===> myself = {}", MYSELF);

        initServers();

        ServerHealth serverHealth = new ServerHealth(this);
        serverHealth.checkServerHealth();
    }

    private void initServers() {
        List<Server> servers = new ArrayList<>();
        for (String url : registryConfigProperties.getServerList()) {
            Server server = new Server();
            if (url.contains("localhost")) {
                url.replace("localhost", host);
            } else if (url.contains("127.0.0.1")) {
                url.replace("127.0.0.1", host);
            }
            if (url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1);
                servers.add(server);
            }
        }
        this.servers = servers;
    }

    public Server self() {
        this.MYSELF.setVersion(McRegistryServiceImpl.VERSION.get());
        return this.MYSELF;
    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }

}
