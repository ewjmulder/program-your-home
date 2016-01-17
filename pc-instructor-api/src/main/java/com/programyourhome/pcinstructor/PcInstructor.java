package com.programyourhome.pcinstructor;

import com.programyourhome.pcinstructor.model.KeyPress;
import com.programyourhome.pcinstructor.model.MouseButton;
import com.programyourhome.pcinstructor.model.MouseScroll;
import com.programyourhome.pcinstructor.model.PyhDimension;
import com.programyourhome.pcinstructor.model.PyhPoint;

public interface PcInstructor {

    public PyhDimension getScreenResolution();

    public PyhPoint getMousePosition();

    public void moveMouseAbsolute(int x, int y);

    public void moveMouseRelative(int dx, int dy);

    public void clickMouseButton(MouseButton mouseButton);

    public void clickLeftMouseButton();

    public void clickMiddleMouseButton();

    public void clickRightMouseButton();

    public void scrollMouse(MouseScroll mouseScroll);

    public void scrollMouseUp(int amount);

    public void scrollMouseDown(int amount);

    public void pressKey(KeyPress keyPress);

    // TODO: other PC commands, like booting a program, etc.

    // TODO: a way to get info on current running system? (open programs, etc)

}
