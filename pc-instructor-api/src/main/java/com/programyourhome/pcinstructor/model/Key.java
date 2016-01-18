package com.programyourhome.pcinstructor.model;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public enum Key {

    KEY_A('a', KeyEvent.VK_A),
    KEY_B('b', KeyEvent.VK_B),
    KEY_C('c', KeyEvent.VK_C),
    KEY_D('d', KeyEvent.VK_D),
    KEY_E('e', KeyEvent.VK_E),
    KEY_F('f', KeyEvent.VK_F),
    KEY_G('g', KeyEvent.VK_G),
    KEY_H('h', KeyEvent.VK_H),
    KEY_I('i', KeyEvent.VK_I),
    KEY_J('j', KeyEvent.VK_J),
    KEY_K('k', KeyEvent.VK_K),
    KEY_L('l', KeyEvent.VK_L),
    KEY_M('m', KeyEvent.VK_M),
    KEY_N('n', KeyEvent.VK_N),
    KEY_O('o', KeyEvent.VK_O),
    KEY_P('p', KeyEvent.VK_P),
    KEY_Q('q', KeyEvent.VK_Q),
    KEY_R('r', KeyEvent.VK_R),
    KEY_S('s', KeyEvent.VK_S),
    KEY_T('t', KeyEvent.VK_T),
    KEY_U('u', KeyEvent.VK_U),
    KEY_V('v', KeyEvent.VK_V),
    KEY_W('w', KeyEvent.VK_W),
    KEY_X('x', KeyEvent.VK_X),
    KEY_Y('y', KeyEvent.VK_Y),
    KEY_Z('z', KeyEvent.VK_Z),

    KEY_0('0', KeyEvent.VK_0),
    KEY_1('1', KeyEvent.VK_1),
    KEY_2('2', KeyEvent.VK_2),
    KEY_3('3', KeyEvent.VK_3),
    KEY_4('4', KeyEvent.VK_4),
    KEY_5('5', KeyEvent.VK_5),
    KEY_6('6', KeyEvent.VK_6),
    KEY_7('7', KeyEvent.VK_7),
    KEY_8('8', KeyEvent.VK_8),
    KEY_9('9', KeyEvent.VK_9),

    KEY_BACK_QUOTE('`', KeyEvent.VK_BACK_QUOTE),
    KEY_TILDE('~', KeyEvent.VK_BACK_QUOTE, true),

    KEY_EXCLAMATION_MARK('!', KeyEvent.VK_1, true), // VK_EXCLAMATION_MARK
    KEY_AT('@', KeyEvent.VK_2, true), // VK_AT
    KEY_POUND('#', KeyEvent.VK_3, true),
    KEY_DOLLAR('$', KeyEvent.VK_4, true), // VK_DOLLAR
    KEY_PERCENTAGE('%', KeyEvent.VK_5, true),
    KEY_CARAT('^', KeyEvent.VK_6, true),
    KEY_AMPERSAND('&', KeyEvent.VK_7, true), // VK_AMPERSAND
    KEY_ASTERISK('*', KeyEvent.VK_8, true), // VK_ASTERISK
    KEY_PARENTHESIS_OPEN('(', KeyEvent.VK_9, true), // VK_LEFT_PARENTHESIS
    KEY_PARENTHESIS_CLOSE(')', KeyEvent.VK_0, true), // VK_RIGHT_PARENTHESIS

    KEY_MINUS('-', KeyEvent.VK_MINUS),
    KEY_UNDERSCORE('_', KeyEvent.VK_MINUS, true), // VK_UNDERSCORE
    KEY_EQUALS('=', KeyEvent.VK_EQUALS),
    KEY_PLUS('+', KeyEvent.VK_EQUALS, true), // VK_PLUS
    KEY_BRACKET_OPEN('[', KeyEvent.VK_OPEN_BRACKET),
    KEY_CURLY_BRACKET_OPEN('{', KeyEvent.VK_OPEN_BRACKET, true), // VK_BRACELEFT
    KEY_BRACKET_CLOSE(']', KeyEvent.VK_CLOSE_BRACKET),
    KEY_CURLY_BRACKET_CLOSE('}', KeyEvent.VK_CLOSE_BRACKET, true), // VK_BRACERIGHT
    KEY_BACK_SLASH('\\', KeyEvent.VK_BACK_SLASH),
    KEY_PIPE('|', KeyEvent.VK_BACK_SLASH, true),
    KEY_SEMICOLON(';', KeyEvent.VK_SEMICOLON),
    KEY_COLON(':', KeyEvent.VK_SEMICOLON, true), // VK_COLON
    KEY_SINGLE_QUOTE('\'', KeyEvent.VK_QUOTE),
    KEY_DOUBLE_QUOTE('\"', KeyEvent.VK_QUOTE, true), // VK_QUOTEDBL
    KEY_COMMA(',', KeyEvent.VK_COMMA),
    KEY_LESS_THAN('<', KeyEvent.VK_COMMA, true), // VK_LESS
    KEY_PERIOD('.', KeyEvent.VK_PERIOD),
    KEY_GREATER_THAN('>', KeyEvent.VK_PERIOD, true), // VK_GREATER
    KEY_SLASH('/', KeyEvent.VK_SLASH),
    KEY_QUESTION_MARK('?', KeyEvent.VK_SLASH, true),
    KEY_SPACEBAR(' ', KeyEvent.VK_SPACE),

    KEY_F1(KeyEvent.VK_F1),
    KEY_F2(KeyEvent.VK_F2),
    KEY_F3(KeyEvent.VK_F3),
    KEY_F4(KeyEvent.VK_F4),
    KEY_F5(KeyEvent.VK_F5),
    KEY_F6(KeyEvent.VK_F6),
    KEY_F7(KeyEvent.VK_F7),
    KEY_F8(KeyEvent.VK_F8),
    KEY_F9(KeyEvent.VK_F9),
    KEY_F10(KeyEvent.VK_F10),
    KEY_F11(KeyEvent.VK_F11),
    KEY_F12(KeyEvent.VK_F12),
    KEY_F13(KeyEvent.VK_F13),
    KEY_F14(KeyEvent.VK_F14),
    KEY_F15(KeyEvent.VK_F15),
    KEY_F16(KeyEvent.VK_F16),
    KEY_F17(KeyEvent.VK_F17),
    KEY_F18(KeyEvent.VK_F18),
    KEY_F19(KeyEvent.VK_F19),
    KEY_F20(KeyEvent.VK_F20),
    KEY_F21(KeyEvent.VK_F21),
    KEY_F22(KeyEvent.VK_F22),
    KEY_F23(KeyEvent.VK_F23),
    KEY_F24(KeyEvent.VK_F24),

    KEY_ESCAPE(KeyEvent.VK_ESCAPE),
    KEY_TAB('\t', KeyEvent.VK_TAB),
    KEY_CAPS_LOCK(KeyEvent.VK_CAPS_LOCK),
    KEY_SHIFT(KeyEvent.VK_SHIFT),
    KEY_CONTROL(KeyEvent.VK_CONTROL),
    KEY_ALT(KeyEvent.VK_ALT),
    KEY_SUPER(KeyEvent.VK_WINDOWS),
    KEY_BACKSPACE(KeyEvent.VK_BACK_SPACE),
    KEY_ENTER('\n', KeyEvent.VK_ENTER),

    KEY_LEFT(KeyEvent.VK_LEFT),
    KEY_UP(KeyEvent.VK_UP),
    KEY_DOWN(KeyEvent.VK_DOWN),
    KEY_RIGHT(KeyEvent.VK_RIGHT),

    KEY_INSERT(KeyEvent.VK_INSERT),
    KEY_PRINT_SCREEN(KeyEvent.VK_PRINTSCREEN),
    KEY_DELETE(KeyEvent.VK_DELETE),
    KEY_PAGE_UP(KeyEvent.VK_PAGE_UP),
    KEY_PAGE_DOWN(KeyEvent.VK_PAGE_DOWN),
    KEY_HOME(KeyEvent.VK_HOME),
    KEY_END(KeyEvent.VK_END),
    KEY_NUM_LOCK(KeyEvent.VK_NUM_LOCK),
    KEY_SCROLL_LOCK(KeyEvent.VK_SCROLL_LOCK);

    private Character character;
    private int keyCode;
    private boolean shift;

    private Key(final int keyCode) {
        this(null, keyCode);
    }

    private Key(final Character character, final int keyCode) {
        this(character, keyCode, false);
    }

    private Key(final int keyCode, final boolean shift) {
        this(null, keyCode, shift);
    }

    private Key(final Character character, final int keyCode, final boolean shift) {
        this.character = character;
        this.keyCode = keyCode;
        this.shift = shift;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public boolean isShift() {
        return this.shift;
    }

    /**
     * Find the key that matches the given character.
     *
     * @param aCharacter the character of the key
     * @return the key or null if no match found
     */
    public static Key getKeyByCharacter(final char aCharacter) {
        return Arrays.stream(Key.values())
                .filter(key -> key.character != null && key.character == aCharacter)
                .findAny().orElse(null);
    }

}
