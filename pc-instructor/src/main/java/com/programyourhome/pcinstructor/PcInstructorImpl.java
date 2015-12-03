package com.programyourhome.pcinstructor;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.programyourhome.common.functional.FailableRunnable;
import com.programyourhome.pcinstructor.model.PyhDimension;
import com.programyourhome.pcinstructor.model.PyhDimensionImpl;
import com.programyourhome.pcinstructor.model.PyhPoint;
import com.programyourhome.pcinstructor.model.PyhPointImpl;

@Component
public class PcInstructorImpl implements PcInstructor {

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
    public void moveMouseAbsolute(final int x, final int y) {
        this.log.trace("Moving mouse (absolute) to position: (" + x + ", " + y + ").");
        this.tryRobot(() -> this.robot.mouseMove(x, y));
    }

    @Override
    public void moveMouseRelative(final int dx, final int dy) {
        final PyhPoint mousePosition = this.getMousePosition();
        final int newX = mousePosition.getX() + dx;
        final int newY = mousePosition.getY() + dy;
        this.log.trace("Moving mouse (relative) to position: (" + newX + ", " + newY + ").");
        this.tryRobot(() -> this.robot.mouseMove(newX, newY));
    }

    @Override
    public void clickLeftMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_LEFT);
    }

    @Override
    public void clickMiddleMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_MIDDLE);
    }

    @Override
    public void clickRightMouseButton() {
        this.clickMouseButton(MOUSE_BUTTON_RIGHT);
    }

    public void clickMouseButton(final int buttonMask) {
        this.tryRobot(() -> {
            this.robot.mousePress(buttonMask);
            this.robot.mouseRelease(buttonMask);
        });
    }

    @Override
    public void scrollMouseUp(final int amount) {
        // A negative amount means up.
        this.tryRobot(() -> this.robot.mouseWheel(-1 * amount));
    }

    @Override
    public void scrollMouseDown(final int amount) {
        this.tryRobot(() -> this.robot.mouseWheel(amount));
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
