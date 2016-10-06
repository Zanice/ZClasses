package ZClasses.Interface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class ZWindow extends JFrame implements ComponentListener {
	private static boolean buffer_x_is_set = false;
	private static boolean buffer_y_is_set = false;
	
	private static int buffer_x = 0;
	private static int buffer_y = 0;
	
	private int realx;
	private int realy;
	
	public ZWindow(String name) {
		super(name);
		
		// configure the window
		addComponentListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		setRealSize(500, 500);
		setVisible(false);
	}
	
	public void setLabel(String s) {
		super.setTitle(s);
	}
	
	public int getBufferedWidth() {
		return buffer_x_is_set ? getWidth() - buffer_x : getWidth();
	}
	
	public int getBufferedHeight() {
		return buffer_y_is_set ? getHeight() - buffer_y : getHeight();
	}
	
	public int getUnbufferedWidth() {
		return getWidth();
	}
	
	public int getUnbufferedHeight() {
		return getHeight();
	}
	
	public int getCenterWidth() {
		return getUnbufferedWidth() / 2;
	}
	
	public int getCenterHeight() {
		return getUnbufferedHeight() / 2;
	}
	
	public Component add(ZDisplay display) {
		// add the display as a component for the window
		Component result = super.add(display);
		
		// configure the display appropriately
		display.configure(this);
		configureOffsets();
		
		// assuming offsets set, set the size and minimum size (as size) of the window
		setRealSize(display.getDimensionX(), display.getDimensionY());
		setMinimumSize(display.getDimensionX(), display.getDimensionY());
		
		// confirm the window is visible
		setVisible(true);
		
		return result;
	}
	
	private void configureOffsets() {
		// return if there is no current display or if both buffers are already set
		if ((getContentPane() == null)||((buffer_x_is_set)&&(buffer_y_is_set)))
			return;
		
		// declare integer values for the true dimensions of the display
		realx = 0;
		realy = 0;
		
		// attempt to find the display buffers
		while ((!buffer_x_is_set)||(!buffer_y_is_set)) {
			// set the display's configuration for buffer determining
			setVisible(true);
			setSize(500, 500);
			
			// wait for the display to update
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// attempt to grab the actual dimensions as determined by the content pane
			if (getContentPane() != null) {
				realx = getContentPane().getWidth();
				setSize(500, 500);
				realy = getContentPane().getHeight();
			}
			else
				return;
			
			// if the dimensions were retrieved, find the respective buffer
			if (realx != 0) {
				buffer_x = 500 - realx;
				buffer_x_is_set = true;
			}
			if (realy != 0) {
				buffer_y = 500 - realy;
				buffer_y_is_set = true;
			}
		}
	}
	
	public void changeDisplay(ZDisplay display) {
		// if the window already has a display, remove it
		for (Component c : getContentPane().getComponents()) {
			if (c instanceof JPanel)
				remove(c);
		}
		
		// and the display and ensure it is sized properly
		add(display);
		setRealSize(display.getDimensionX(), display.getDimensionY());
		display.onResize();
	}
	
	//Sets the size of the window, adding pixel buffers for the window frame.
	public void setRealSize(int x, int y) {
		setSize(x + buffer_x, y + buffer_y);
		getContentPane().repaint();
	}
	
	public void setMinimumSize(int x, int y) {
		// determine if offsets can be applied
		int offsetx = buffer_x_is_set ? buffer_x : 0;
		int offsety = buffer_y_is_set ? buffer_y : 0;
		
		// call the super method with a Dimension representation
		setMinimumSize(new Dimension(x + offsetx, y + offsety));
	}
	
	public void setMaximumSize(int x, int y) {
		// determine if offsets can be applied
		int offsetx = buffer_x_is_set ? buffer_x : 0;
		int offsety = buffer_y_is_set ? buffer_y : 0;
		
		// call the super method with a Dimension representation
		setMaximumSize(new Dimension(x + offsetx, y + offsety));
	}
	
	public void centerWindow() {
		// use system information and component information to center the window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (getSize().height / 2));
	}
	
	public void componentResized(ComponentEvent event) {
		// relay the resize to any attached ZDisplay components
		for (Component c : getContentPane().getComponents()) {
			try {
				((ZDisplay) c).onResize();
			} catch (Exception e) {
				;
			}
		}
	}
	
	public void componentHidden(ComponentEvent event) {
		onHidden(event);
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
