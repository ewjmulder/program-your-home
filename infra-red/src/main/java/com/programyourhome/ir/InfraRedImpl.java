package com.programyourhome.ir;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
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
@ConditionalOnProperty("environment.live")
@PropertySource("classpath:com/programyourhome/config/infra-red/properties/infra-red.properties")
public class InfraRedImpl implements InfraRed {

    // TODO: use logging framework for logging instead of sysout
    private final Log log = LogFactory.getLog(this.getClass());

    // TODO: make asychronous, with internal queueing machanism that waits a small time between IR commands of different devices and
    // a configured time between IR command of the same device (general wait and possible override per key (like POWER)

    // TODO: failover possiblilities for non existing key types / names? (instead of Optional.get())

    @Autowired
    private TaskScheduler pressRemoteKeyScheduler;

    // TODO: document: a queue with keys to press for every device. always retains the last key pressed, to be able to check if the required delay has passed.
    // TODO: move queueing mechanism to seperate class?
    private final Map<String, Queue<RemoteKeyPress>> keyPressQueues;

    @Value("${winlirc.host}")
    private String winlircHost;

    @Value("${winlirc.port}")
    private int winlircPort;

    @Value("${keyPressInterval}")
    private int keyPressInterval;

    @Autowired
    private InfraRedConfigHolder configHolder;

    @Autowired
    private WinLIRCClient winLircClient;

    private final Map<Integer, DeviceState> deviceStates;

    public InfraRedImpl() {
        this.deviceStates = new HashMap<>();
        this.keyPressQueues = new HashMap<>();
    }

    @PostConstruct
    public void init() throws Exception {
        this.winLircClient.connect(this.winlircHost, this.winlircPort);
        // Fake a non-blocking last key press to start off each queue.
        final RemoteKeyPress dummyKeyPress = new RemoteKeyPress("dummy", "dummy", 0);
        dummyKeyPress.press();
        for (final Device device : this.configHolder.getConfig().getDevices()) {
            // Assume all devices are turned off at server startup time.
            this.deviceStates.put(device.getId(), new DeviceState());
            this.keyPressQueues.put(device.getName(), new LinkedList<>(Arrays.asList(dummyKeyPress)));
        }
        this.pressRemoteKeyScheduler.scheduleAtFixedRate(this::pressKeys,
                DateUtils.addMilliseconds(new Date(), this.keyPressInterval), this.keyPressInterval);
        this.log.info("Scheduler: 'press remote key' successfully started with interval: " + this.keyPressInterval);
    }

    private void pressKeys() {
        for (final String deviceName : this.keyPressQueues.keySet()) {
            final Queue<RemoteKeyPress> queue = this.keyPressQueues.get(deviceName);
            if (queue.size() > 1) {
                final RemoteKeyPress lastKeyPress = queue.peek();
                // Can we press the next key yet? True if the current time is past the last key pressed time + the required delay.
                if (System.currentTimeMillis() > lastKeyPress.getPressedOn() + lastKeyPress.getDelay()) {
                    // Remove the last pressed key.
                    queue.poll();
                    // The new head is our next key to press.
                    final RemoteKeyPress nextKeyPress = queue.peek();
                    nextKeyPress.press();
                    this.winLircClient.pressRemoteKey(nextKeyPress.getRemoteName(), nextKeyPress.getKeyName());
                    this.log.info("Successfully pressed key: '" + nextKeyPress.getKeyName() + "' on remote: '" + nextKeyPress.getRemoteName() + "'.");
                }
            }
        }
    }

    @Override
    public Collection<PyhDevice> getDevices() {
        return this.configHolder.getConfig().getDevices().stream()
                .map(device -> new PyhDeviceImpl(device))
                .collect(Collectors.toList());
    }

    private Device getDevice(final int deviceId) {
        return this.getDeviceByPredicate(device -> device.getId() == deviceId);
    }

    private Device getDevice(final String deviceName) {
        return this.getDeviceByPredicate(device -> device.getName().equals(deviceName));
    }

    // TODO: generify even this method by getXbyPred<X> with generic stream/collection -> google if it might already exists in the Java API.
    // If own implementation, move to PyhUtils
    private Device getDeviceByPredicate(final Predicate<Device> predicate) {
        return this.configHolder.getConfig().getDevices().stream()
                .filter(predicate)
                .findFirst()
                .get();
    }

    private String getRemoteName(final int deviceId) {
        return this.getDevice(deviceId).getRemote().getName();
    }

    private boolean hasKeyOfType(final int deviceId, final KeyType keyType) {
        return this.getOptionKeyByPredicate(deviceId, key -> key.getType() == keyType).isPresent();
    }

    private String getKeyNameOfType(final int deviceId, final KeyType keyType) {
        return this.getKeyNameByPredicate(deviceId, key -> key.getType() == keyType);
    }

    private String getKeyName(final int deviceId, final int keyId) {
        return this.getKeyNameByPredicate(deviceId, key -> key.getId() == keyId);
    }

    private Key getKeyByName(final int deviceId, final String keyName) {
        return this.getKeyByPredicate(deviceId, key -> key.getName().equals(keyName));
    }

    private String getKeyNameByPredicate(final int deviceId, final Predicate<Key> predicate) {
        return this.getKeyByPredicate(deviceId, predicate).getName();
    }

    private Key getKeyByPredicate(final int deviceId, final Predicate<Key> predicate) {
        return this.getOptionKeyByPredicate(deviceId, predicate).get();
    }

    private Optional<Key> getOptionKeyByPredicate(final int deviceId, final Predicate<Key> predicate) {
        return ConfigUtil.extractAllKeys(this.getDevice(deviceId)).stream()
                .filter(predicate)
                .findFirst();
    }

    private void pressRemoteKeyType(final int deviceId, final KeyType keyType) {
        this.pressRemoteKeyName(deviceId, this.getKeyNameOfType(deviceId, keyType));
    }

    private void pressRemoteKeyName(final int deviceId, final String keyName) {
        final Key key = this.getKeyByName(deviceId, keyName);
        // Put the key press on the queue for that device.
        this.keyPressQueues.get(this.getDevice(deviceId).getName()).add(new RemoteKeyPress(this.getRemoteName(deviceId), key.getWinlircName(), key.getDelay()));
    }

    @Override
    public synchronized void turnOn(final int deviceId) {
        // Only hit the power key if the device is currently off.
        if (this.deviceStates.get(deviceId).isTurnedOff()) {
            this.deviceStates.get(deviceId).turnOn();
            this.pressRemoteKeyType(deviceId, KeyType.POWER);
        }
    }

    @Override
    public synchronized void turnOff(final int deviceId) {
        // Only hit the power key if the device is currently on.
        if (this.deviceStates.get(deviceId).isTurnedOn()) {
            this.deviceStates.get(deviceId).turnOff();
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

    // TODO: keep track of play / pause state? (or in main activity)
    @Override
    public void play(final int deviceId) {
        if (this.hasKeyOfType(deviceId, KeyType.PLAY)) {
            this.pressRemoteKeyType(deviceId, KeyType.PLAY);
        } else {
            this.pressRemoteKeyType(deviceId, KeyType.PLAY_PAUSE);
        }
    }

    @Override
    public void pause(final int deviceId) {
        if (this.hasKeyOfType(deviceId, KeyType.PAUSE)) {
            this.pressRemoteKeyType(deviceId, KeyType.PAUSE);
        } else {
            this.pressRemoteKeyType(deviceId, KeyType.PLAY_PAUSE);
        }
    }

    @Override
    public void stop(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.STOP);
    }

    @Override
    public void fastForward(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.FAST_FORWARD);
    }

    @Override
    public void rewind(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.REWIND);
    }

    @Override
    public void skipNext(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.SKIP_NEXT);
    }

    @Override
    public void skipPrevious(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.SKIP_PREVIOUS);
    }

    @Override
    public void record(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.RECORD);
    }

    @Override
    public void menuToggle(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_TOGGLE);
    }

    @Override
    public void menuSelect(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_SELECT);
    }

    @Override
    public void menuBack(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_BACK);
    }

    @Override
    public void menuUp(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_ARROW_UP);
    }

    @Override
    public void menuDown(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_ARROW_DOWN);
    }

    @Override
    public void menuLeft(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_ARROW_LEFT);
    }

    @Override
    public void menuRight(final int deviceId) {
        this.pressRemoteKeyType(deviceId, KeyType.MENU_ARROW_RIGHT);
    }

    @Override
    public void pressKeyOnRemote(final int deviceId, final int keyId) {
        this.pressRemoteKeyName(deviceId, this.getKeyName(deviceId, keyId));
    }

}
