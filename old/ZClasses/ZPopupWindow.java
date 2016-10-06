package ZClasses;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class ZPopupWindow extends ZWindow {
	public ZPopupWindow(String name) {
		super(name);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		requestFocus();
	}
}
