package io.github.maccoycookies.mcregistry.cluster;

import io.github.maccoycookies.mcregistry.http.HttpInvoker;
import io.github.maccoycookies.mcregistry.service.impl.McRegistryServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/4/27 17:26
 * Description
 */
@Slf4j
@Data
public class ServerHealth {

    final Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    long interval = 5_000;

    public void checkServerHealth() {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                // 更新服务状态
                updateServers();
                // 选主
                doElect();
                // 同步服务快照
                syncSnapshotFromLeader();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void doElect() {
        new Election().electLeader(cluster.getServers());
    }

    private void syncSnapshotFromLeader() {
        Server self = cluster.self();
        Server leader = cluster.leader();
        log.info(" ===>>> leader version: " + leader.getVersion() + ", my version: " + cluster.self().getVersion());
        if (!self.isLeader() && self.getVersion() < leader.getVersion()) {
            log.debug(" ===>>> sync snapshot from leader: " + leader);
            Snapshot snapshot = HttpInvoker.httpGet(leader.getUrl() + "/snapshot", Snapshot.class);
            log.debug(" ===>>> sync and restore snapshot: " + snapshot);
            McRegistryServiceImpl.restore(snapshot);
        }
    }

    private void updateServers() {
        List<Server> servers = cluster.getServers();
        servers.stream().parallel().forEach(server -> {
            try {
                if (server.equals(cluster.MYSELF)) return;
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception exception) {
                log.error(" ===> health check failed for {}", server);
                server.setStatus(false);
                server.setLeader(false);
            }
        });
    }
}
