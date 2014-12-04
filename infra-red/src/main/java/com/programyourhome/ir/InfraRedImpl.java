package com.programyourhome.ir;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.model.PyhRemote;

@Component
@PropertySource("classpath:com/programyourhome/config/infra-red/properties/infra-red.properties")
public class InfraRedImpl implements InfraRed {

    @Value("${winlirc.host}")
    private String winlircHost;

    @Value("${winlirc.port}")
    private int winlircPort;

    @Autowired
    private WinLIRCClient irClient;

    @PostConstruct
    public void init() throws Exception {
        this.irClient.connect(this.winlircHost, this.winlircPort);
    }

    /**
     * TODO: load xml configuration and validate:(non-Javadoc)
     * 
     * <pre>
     * - Internal XML:
     *   - Unique device names (PYH common utils thingy, since has overlap with server stuff (is ok, see as apache commons alternative))
     *   - Unique key names per device
     *   - Prototypes vs actual key type mapping
     * - Consistency check with winlirc data
     *   - Remote name mapping
     *   - (winlirc) Key name mapping
     * </pre>
     */

    @Override
    public Collection<PyhRemote> getRemotes() {
        // Use this construction so we don't have issues with generics.
        return this.irClient.getRemotes().stream()
                .collect(Collectors.toList());
    }

    // TODO: provide more generic control over devices, making use of the defined types like power, input, volume, channel.
    // This module then translates that to the required key presses.

    @Override
    // TODO: make asychronous, with internal queueing machanism that waits a small time between IR commands of different
    // devices and
    // a configured time between IR command of the same device (general wait and possible override per key (like POWER)
    public void pressRemoteKey(final String remoteName, final String key) {
        this.irClient.pressRemoteKey(remoteName, key);
    }

}
