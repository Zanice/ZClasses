package ZClasses.Interface;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ZKeyActionHandler implements KeyListener{
	private ZDisplay host;
	
	public ZKeyActionHandler() {
		super();
	}
	
	public ZDisplay getHost() {
		return host;
	}
	
	public void configure(ZDisplay host) {
		this.host = host;
	}
	
	@Override
	public void keyPressed(KeyEvent ke) {
		host.onKeyEvent(true, ke.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		host.onKeyEvent(false, ke.getKeyCode());
	}
	
	public void keyTyped(KeyEvent ke) {}
}

