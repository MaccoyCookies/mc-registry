package io.github.maccoycookies.mcregistry.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/4/14 21:22
 * Description Instance meta model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"scheme", "host", "port", "context"})
public class InstanceMeta {

    private String scheme;

    private String host;

    private Integer port;

    private String context;

    /**
     * online of offline
     */
    private boolean status;

    private Map<String, String> parameters = new HashMap<>();

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "mcrpc");
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public String toMetas() {
        return JSON.toJSONString(parameters);
    }

    public static InstanceMeta from(String url) {
        URI uri = URI.create(url);
        return new InstanceMeta(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath().substring(1));
    }


}
