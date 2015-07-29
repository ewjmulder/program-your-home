package com.programyourhome.pc;

import com.programyourhome.pc.model.PyhDimension;
import com.programyourhome.pc.model.PyhPoint;

public interface PcInstructor {

    public PyhDimension getScreenResolution();

    public PyhPoint getMousePosition();

    public void moveMouseAbsolute(int x, int y);

    public void moveMouseRelative(int dx, int dy);

    public void clickLeftMouseButton();

    public void clickMiddleMouseButton();

    public void clickRightMouseButton();

    public void scrollMouseUp(int amount);

    public void scrollMouseDown(int amount);

    // TODO: scrolling, pressing keys

    // TODO: other PC commands, like booting a program, etc.

    // TODO: a way to get info on current running system? (mouse position, open programs, etc)

}
