package com.programyourhome.pc;

public interface PcInstructor {

    public void moveMouseAbsolute(int x, int y);

    public void moveMouseRelative(int dx, int dy);

    public void clickLeftMouseButton();

    public void clickRightMouseButton();

    public void clickMiddleMouseButton();

    // TODO: scrolling, pressing keys

    // TODO: other PC commands, like booting a program, etc.

    // TODO: a way to get info on current running system? (mouse position, open programs, etc)

}
