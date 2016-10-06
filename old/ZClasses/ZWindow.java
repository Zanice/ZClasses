package ZClasses;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*	ABSTRACT CLASS ZWindow
 * 	@Zanice
 * 
 * 	This abstract class allows for easy creation of windows that
 * 	provide a visual interface for the application. The window
 * 	holds a ZDisplay, can have its label changed, and will properly
 * 	adjust its size for a desired size.
 */

@SuppressWarnings("serial")
public abstract class ZWindow extends JFrame implements ComponentListener {
	private static boolean set_offset_x = false;
	private static boolean set_offset_y = false;
	private static int offset_x = 0;
	private static int offset_y = 0;
	private int realx;
	private int realy;
	
	public ZWindow(String name) {
		//Configure the window.
		super(name);
		addComponentListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		setRealSize(500, 500);
		setVisible(false);
	}
	
	private void configureOffsets() {
		if ((getContentPane() == null)||((set_offset_x)&&(set_offset_y)))
			return;
		realx = 0;
		realy = 0;
		while ((!set_offset_x)||(!set_offset_y)) {
			setVisible(true);
			setSize(500, 500);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (getContentPane() != null) {
				realx = getContentPane().getWidth();
				setSize(500, 500);
				realy = getContentPane().getHeight();
			}
			else
				return;
			if (realx != 0) {
				offset_x = 500 - realx;
				set_offset_x = true;
			}
			if (realy != 0) {
				offset_y = 500 - realy;
				set_offset_y = true;
			}
		}
	}
	
	public Component add(Component c) {
		if (c instanceof ZDisplay) {
			Component result = super.add(c);
			((ZDisplay) c).setWindow(this);
			configureOffsets();
			ZDisplay display = (ZDisplay) c;
			setRealSize(display.getXDimension(), display.getYDimension());
			setMinimumSize(display.getXDimension(), display.getYDimension());
			setVisible(true);
			return result;
		}
		return super.add(c);
	}
	
	//Change the display object that the window holds.
	public void changeDisplay(ZDisplay display) {
		//If the window already has a display, remove it.
		for (Component c : getContentPane().getComponents()) {
			if (c instanceof JPanel)
				remove(c);
		}
		add(display);
		setRealSize(display.getXDimension(), display.getYDimension());
		display.onResize();
	}
	
	//Sets the size of the window, adding pixel buffers for the window frame.
	public void setRealSize(int x, int y) {
		setSize(x + offset_x, y + offset_y);
		getContentPane().repaint();
	}
	
	public void setMinimumSize(int x, int y) {
		int offsetx = set_offset_x ? offset_x : 0;
		int offsety = set_offset_y ? offset_y : 0;
		setMinimumSize(new Dimension(x + offsetx, y + offsety));
	}
	
	public void setMaximumSize(int x, int y) {
		int offsetx = set_offset_x ? offset_x : 0;
		int offsety = set_offset_y ? offset_y : 0;
		setMaximumSize(new Dimension(x + offsetx, y + offsety));
	}
	
	public int getRealWidth() {
		return set_offset_x ? getWidth() - offset_x : getWidth();
	}
	
	public int getRealHeight() {
		return set_offset_y ? getHeight() - offset_y : getHeight();
	}
	
	public int getCenterWidth() {
		return getRealWidth() / 2;
	}
	
	public int getCenterHeight() {
		return getRealHeight() / 2;
	}
	
	public void centerWindow() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (getSize().height / 2));
	}
	
	//Sets the label at the top of the window.
	public void setLabel(String s) {
		super.setTitle(s);
	}
	
	public void componentHidden(ComponentEvent event) {
		onHidden(event);
	}
	
	public void componentResized(ComponentEvent event) {
		for (Component c : getContentPane().getComponents()) {
			try {
				((ZDisplay) c).onResize();
			} catch (Exception e) {
				;
			}
		}
	}
	
	public void componentShown(ComponentEvent event) {
		onShown(event);
	}
	
	public void componentMoved(ComponentEvent event) {
		onMoved(event);
	}
	
	public abstract void onShown(ComponentEvent event);
	public abstract void onHidden(ComponentEvent event);
	public abstract void onMoved(ComponentEvent event);
}
