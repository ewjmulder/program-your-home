package com.programyourhome.ir;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.config.ConfigUtil;
import com.programyourhome.ir.config.Device;
import com.programyourhome.ir.config.InfraRedConfigHolder;
import com.programyourhome.ir.config.Key;
import com.programyourhome.ir.config.KeyType;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.ir.model.PyhDeviceImpl;
import com.programyourhome.ir.winlirc.WinLIRCClient;

@Component
@PropertySource("classpath:com/programyourhome/config/infra-red/properties/infra-red.properties")
public class InfraRedImpl implements InfraRed {

    // TODO: make asychronous, with internal queueing machanism that waits a small time between IR commands of different devices and
    // a configured time between IR command of the same device (general wait and possible override per key (like POWER)

    @Value("${winlirc.host}")
    private String winlircHost;

    @Value("${winlirc.port}")
    private int winlircPort;

    @Autowired
    private InfraRedConfigHolder configHolder;

    @Autowired
    private WinLIRCClient winLircClient;

    private final Map<Integer, DeviceState> deviceStates;

    public InfraRedImpl() {
        this.deviceStates = new HashMap<>();
    }

    @PostConstruct
    public void init() throws Exception {
        this.winLircClient.connect(this.winlircHost, this.winlircPort);
        // Assume all devices are turned off at server startup time.
        for (final Device device : this.configHolder.getConfig().getDevices()) {
            this.deviceStates.put(device.getId(), new DeviceState());
        }
    }

    @Override
    public Collection<PyhDevice> getDevices() {
        return this.configHolder.getConfig().getDevices().stream()
                .map(device -> new PyhDeviceImpl(device))
                .collect(Collectors.toList());
    }

    private Device getDevice(final int deviceId) {
        return this.configHolder.getConfig().getDevices().stream()
                .filter(device -> device.getId() == deviceId)
                .findFirst()
                .get();
    }

    private String getRemoteName(final int deviceId) {
        return this.getDevice(deviceId).getRemote().getName();
    }

    private String getKeyNameOfType(final int deviceId, final KeyType keyType) {
        return this.getKeyNameForPredicate(deviceId, key -> key.getType() == keyType);
    }

    private String getKeyName(final int deviceId, final int keyId) {
        return this.getKeyNameForPredicate(deviceId, key -> key.getId() == keyId);
    }

    private String getKeyNameForPredicate(final int deviceId, final Predicate<Key> predicate) {
        return ConfigUtil.extractAllKeys(this.getDevice(deviceId)).stream()
                .filter(predicate)
                .findFirst()
                .get().getName();
    }

    private void pressRemoteKeyType(final int deviceId, final KeyType keyType) {
        // TODO: Put the key pressing on a queue that takes devices and delays into account, see TODO on top of this class.
        this.pressRemoteKeyName(deviceId, this.getKeyNameOfType(deviceId, keyType));
    }

    private void pressRemoteKeyName(final int deviceId, final String keyName) {
        // TODO: Put the key pressing on a queue that takes devices and delays into account, see TODO on top of this class.
        this.winLircClient.pressRemoteKey(this.getRemoteName(deviceId), keyName);
    }

    @Override
    public void turnOn(final int deviceId) {
        // Only hit the power key if the device is currently off.
        if (this.deviceStates.get(deviceId).isTurnedOff()) {
            this.pressRemoteKeyType(deviceId, KeyType.POWER);
        }
    }

    @Override
    public void turnOff(final int deviceId) {
        // Only hit the power key if the device is currently on.
        if (this.deviceStates.get(deviceId).isTurnedOn()) {
            this.pressRemoteKeyType(deviceId, KeyType.POWER);
        }
    }

    @Override
    public void setInput(final int deviceId, final String input) {
        // The input parameter is actually the key name for the required input.
        this.pressRemoteKeyName(deviceId, input);
    }

    @Override
    public void volumeUp(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.VOLUME_UP);
    }

    @Override
    public void volumeDown(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.VOLUME_DOWN);
    }

    @Override
    public void volumeMute(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.VOLUME_MUTE);
    }

    @Override
    public void channelUp(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.CHANNEL_UP);
    }

    @Override
    public void channelDown(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.CHANNEL_DOWN);
    }

    @Override
    public void setChannel(final int deviceId, final int channel) {
        final String digitsToPress = Integer.toString(channel);
        // Press all digits in the channel number, from left to right.
        for (final char c : digitsToPress.toCharArray()) {
            this.pressRemoteKeyType(deviceId, KeyType.fromValue("channel" + c));
        }
    }

    @Override
    public void pressKeyOnRemote(final int deviceId, final int keyId) {
        this.pressRemoteKeyName(deviceId, this.getKeyName(deviceId, keyId));
    }

}
