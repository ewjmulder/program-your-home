package nl.ewjmulder.smarthome.ir;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

public class Main {

	// https://github.com/malyn/jnaplatext/blob/master/src/main/java/com/michaelalynmiller/jnaplatext/win32/WinUser.java
	public static final int WM_COPYDATA = 0x4A;

	public interface MyUser32 extends User32 {
		MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("User32", MyUser32.class);

		HWND FindWindowA(String className, String windowName);

		LRESULT SendMessageA(HWND hWnd, int msg, WPARAM wParam, COPYDATASTRUCT lParam);
	}

	public static void main(String[] args) {
		String command = "Samsung_BN59-00685A KEY_POWER 0";
		
		MyUser32 u32 = MyUser32.INSTANCE;
		System.out.println("u32: " + u32);
		HWND hwnd = u32.FindWindowA(null, "WinLIRC");
		System.out.println("hwnd: " + hwnd);
		COPYDATASTRUCT data = new COPYDATASTRUCT();
		data.dwData = new ULONG_PTR(0);
		data.cbData = command.length() + 1;
		Pointer commandPointer = new Memory(command.length() + 1);
		commandPointer.setString(0, command); 
		data.lpData = commandPointer;
		WinDef.LRESULT response = u32.SendMessageA(hwnd, WM_COPYDATA, null, data);

		System.out.println("reponse: " + response);
	}

	/**
	 * https://github.com/malyn/jnaplatext/blob/master/src/main/java/com/michaelalynmiller/jnaplatext/win32/WinUser.java
	 * 
	 * Contains data to be passed to another application by the WM_COPYDATA
	 * message.
	 */
	public static class COPYDATASTRUCT extends Structure {
		/**
		 * The by-reference version of this structure.
		 */
		public static class ByReference extends COPYDATASTRUCT implements
				Structure.ByReference {
		}

		/**
		 * Instantiates a new COPYDATASTRUCT.
		 */
		public COPYDATASTRUCT() {
		}

		/**
		 * Instantiates a new COPYDATASTRUCT with existing data given the
		 * address of that data.
		 *
		 * @param pointer
		 *            Address of the existing structure.
		 */
		public COPYDATASTRUCT(final long pointer) {
			this(new Pointer(pointer));
		}

		/**
		 * Instantiates a new COPYDATASTRUCT with existing data given a pointer
		 * to that data.
		 *
		 * @param memory
		 *            Pointer to the existing structure.
		 */
		public COPYDATASTRUCT(final Pointer memory) {
			super(memory);
			read();
		}

		/** The data to be passed to the receiving application. */
		public ULONG_PTR dwData;
		/**
		 * The size, in bytes, of the data pointed to by the lpData member.
		 */
		public int cbData;
		/**
		 * The data to be passed to the receiving application. This member can
		 * be null.
		 */
		public Pointer lpData;

		/**
		 * Returns the serialized order of this structure's fields.
		 *
		 * @return The serialized order of this structure's fields.
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		@Override
		protected final List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwData", "cbData", "lpData" });
		}
	}

}
