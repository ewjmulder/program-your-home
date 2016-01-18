package com.programyourhome.pcinstructor;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.programyourhome.common.functional.FailableRunnable;
import com.programyourhome.pcinstructor.model.KeyPress;
import com.programyourhome.pcinstructor.model.MouseButton;
import com.programyourhome.pcinstructor.model.MouseScroll;
import com.programyourhome.pcinstructor.model.PyhDimension;
import com.programyourhome.pcinstructor.model.PyhDimensionImpl;
import com.programyourhome.pcinstructor.model.PyhPoint;
import com.programyourhome.pcinstructor.model.PyhPointImpl;
import com.programyourhome.pcinstructor.model.ScrollDirection;

@Component
public class PcInstructorImpl implements PcInstructor {

    // TODO: make configurable
    private static final int DEFAULT_KEY_PRESS_MILLIS = 50;

    private static final int MOUSE_BUTTON_LEFT = InputEvent.BUTTON1_DOWN_MASK;
    private static final int MOUSE_BUTTON_MIDDLE = InputEvent.BUTTON2_DOWN_MASK;
    private static final int MOUSE_BUTTON_RIGHT = InputEvent.BUTTON3_DOWN_MASK;

    private final Log log = LogFactory.getLog(this.getClass());

    private Robot robot;

    public PcInstructorImpl() {
        this.tryRobot(() -> this.robot = new Robot());
    }

    private int round(final double input) {
        return (int) Math.round(input);
    }

    @Override
    public PyhDimension getScreenResolution() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new PyhDimensionImpl(this.round(screenSize.getWidth()), this.round(screenSize.getHeight()));
    }

    @Override
    public PyhPoint getMousePosition() {
        final Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        return new PyhPointImpl(mousePosition.x, mousePosition.y);
    }

    @Override
    public synchronized void moveMouseAbsolute(final int x, final int y) {
        this.log.trace("Moving mouse (absolute) to position: (" + x + ", " + y + ").");
        this.tryRobot(() -> this.robot.mouseMove(x, y));
    }

    @Override
    public synchronized void moveMouseRelative(final int dx, final int dy) {
        final PyhPoint mousePosition = this.getMousePosition();
        final int newX = mousePosition.getX() + dx;
        final int newY = mousePosition.getY() + dy;
        this.log.trace("Moving mouse (relative) to position: (" + newX + ", " + newY + ").");
        this.tryRobot(() -> this.robot.mouseMove(newX, newY));
    }

    @Override
    public synchronized void clickMouseButton(final MouseButton mouseButton) {
        if (mouseButton == MouseButton.LEFT) {
            this.clickLeftMouseButton();
        } else if (mouseButton == MouseButton.MIDDLE) {
            this.clickMiddleMouseButton();
        } else if (mouseButton == MouseButton.RIGHT) {
            this.clickRightMouseButton();
        } else {
            throw new IllegalArgumentException("Unknown mouse button: " + mouseButton);
        }
    }

    @Override
    public synchronized void clickLeftMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_LEFT);
    }

    @Override
    public synchronized void clickMiddleMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_MIDDLE);
    }

    @Override
    public synchronized void clickRightMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_RIGHT);
    }

    private void clickMouseButton(final int buttonMask) {
        this.tryRobot(() -> {
            this.robot.mousePress(buttonMask);
            this.robot.mouseRelease(buttonMask);
        });
    }

    @Override
    public synchronized void scrollMouse(final MouseScroll mouseScroll) {
        if (mouseScroll.getDirection() == ScrollDirection.UP) {
            this.scrollMouseUp(mouseScroll.getAmount());
        } else if (mouseScroll.getDirection() == ScrollDirection.DOWN) {
            this.scrollMouseDown(mouseScroll.getAmount());
        } else {
            throw new IllegalArgumentException("Unknown scroll direction: " + mouseScroll.getDirection());
        }
    }

    @Override
    public synchronized void scrollMouseUp(final int amount) {
        // A negative amount means up.
        this.tryRobot(() -> this.robot.mouseWheel(-1 * amount));
    }

    @Override
    public synchronized void scrollMouseDown(final int amount) {
        this.tryRobot(() -> this.robot.mouseWheel(amount));
    }

    @Override
    public synchronized void pressKey(final KeyPress keyPress) {
        this.tryRobot(() -> {
            final boolean withShift = keyPress.isShift() || keyPress.getKey().isShift();
            this.conditionalKeyFunction(withShift, KeyEvent.VK_SHIFT, this.robot::keyPress);
            this.conditionalKeyFunction(keyPress.isControl(), KeyEvent.VK_CONTROL, this.robot::keyPress);
            this.conditionalKeyFunction(keyPress.isAlt(), KeyEvent.VK_ALT, this.robot::keyPress);
            this.conditionalKeyFunction(keyPress.isSuper(), KeyEvent.VK_WINDOWS, this.robot::keyPress);
            this.robot.keyPress(keyPress.getKey().getKeyCode());

            // Wait for the specified key-press time.
            if (keyPress.getMillis() != null) {
                this.robot.delay(keyPress.getMillis());
            } else {
                this.robot.delay(DEFAULT_KEY_PRESS_MILLIS);
            }

            this.robot.keyRelease(keyPress.getKey().getKeyCode());
            this.conditionalKeyFunction(keyPress.isSuper(), KeyEvent.VK_WINDOWS, this.robot::keyRelease);
            this.conditionalKeyFunction(keyPress.isAlt(), KeyEvent.VK_ALT, this.robot::keyRelease);
            this.conditionalKeyFunction(keyPress.isControl(), KeyEvent.VK_CONTROL, this.robot::keyRelease);
            this.conditionalKeyFunction(withShift, KeyEvent.VK_SHIFT, this.robot::keyRelease);
        });
    }

    private void conditionalKeyFunction(final boolean condition, final int keyCode, final Consumer<Integer> keyFunction) {
        if (condition) {
            keyFunction.accept(keyCode);
        }
    }

    private void tryRobot(final FailableRunnable<Exception> tryBlock) {
        try {
            tryBlock.run();
        } catch (final Exception e) {
            final String message = "Exception occured while executing PC instructor task.";
            this.log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

}
