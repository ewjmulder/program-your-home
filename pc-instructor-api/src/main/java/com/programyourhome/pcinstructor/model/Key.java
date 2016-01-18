package com.programyourhome.pcinstructor.model;

import java.awt.event.KeyEvent;

public enum Key {

    KEY_A(KeyEvent.VK_A),
    KEY_B(KeyEvent.VK_B),
    KEY_C(KeyEvent.VK_C),
    KEY_D(KeyEvent.VK_D),
    KEY_E(KeyEvent.VK_E),
    KEY_F(KeyEvent.VK_F),
    KEY_G(KeyEvent.VK_G),
    KEY_H(KeyEvent.VK_H),
    KEY_I(KeyEvent.VK_I),
    KEY_J(KeyEvent.VK_J),
    KEY_K(KeyEvent.VK_K),
    KEY_L(KeyEvent.VK_L),
    KEY_M(KeyEvent.VK_M),
    KEY_N(KeyEvent.VK_N),
    KEY_O(KeyEvent.VK_O),
    KEY_P(KeyEvent.VK_P),
    KEY_Q(KeyEvent.VK_Q),
    KEY_R(KeyEvent.VK_R),
    KEY_S(KeyEvent.VK_S),
    KEY_T(KeyEvent.VK_T),
    KEY_U(KeyEvent.VK_U),
    KEY_V(KeyEvent.VK_V),
    KEY_W(KeyEvent.VK_W),
    KEY_X(KeyEvent.VK_X),
    KEY_Y(KeyEvent.VK_Y),
    KEY_Z(KeyEvent.VK_Z),
    
    KEY_0(KeyEvent.VK_0),
    KEY_1(KeyEvent.VK_1),
    KEY_2(KeyEvent.VK_2),
    KEY_3(KeyEvent.VK_3),
    KEY_4(KeyEvent.VK_4),
    KEY_5(KeyEvent.VK_5),
    KEY_6(KeyEvent.VK_6),
    KEY_7(KeyEvent.VK_7),
    KEY_8(KeyEvent.VK_8),
    KEY_9(KeyEvent.VK_9),

    KEY_TILDE(KeyEvent.VK_BACK_QUOTE, true),
    KEY_COMMA(KeyEvent.VK_COMMA),
    KEY_PERIOD(KeyEvent.VK_PERIOD),
    KEY_COLON(KeyEvent.VK_COLON),
    KEY_SEMICOLON(KeyEvent.VK_SEMICOLON),
    KEY_SINGLE_QUOTE(KeyEvent.VK_QUOTE),
    KEY_DOUBLE_QUOTE(KeyEvent.VK_QUOTEDBL),

    KEY_SPACEBAR(KeyEvent.VK_SPACE),
    KEY_BACKSPACE(KeyEvent.VK_BACK_SPACE),
    KEY_SUPER(KeyEvent.VK_WINDOWS);

    private int keyCode;
    private boolean shift;

    private Key(final int keyCode) {
        this(keyCode, false);
    }
    
    private Key(final int keyCode, final boolean shift) {
        this.keyCode = keyCode;
        this.shift = shift;
    }

    public int getKeyCode() {
        return this.keyCode;
    }
    
    public boolean isShift() {
        return shift;
    }

}
