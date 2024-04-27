package io.github.maccoycookies.mcregistry.cluster;

import io.github.maccoycookies.mcregistry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/4/27 16:19
 * Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot {

    LinkedMultiValueMap<String, InstanceMeta> REGISTRY;
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMPS;
    long version;

}
