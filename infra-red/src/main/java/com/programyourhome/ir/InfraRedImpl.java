package com.programyourhome.ir;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
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

    private final ScheduledExecutorService pressRemoteKeyService;
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

        // TODO: put somewhere else and reuse
        final ThreadFactory factory = new ThreadFactory() {

            @Override
            public Thread newThread(final Runnable target) {
                final Thread thread = new Thread(target);
                thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                    @Override
                    public void uncaughtException(final Thread t, final Throwable e) {
                        e.printStackTrace();
                    }

                });
                return thread;
            }

        };

        this.pressRemoteKeyService = Executors.newScheduledThreadPool(1, factory);
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
        final ScheduledFuture future = this.pressRemoteKeyService.scheduleAtFixedRate(this::pressKeys, this.keyPressInterval, this.keyPressInterval,
                TimeUnit.MILLISECONDS);
        // this.pressRemoteKeyService.execute(new Runnable() {
        //
        // @Override
        // public void run() {
        // try {
        // future.get();
        // // dead code here?
        // } catch (final InterruptedException e) {
        // e.printStackTrace();
        // } catch (final CancellationException e) {
        // e.printStackTrace();
        // } catch (final ExecutionException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // });
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
                    // System.out.println("pressRemoteKey(" + nextKeyPress.getRemoteName() + ", " + nextKeyPress.getKeyName() + ")");
                    this.winLircClient.pressRemoteKey(nextKeyPress.getRemoteName(), nextKeyPress.getKeyName());
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
        return ConfigUtil.extractAllKeys(this.getDevice(deviceId)).stream()
                .filter(predicate)
                .findFirst()
                .get();
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

    @Override
    public void pressKeyOnRemote(final int deviceId, final int keyId) {
        this.pressRemoteKeyName(deviceId, this.getKeyName(deviceId, keyId));
    }

}
