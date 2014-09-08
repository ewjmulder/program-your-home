package nl.ewjmulder.smarthome.hue;

public class MyThread extends Thread {

	@Override
	public void run() {
		while (true) {
			//System.out.println("Hoi thread!");
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
		}
	}
	
}
