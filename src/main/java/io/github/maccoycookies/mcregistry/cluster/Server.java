package io.github.maccoycookies.mcregistry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Maccoy
 * @date 2024/4/20 10:16
 * Description Registry server instance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {

    private String url;

    private boolean status;

    private boolean leader;

    private long version;

}
