package io.github.maccoycookies.mcregistry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/4/20 10:19
 * Description
 */
@Data
@ConfigurationProperties(prefix = "mcregistry")
public class McRegistryConfigProperties {

    private List<String> serverList;

}
