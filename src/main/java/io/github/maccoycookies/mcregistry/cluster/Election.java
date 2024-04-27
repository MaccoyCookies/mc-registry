package io.github.maccoycookies.mcregistry.cluster;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/4/27 17:30
 * Description
 */
@Slf4j
public class Election {

    private void elect(List<Server> servers) {
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

    public void electLeader(List<Server> servers) {
        List<Server> leaders = servers.stream().filter(Server::isStatus).filter(Server::isLeader).collect(Collectors.toList());
        if (leaders.size() == 1) {
            log.info(" ===> no need election for leader: {}", leaders.get(0));
            return;
        }
        log.warn(" ===> elect for no leader or more than one leader: {}", leaders);
        elect(servers);
    }

}
