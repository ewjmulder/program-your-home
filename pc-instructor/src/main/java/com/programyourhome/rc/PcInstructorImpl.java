package com.programyourhome.rc;

import java.awt.Robot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.programyourhome.common.functional.RunnableWithException;
import com.programyourhome.pc.PcInstructor;

@Component
public class PcInstructorImpl implements PcInstructor {

    private final Log log = LogFactory.getLog(this.getClass());

    private Robot robot;

    public PcInstructorImpl() {
        this.tryRobot(() -> this.robot = new Robot());
    }

    @Override
    public void moveMouseAbsolute(final int x, final int y) {
        this.log.trace("Moving mouse (absolute) to position: (" + x + ", " + y + ").");
        this.tryRobot(() -> this.robot.mouseMove(x, y));
    }

    @Override
    public void moveMouseRelative(final int dx, final int dy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void clickLeftMouseButton() {
        // TODO Auto-generated method stub
    }

    @Override
    public void clickRightMouseButton() {
        // TODO Auto-generated method stub
    }

    @Override
    public void clickMiddleMouseButton() {
        // TODO Auto-generated method stub
    }

    private void tryRobot(final RunnableWithException<Exception> tryBlock) {
        try {
            tryBlock.run();
        } catch (final Exception e) {
            final String message = "Exception occured while executing PC instructor task.";
            this.log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

}
