package io.github.maccoycookies.mcregistry.cluster;

import io.github.maccoycookies.mcregistry.config.McRegistryConfigProperties;
import io.github.maccoycookies.mcregistry.http.HttpInvoker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    long timeout = 5_000;

    public Cluster(McRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;
    }

    public void init() {
        try (InetUtils inetUtils = new InetUtils(new InetUtilsProperties())) {
            host = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
            System.out.println(" ===> findFirstNonLoopbackHostInfo = " + host);
        } catch (Exception exception) {
            host = "127.0.0.1";
        }
        this.MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        System.out.println(" ===> myself = " + MYSELF);

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

        executorService.scheduleWithFixedDelay(() -> {
            try {
                updateServers();
                electLeader();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 0, timeout, TimeUnit.MILLISECONDS);

    }

    private void electLeader() {
        List<Server> leaders = this.servers.stream().filter(Server::isStatus).filter(Server::isLeader).collect(Collectors.toList());
        if (leaders.size() == 1) {
            log.info(" ===> no need election for leader: {}", leaders.get(0));
            return;
        }
        log.info(" ===> elect for no leader or more than one leader: {}", leaders);
        elect();
    }

    private void elect() {
        // 1.各种节点自己选，算法保证大家选的是同一个
        // 2.外部有一个分布式锁，谁拿到锁，谁是主
        // 3.分布式一致性算法 比如paxos、raft
        Server candidate = null;
        for (Server server : servers) {
            server.setLeader(false);
            if (!server.isStatus()) continue;
            if (candidate == null) {
                candidate = server;
            } else {
                if (candidate.hashCode() > server.hashCode()) {
                    candidate = server;
                }
            }
        }

        if (candidate != null) {
            candidate.setLeader(true);
            log.info(" ===> elect for leader: {}", candidate);
        } else {
            log.info(" ===> elect failed for no leaders: {}", servers);
        }
    }

    private void updateServers() {
        for (Server server : servers) {
            try {
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception exception) {
                System.out.println(" ===> health check failed for " + server);
                server.setStatus(false);
                server.setLeader(false);
            }
        }
    }

    public Server self() {
        return this.MYSELF;
    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }

}
