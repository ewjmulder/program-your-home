package com.programyourhome.pcinstructor.model;

public interface KeyPress {

    /**
     * The key to press.
     *
     * @return the key
     */
    public Key getKey();

    /**
     * The amount of milliseconds that the key should be pressed.
     * If not specified (null), a sensible default (short) value will be used.
     *
     * @return the millis
     */
    public Integer getMillis();

    /**
     * Whether of not the shift key is pressed simultaneously.
     *
     * @return hold shift or not
     */
    public boolean isShift();

    /**
     * Whether of not the control (ctrl) key is pressed simultaneously.
     *
     * @return hold control or not
     */
    public boolean isControl();

    /**
     * Whether of not the alt key is pressed simultaneously.
     *
     * @return hold alt or not
     */
    public boolean isAlt();

    /**
     * Whether of not the 'super' key is pressed simultaneously.
     * Also known as the Windows key or Command key (Mac).
     *
     * @return hold super or not
     */
    public boolean isSuper();

}
